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
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.error.IError;
import com.helger.commons.error.list.ErrorList;
import com.helger.commons.state.ESuccess;
import com.helger.commons.string.StringHelper;
import com.helger.peppol.reporting.eusr.EndUserStatisticsReportValidator;
import com.helger.peppol.reporting.jaxb.eusr.EndUserStatisticsReport110Marshaller;
import com.helger.peppol.reporting.jaxb.eusr.v110.EndUserStatisticsReportType;
import com.helger.peppol.reporting.jaxb.tsr.TransactionStatisticsReport101Marshaller;
import com.helger.peppol.reporting.jaxb.tsr.v101.TransactionStatisticsReportType;
import com.helger.peppol.reporting.tsr.TransactionStatisticsReportValidator;
import com.helger.peppol.reportingsupport.domain.PeppolReportData;
import com.helger.peppol.reportingsupport.domain.PeppolReportSendingReportData;
import com.helger.peppolid.IDocumentTypeIdentifier;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.peppolid.peppol.doctype.EPredefinedDocumentTypeIdentifier;
import com.helger.peppolid.peppol.process.EPredefinedProcessIdentifier;
import com.helger.schematron.svrl.SVRLFailedAssert;
import com.helger.schematron.svrl.SVRLHelper;
import com.helger.schematron.svrl.jaxb.SchematronOutputType;
import com.helger.xml.transform.TransformSourceFactory;

