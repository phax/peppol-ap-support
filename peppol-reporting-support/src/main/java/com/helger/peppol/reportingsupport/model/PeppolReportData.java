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
package com.helger.peppol.reportingsupport.model;

import java.time.LocalDateTime;
import java.time.YearMonth;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.io.ByteArrayWrapper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.peppol.reportingsupport.EPeppolReportType;

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
  private final ByteArrayWrapper m_aReportXMLBytes;
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
   * @param aReportXMLBytes
   *        The effective bytes of the report XML. Usually UTF-8 encoded. May neither be
   *        <code>null</code> nor empty.
   * @param bReportValid
   *        The indicator, if the report was valid or not.
   */
  public PeppolReportData (@Nonnull final EPeppolReportType eReportType,
                           @Nonnull final YearMonth aReportPeriod,
                           @Nonnull final LocalDateTime aReportCreationDT,
                           @Nonnull @Nonempty final ByteArrayWrapper aReportXMLBytes,
                           final boolean bReportValid)
  {
    ValueEnforcer.notNull (eReportType, "ReportType");
    ValueEnforcer.notNull (aReportPeriod, "ReportPeriod");
    ValueEnforcer.notNull (aReportCreationDT, "ReportCreationDT");
    ValueEnforcer.notNull (aReportXMLBytes, "ReportXMLBytes");

    m_eReportType = eReportType;
    m_aReportPeriod = aReportPeriod;
    m_aReportCreationDT = aReportCreationDT;
    m_aReportXMLBytes = aReportXMLBytes;
    m_bReportValid = bReportValid;
  }

  @Nonnull
  public EPeppolReportType getReportType ()
  {
    return m_eReportType;
  }

  @Nonnull
  public YearMonth getReportPeriod ()
  {
    return m_aReportPeriod;
  }

  @Nonnull
  public LocalDateTime getReportCreationDT ()
  {
    return m_aReportCreationDT;
  }

  @Nonnull
  public ByteArrayWrapper getReportXMLBytes ()
  {
    return m_aReportXMLBytes;
  }

  public boolean isReportValid ()
  {
    return m_bReportValid;
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
           m_aReportXMLBytes.equals (rhs.m_aReportXMLBytes) &&
           m_bReportValid == rhs.m_bReportValid;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_eReportType)
                                       .append (m_aReportPeriod)
                                       .append (m_aReportCreationDT)
                                       .append (m_aReportXMLBytes)
                                       .append (m_bReportValid)
                                       .getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ReportType", m_eReportType)
                                       .append ("ReportPeriod", m_aReportPeriod)
                                       .append ("ReportCreationDT", m_aReportCreationDT)
                                       .append ("ReportXMLBytes", m_aReportXMLBytes)
                                       .append ("ReportValid", m_bReportValid)
                                       .getToString ();
  }
}
