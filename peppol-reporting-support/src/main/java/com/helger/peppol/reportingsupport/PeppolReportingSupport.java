/*
 * Copyright (C) 2025 Philip Helger
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.peppol.reportingsupport;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Locale;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.error.IError;
import com.helger.commons.error.list.ErrorList;
import com.helger.commons.state.ESuccess;
import com.helger.peppol.reporting.jaxb.tsr.TransactionStatisticsReport101Marshaller;
import com.helger.peppol.reporting.jaxb.tsr.v101.TransactionStatisticsReportType;
import com.helger.peppol.reporting.tsr.TransactionStatisticsReportValidator;
import com.helger.schematron.svrl.SVRLFailedAssert;
import com.helger.schematron.svrl.SVRLHelper;
import com.helger.schematron.svrl.jaxb.SchematronOutputType;
import com.helger.xml.transform.TransformSourceFactory;

public final class PeppolReportingSupport
{

  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolReportingSupport.class);

  private final IPeppolReportingStorage m_aStorage;
  private Locale m_aDisplayLocale = Locale.ROOT;
  private Consumer <? super String> m_aUIWarnHdl = LOGGER::warn;
  private Consumer <? super String> m_aUIErrorHdl = LOGGER::error;

  /**
   * Constructor
   *
   * @param aStorage
   *        The storage backend handler. May not be <code>null</code>.
   */
  public PeppolReportingSupport (@Nonnull final IPeppolReportingStorage aStorage)
  {
    ValueEnforcer.notNull (aStorage, "Storage");
    m_aStorage = aStorage;
  }

  @Nonnull
  public Locale getDisplayLocale ()
  {
    return m_aDisplayLocale;
  }

  @Nonnull
  public PeppolReportingSupport setDisplayLocale (@Nonnull final Locale aDisplayLocale)
  {
    ValueEnforcer.notNull (aDisplayLocale, "DisplayLocale");
    m_aDisplayLocale = aDisplayLocale;
    return this;
  }

  @Nonnull
  public PeppolReportingSupport setUIWarningHandler (@Nonnull final Consumer <? super String> aUIWarnHdl)
  {
    ValueEnforcer.notNull (aUIWarnHdl, "UIWarnHdl");
    m_aUIWarnHdl = aUIWarnHdl;
    return this;
  }

  @Nonnull
  public PeppolReportingSupport setUIErrorHandler (@Nonnull final Consumer <? super String> aUIErrorHdl)
  {
    ValueEnforcer.notNull (aUIErrorHdl, "UIErrorHdl");
    m_aUIErrorHdl = aUIErrorHdl;
    return this;
  }

  @Nonnull
  public ESuccess validateAndStorePeppolTSR (@Nonnull final TransactionStatisticsReportType aTSR,
                                             @Nonnull final LocalDateTime aReportCreationDT)
  {
    ValueEnforcer.notNull (aTSR, "TSR");
    ValueEnforcer.notNull (aReportCreationDT, "ReportCreationDT");

    final YearMonth aYearMonth = YearMonth.of (aTSR.getHeader ().getReportPeriod ().getStartDate ().getYear (),
                                               aTSR.getHeader ().getReportPeriod ().getStartDate ().getMonth ());
    ESuccess eReportSuccess = ESuccess.SUCCESS;

    // Convert to XML
    final ErrorList aErrorList = new ErrorList ();
    final byte [] aTSRBytes = new TransactionStatisticsReport101Marshaller ().setCollectErrors (aErrorList)
                                                                             .setFormattedOutput (true)
                                                                             .getAsBytes (aTSR);
    for (final IError aError : aErrorList)
      if (aError.isError ())
        m_aUIErrorHdl.accept ("Error: " + aError.getAsString (m_aDisplayLocale));
      else
        m_aUIWarnHdl.accept ("Warning: " + aError.getAsString (m_aDisplayLocale));

    if (aTSRBytes != null)
    {
      // Validate if TSR is correct or not
      LOGGER.info ("Starting TSR " + aYearMonth + " Schematron validation");
      try
      {
        final SchematronOutputType aSVRL = TransactionStatisticsReportValidator.getSchematronTSR_1 ()
                                                                               .applySchematronValidationToSVRL (TransformSourceFactory.create (aTSRBytes));
        final ICommonsList <SVRLFailedAssert> aFailedAsserts = SVRLHelper.getAllFailedAssertions (aSVRL);
        int nErrors = 0;
        for (final SVRLFailedAssert aFailedAssert : aFailedAsserts)
        {
          final String sErrText = aFailedAssert.getAsResourceError ("in-memory").getAsString (m_aDisplayLocale);
          if (aFailedAssert.getFlag ().isError ())
          {
            nErrors++;
            m_aUIErrorHdl.accept ("Schematron error: " + sErrText);
          }
          else
          {
            m_aUIWarnHdl.accept ("Schematron warning: " + sErrText);
          }
        }

        if (nErrors > 0)
        {
          // Overwrite Status
          eReportSuccess = ESuccess.FAILURE;
        }
      }
      catch (final Exception ex)
      {
        LOGGER.error ("Error in TSR " + aYearMonth + " Schematron validation", ex);
        m_aUIErrorHdl.accept ("Error in TSR Schematron validation. Technical details: " +
                              ex.getClass ().getName () +
                              " - " +
                              ex.getMessage ());

        // Overwrite Status
        eReportSuccess = ESuccess.FAILURE;
      }

      // Finally store in storage
      LOGGER.info ("Now storing TSR " + aYearMonth + " with success " + eReportSuccess);
      if (m_aStorage.storePeppolReport (EPeppolReportType.TSR_V1,
                                        aYearMonth,
                                        aReportCreationDT,
                                        aTSRBytes,
                                        eReportSuccess).isFailure ())
        m_aUIErrorHdl.accept ("Error storing TSR " + aYearMonth);
    }

    return ESuccess.SUCCESS;
  }
}