public final class PeppolReportingSupport
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolReportingSupport.class);

  private final IPeppolReportingStorage m_aStorage;
  private Locale m_aDisplayLocale = Locale.ROOT;
  private Consumer <? super String> m_aWarnHdl = LOGGER::warn;
  private BiConsumer <? super String, ? super Exception> m_aErrorHdl = LOGGER::error;

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
  public PeppolReportingSupport setWarningHandler (@Nonnull final Consumer <? super String> aWarnHdl)
  {
    ValueEnforcer.notNull (aWarnHdl, "WarnHdl");
    m_aWarnHdl = aWarnHdl;
    return this;
  }

  @Nonnull
  public PeppolReportingSupport setErrorHandler (@Nonnull final BiConsumer <? super String, ? super Exception> aErrorHdl)
  {
    ValueEnforcer.notNull (aErrorHdl, "ErrorHdl");
    m_aErrorHdl = aErrorHdl;
    return this;
  }

  /**
   * Validate and store a Peppol Transaction Statistics Report.
   *
   * @param aTSR
   *        The TSR to be stored. May not be <code>null</code>.
   * @param aTSRStringConsumer
   *        The consumer to be invoked on the serialized report. May not be <code>null</code>.
   * @return {@link ESuccess#SUCCESS} only if the XML serialization, the Schematron validation and
   *         the storage of it where successful.
   */
  @Nonnull
  public ESuccess validateAndStorePeppolTSR (@Nonnull final TransactionStatisticsReportType aTSR,
                                             @Nonnull final Consumer <String> aTSRStringConsumer)
  {
    ValueEnforcer.notNull (aTSR, "TSR");
    ValueEnforcer.notNull (aTSRStringConsumer, "TSRStringConsumer");

    final YearMonth aYearMonth = YearMonth.of (aTSR.getHeader ().getReportPeriod ().getStartDate ().getYear (),
                                               aTSR.getHeader ().getReportPeriod ().getStartDate ().getMonth ());
    ESuccess eReportSuccessState = ESuccess.SUCCESS;
    final LocalDateTime aReportCreationDT = PDTFactory.getCurrentLocalDateTime ();

    // Convert to XML
    final ErrorList aErrorList = new ErrorList ();
    final String sTSR = new TransactionStatisticsReport101Marshaller ().setCollectErrors (aErrorList)
                                                                       .setFormattedOutput (true)
                                                                       .getAsString (aTSR);
    for (final IError aError : aErrorList)
      if (aError.isError ())
        m_aErrorHdl.accept ("TSR XSD error: " + aError.getAsString (m_aDisplayLocale), null);
      else
        m_aWarnHdl.accept ("TSR XSD warning: " + aError.getAsString (m_aDisplayLocale));

    if (StringHelper.hasNoText (sTSR))
      return ESuccess.FAILURE;

    // Call callback to avoid double serialization
    aTSRStringConsumer.accept (sTSR);

    // Validate if TSR is correct or not
    LOGGER.info ("Starting TSR " + aYearMonth + " Schematron validation");
    try
    {
      final SchematronOutputType aSVRL = TransactionStatisticsReportValidator.getSchematronTSR_1 ()
                                                                             .applySchematronValidationToSVRL (TransformSourceFactory.create (sTSR));
      final ICommonsList <SVRLFailedAssert> aFailedAsserts = SVRLHelper.getAllFailedAssertions (aSVRL);
      int nErrors = 0;
      for (final SVRLFailedAssert aFailedAssert : aFailedAsserts)
      {
        final String sErrText = aFailedAssert.getAsResourceError ("in-memory").getAsString (m_aDisplayLocale);
        if (aFailedAssert.getFlag ().isError ())
        {
          nErrors++;
          m_aErrorHdl.accept ("TSR Schematron error: " + sErrText, null);
        }
        else
        {
          m_aWarnHdl.accept ("TSR Schematron warning: " + sErrText);
        }
      }

      if (nErrors > 0)
      {
        // Overwrite Status
        eReportSuccessState = ESuccess.FAILURE;
      }
    }
    catch (final Exception ex)
    {
      LOGGER.error ("Error in TSR " + aYearMonth + " Schematron validation", ex);
      m_aErrorHdl.accept ("Error in TSR Schematron validation", ex);

      // Overwrite Status
      eReportSuccessState = ESuccess.FAILURE;
    }

    // Finally store in storage
    LOGGER.info ("Now storing TSR " + aYearMonth + " in state " + eReportSuccessState);
    final PeppolReportData aReportData = new PeppolReportData (EPeppolReportType.TSR_V10,
                                                               aYearMonth,
                                                               aReportCreationDT,
                                                               sTSR,
                                                               eReportSuccessState.isSuccess ());
    if (m_aStorage.storePeppolReport (aReportData).isFailure ())
    {
      m_aErrorHdl.accept ("Error storing TSR " + aYearMonth, null);
      return ESuccess.FAILURE;
    }

    // Return SUCCESS only if everything was successful
    return eReportSuccessState;
  }

  /**
   * Validate and store a Peppol End User Statistics Report.
   *
   * @param aEUSR
   *        The EUSR to be stored. May not be <code>null</code>.
   * @param aEUSRStringConsumer
   *        The consumer to be invoked on the serialized report. May not be <code>null</code>.
   * @return {@link ESuccess#SUCCESS} only if the XML serialization, the Schematron validation and
   *         the storage of it where successful.
   */
  @Nonnull
  public ESuccess validateAndStorePeppolEUSR (@Nonnull final EndUserStatisticsReportType aEUSR,
                                              @Nonnull final Consumer <String> aEUSRStringConsumer)
  {
    ValueEnforcer.notNull (aEUSR, "EUSR");
    ValueEnforcer.notNull (aEUSRStringConsumer, "EUSRByteConsumer");

    final YearMonth aYearMonth = YearMonth.of (aEUSR.getHeader ().getReportPeriod ().getStartDate ().getYear (),
                                               aEUSR.getHeader ().getReportPeriod ().getStartDate ().getMonth ());
    ESuccess eReportSuccessState = ESuccess.SUCCESS;
    final LocalDateTime aReportCreationDT = PDTFactory.getCurrentLocalDateTime ();

    // Convert to XML
    final ErrorList aErrorList = new ErrorList ();
    final String sEUSR = new EndUserStatisticsReport110Marshaller ().setCollectErrors (aErrorList)
                                                                    .setFormattedOutput (true)
                                                                    .getAsString (aEUSR);
    for (final IError aError : aErrorList)
      if (aError.isError ())
        m_aErrorHdl.accept ("EUSR XSD error: " + aError.getAsString (m_aDisplayLocale), null);
      else
        m_aWarnHdl.accept ("EUSR XSD warning: " + aError.getAsString (m_aDisplayLocale));

    if (StringHelper.isEmpty (sEUSR))
      return ESuccess.FAILURE;

    // Call callback to avoid double serialization
    aEUSRStringConsumer.accept (sEUSR);

    // Validate if EUSR is correct or not
    LOGGER.info ("Starting EUSR " + aYearMonth + " Schematron validation");
    try
    {
      final SchematronOutputType aSVRL = EndUserStatisticsReportValidator.getSchematronEUSR_1 ()
                                                                         .applySchematronValidationToSVRL (TransformSourceFactory.create (sEUSR));
      final ICommonsList <SVRLFailedAssert> aFailedAsserts = SVRLHelper.getAllFailedAssertions (aSVRL);
      int nErrors = 0;
      for (final SVRLFailedAssert aFailedAssert : aFailedAsserts)
      {
        final String sErrText = aFailedAssert.getAsResourceError ("in-memory").getAsString (m_aDisplayLocale);
        if (aFailedAssert.getFlag ().isError ())
        {
          nErrors++;
          m_aErrorHdl.accept ("EUSR Schematron error: " + sErrText, null);
        }
        else
        {
          m_aWarnHdl.accept ("EUSR Schematron warning: " + sErrText);
        }
      }

      if (nErrors > 0)
      {
        // Overwrite Status
        eReportSuccessState = ESuccess.FAILURE;
      }
    }
    catch (final Exception ex)
    {
      LOGGER.error ("Error in EUSR " + aYearMonth + " Schematron validation", ex);
      m_aErrorHdl.accept ("Error in EUSR Schematron validation", ex);

      // Overwrite Status
      eReportSuccessState = ESuccess.FAILURE;
    }

    // Finally store in storage
    LOGGER.info ("Now storing EUSR " + aYearMonth + " in state " + eReportSuccessState);
    final PeppolReportData aReportData = new PeppolReportData (EPeppolReportType.EUSR_V11,
                                                               aYearMonth,
                                                               aReportCreationDT,
                                                               sEUSR,
                                                               eReportSuccessState.isSuccess ());
    if (m_aStorage.storePeppolReport (aReportData).isFailure ())
    {
      m_aErrorHdl.accept ("Error storing EUSR " + aYearMonth, null);
      return ESuccess.FAILURE;
    }

    return eReportSuccessState;
  }

  /**
   * Send a Peppol Report to OpenPeppol.
   *
   * @param aYearMonth
   *        The year and month for which the report was created. May not be <code>null</code>.
   * @param eReportType
   *        The Peppol Reporting report type. May not be <code>null</code>.
   * @param aReportPayload
   *        The Peppol Reporting report in an XML serialized way. May neither be <code>null</code>
   *        nor empty.
   * @param aMainPeppolSender
   *        The callback interface that performs the actual Peppol sending and returns a sending
   *        report.
   * @return {@link ESuccess#SUCCESS} only if the Peppol AS4 sending and the storage where
   *         successful.
   */
  @Nonnull
  public ESuccess sendPeppolReport (@Nonnull final YearMonth aYearMonth,
                                    @Nonnull final EPeppolReportType eReportType,
                                    @Nonnull @Nonempty final byte [] aReportPayload,
                                    @Nonnull final IPeppolReportSenderCallback aMainPeppolSender)
  {
    ValueEnforcer.notNull (aYearMonth, "YearMonth");
    ValueEnforcer.notNull (eReportType, "ReportType");
    ValueEnforcer.notEmpty (aReportPayload, "ReportPayload");
    ValueEnforcer.notNull (aMainPeppolSender, "MainPeppolSender");

    // Determine parameters
    final IDocumentTypeIdentifier aDocTypeID;
    final IProcessIdentifier aProcessID;
    switch (eReportType)
    {
      case TSR_V10:
        aDocTypeID = EPredefinedDocumentTypeIdentifier.TRANSACTIONSTATISTICSREPORT_FDC_PEPPOL_EU_EDEC_TRNS_TRANSACTION_STATISTICS_REPORTING_1_0;
        aProcessID = EPredefinedProcessIdentifier.urn_fdc_peppol_eu_edec_bis_reporting_1_0;
        break;
      case EUSR_V11:
        aDocTypeID = EPredefinedDocumentTypeIdentifier.ENDUSERSTATISTICSREPORT_FDC_PEPPOL_EU_EDEC_TRNS_END_USER_STATISTICS_REPORT_1_1;
        aProcessID = EPredefinedProcessIdentifier.urn_fdc_peppol_eu_edec_bis_reporting_1_0;
        break;
      default:
        throw new IllegalStateException ("Unsupported Peppol report type " + eReportType);
    }

    final LocalDateTime aSendingDT = PDTFactory.getCurrentLocalDateTime ();
    final String sSendingReport;
    try
    {
      // This is the callback that triggers the external sending and requests back a sending report
      LOGGER.info ("Now sending Peppol Report " + eReportType + " for " + aYearMonth + " via Peppol Network");
      sSendingReport = aMainPeppolSender.sendPeppolMessage (aDocTypeID, aProcessID, aReportPayload);
    }
    catch (final Exception ex)
    {
      final String sMsg = "Failed to send Peppol Report " +
                          eReportType +
                          " for " +
                          aYearMonth +
                          " via the Peppol Network";
      m_aErrorHdl.accept (sMsg, ex);
      return ESuccess.FAILURE;
    }

    // Finally store in storage
    LOGGER.info ("Now storing sending report of " + eReportType + " for " + aYearMonth);
    final PeppolReportSendingReportData aSendingReportData = new PeppolReportSendingReportData (eReportType,
                                                                                                aYearMonth,
                                                                                                aSendingDT,
                                                                                                sSendingReport);
    if (m_aStorage.storePeppolReportSendingReport (aSendingReportData).isFailure ())
    {
      m_aErrorHdl.accept ("Error storing sending report of " + eReportType + " for " + aYearMonth, null);
      return ESuccess.FAILURE;
    }

    return ESuccess.SUCCESS;
  }
}
