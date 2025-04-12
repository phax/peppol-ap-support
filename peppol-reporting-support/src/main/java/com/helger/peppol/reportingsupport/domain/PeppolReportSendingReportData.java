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
package com.helger.peppol.reportingsupport.domain;

import java.time.LocalDateTime;
import java.time.YearMonth;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.datetime.PDTWebDateHelper;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.peppol.reportingsupport.EPeppolReportType;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;

/**
 * Contains the relevant data for a single Peppol Network Report Sending Report.
 *
 * @author Philip Helger
 */
@Immutable
public class PeppolReportSendingReportData
{
  private final EPeppolReportType m_eReportType;
  private final YearMonth m_aReportPeriod;
  private final LocalDateTime m_aReportCreationDT;
  private final String m_sSendingReportContent;

  /**
   * Constructor
   *
   * @param eReportType
   *        The report type. May not be <code>null</code>.
   * @param aReportPeriod
   *        The year and month for which the report was created. May not be <code>null</code>.
   * @param aReportCreationDT
   *        The date and time, when the report was created. May not be <code>null</code>.
   * @param sSendingReportContent
   *        The effective sending report content in any syntax. May be <code>null</code>.
   */
  public PeppolReportSendingReportData (@Nonnull final EPeppolReportType eReportType,
                                        @Nonnull final YearMonth aReportPeriod,
                                        @Nonnull final LocalDateTime aReportCreationDT,
                                        @Nullable final String sSendingReportContent)
  {
    ValueEnforcer.notNull (eReportType, "ReportType");
    ValueEnforcer.notNull (aReportPeriod, "ReportPeriod");
    ValueEnforcer.notNull (aReportCreationDT, "ReportCreationDT");

    m_eReportType = eReportType;
    m_aReportPeriod = aReportPeriod;
    m_aReportCreationDT = aReportCreationDT;
    m_sSendingReportContent = sSendingReportContent;
  }

  /**
   * Get the report type
   *
   * @return The report type. Never <code>null</code>.
   */
  @Nonnull
  public final EPeppolReportType getReportType ()
  {
    return m_eReportType;
  }

  /**
   * Get the report period
   *
   * @return The report period. Never <code>null</code>.
   */
  @Nonnull
  public final YearMonth getReportPeriod ()
  {
    return m_aReportPeriod;
  }

  /**
   * Get the report creation date and time (with millisecond precision)
   *
   * @return The report creation date and time. Never <code>null</code>.
   */
  @Nonnull
  public final LocalDateTime getReportCreationDT ()
  {
    return m_aReportCreationDT;
  }

  /**
   * CHeck if a sending report is present
   *
   * @return <code>true</code> if a sending report is present, <code>false</code> if not.
   */
  public final boolean hasSendingReportContent ()
  {
    return StringHelper.hasText (m_sSendingReportContent);
  }

  /**
   * Get the sending report content
   *
   * @return The sending report content. May be <code>null</code>.
   */
  @Nullable
  public final String getSendingReportContent ()
  {
    return m_sSendingReportContent;
  }

  /**
   * Get an XML element representation
   *
   * @param sNamespaceURI
   *        The XML namespace URI to use. May be <code>null</code>.
   * @param sElementName
   *        The XML element name to use for response root element. May neither be <code>null</code>
   *        nor empty.
   * @return The XML element and never <code>null</code>.
   */
  @Nonnull
  public IMicroElement getAsMicroElement (@Nullable final String sNamespaceURI,
                                          @Nonnull @Nonempty final String sElementName)
  {
    ValueEnforcer.notEmpty (sElementName, "ElementName");

    final IMicroElement ret = new MicroElement (sNamespaceURI, sElementName);
    ret.appendElement (sNamespaceURI, "ReportType").appendText (m_eReportType.getID ());
    ret.appendElement (sNamespaceURI, "ReportYear").appendText (m_aReportPeriod.getYear ());
    ret.appendElement (sNamespaceURI, "ReportType").appendText (m_aReportPeriod.getMonthValue ());
    ret.appendElement (sNamespaceURI, "ReportCreationDT")
       .appendText (PDTWebDateHelper.getAsStringXSD (m_aReportCreationDT));
    ret.appendElement (sNamespaceURI, "SendingReport").appendText (m_sSendingReportContent);
    return ret;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !o.getClass ().equals (getClass ()))
      return false;

    final PeppolReportSendingReportData rhs = (PeppolReportSendingReportData) o;
    return m_eReportType.equals (rhs.m_eReportType) &&
           m_aReportPeriod.equals (rhs.m_aReportPeriod) &&
           m_aReportCreationDT.equals (rhs.m_aReportCreationDT) &&
           EqualsHelper.equals (m_sSendingReportContent, rhs.m_sSendingReportContent);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_eReportType)
                                       .append (m_aReportPeriod)
                                       .append (m_aReportCreationDT)
                                       .append (m_sSendingReportContent)
                                       .getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ReportType", m_eReportType)
                                       .append ("ReportPeriod", m_aReportPeriod)
                                       .append ("ReportCreationDT", m_aReportCreationDT)
                                       .append ("SendingReportContent", m_sSendingReportContent)
                                       .getToString ();
  }
}
