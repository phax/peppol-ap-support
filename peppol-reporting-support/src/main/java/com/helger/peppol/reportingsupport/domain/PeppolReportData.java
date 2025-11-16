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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.datetime.web.PDTWebDateHelper;
import com.helger.peppol.reportingsupport.EPeppolReportType;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;

/**
 * Contains the relevant data for a single Peppol Network Report.
 *
 * @author Philip Helger
 */
@Immutable
public class PeppolReportData
{
  private final EPeppolReportType m_eReportType;
  private final YearMonth m_aReportPeriod;
  private final LocalDateTime m_aReportCreationDT;
  private final String m_sReportXML;
  private final boolean m_bReportValid;

  /**
   * Constructor
   *
   * @param eReportType
   *        The report type. May not be <code>null</code>.
   * @param aReportPeriod
   *        The year and month for which the report was created. May not be <code>null</code>.
   * @param aReportCreationDT
   *        The date and time, when the report was created. May not be <code>null</code>.
   * @param sReportXML
   *        The effective bytes of the report XML. Usually UTF-8 encoded. May neither be
   *        <code>null</code> nor empty.
   * @param bReportValid
   *        The indicator, if the report was valid or not.
   */
  public PeppolReportData (@NonNull final EPeppolReportType eReportType,
                           @NonNull final YearMonth aReportPeriod,
                           @NonNull final LocalDateTime aReportCreationDT,
                           @NonNull @Nonempty final String sReportXML,
                           final boolean bReportValid)
  {
    ValueEnforcer.notNull (eReportType, "ReportType");
    ValueEnforcer.notNull (aReportPeriod, "ReportPeriod");
    ValueEnforcer.notNull (aReportCreationDT, "ReportCreationDT");
    ValueEnforcer.notEmpty (sReportXML, "ReportXMLBytes");

    m_eReportType = eReportType;
    m_aReportPeriod = aReportPeriod;
    m_aReportCreationDT = aReportCreationDT;
    m_sReportXML = sReportXML;
    m_bReportValid = bReportValid;
  }

  /**
   * Get the report type
   *
   * @return The report type. Never <code>null</code>.
   */
  @NonNull
  public final EPeppolReportType getReportType ()
  {
    return m_eReportType;
  }

  /**
   * Get the report period
   *
   * @return The report period. Never <code>null</code>.
   */
  @NonNull
  public final YearMonth getReportPeriod ()
  {
    return m_aReportPeriod;
  }

  /**
   * Get the report creation date and time (with millisecond precision)
   *
   * @return The report creation date and time. Never <code>null</code>.
   */
  @NonNull
  public final LocalDateTime getReportCreationDT ()
  {
    return m_aReportCreationDT;
  }

  /**
   * Get the created report XML string
   *
   * @return The created Report XML as string. Never <code>null</code>.
   */
  @NonNull
  @Nonempty
  public final String getReportXMLString ()
  {
    return m_sReportXML;
  }

  /**
   * Is the report valid according to XSD and Schematron?
   *
   * @return <code>true</code> if the report is technically valid, <code>false</code> otherwise.
   */
  public final boolean isReportValid ()
  {
    return m_bReportValid;
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
  @NonNull
  public IMicroElement getAsMicroElement (@Nullable final String sNamespaceURI,
                                          @NonNull @Nonempty final String sElementName)
  {
    ValueEnforcer.notEmpty (sElementName, "ElementName");

    final IMicroElement ret = new MicroElement (sNamespaceURI, sElementName);
    ret.addElementNS (sNamespaceURI, "ReportType").addText (m_eReportType.getID ());
    ret.addElementNS (sNamespaceURI, "ReportYear").addText (m_aReportPeriod.getYear ());
    ret.addElementNS (sNamespaceURI, "ReportType").addText (m_aReportPeriod.getMonthValue ());
    ret.addElementNS (sNamespaceURI, "ReportCreationDT")
       .addText (PDTWebDateHelper.getAsStringXSD (m_aReportCreationDT));

    final IMicroElement eReport = ret.addElementNS (sNamespaceURI, "ReportXML");
    eReport.addText (m_sReportXML);
    eReport.setAttribute ("valid", m_bReportValid);
    return ret;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !o.getClass ().equals (getClass ()))
      return false;

    final PeppolReportData rhs = (PeppolReportData) o;
    return m_eReportType.equals (rhs.m_eReportType) &&
           m_aReportPeriod.equals (rhs.m_aReportPeriod) &&
           m_aReportCreationDT.equals (rhs.m_aReportCreationDT) &&
           m_sReportXML.equals (rhs.m_sReportXML) &&
           m_bReportValid == rhs.m_bReportValid;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_eReportType)
                                       .append (m_aReportPeriod)
                                       .append (m_aReportCreationDT)
                                       .append (m_sReportXML)
                                       .append (m_bReportValid)
                                       .getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ReportType", m_eReportType)
                                       .append ("ReportPeriod", m_aReportPeriod)
                                       .append ("ReportCreationDT", m_aReportCreationDT)
                                       .append ("ReportXML", m_sReportXML)
                                       .append ("ReportValid", m_bReportValid)
                                       .getToString ();
  }
}
