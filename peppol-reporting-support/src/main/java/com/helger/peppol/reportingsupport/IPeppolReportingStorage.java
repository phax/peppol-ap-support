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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.state.ESuccess;

/**
 * Interface for storing Peppol Reports and their sending reports
 *
 * @author Philip Helger
 */
public interface IPeppolReportingStorage
{
  /**
   * Store a new Peppol report in the persistent storage.
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
   * @param eReportValid
   *        The indicator, if the report was valid or not. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  ESuccess storePeppolReport (@Nonnull EPeppolReportType eReportType,
                              @Nonnull YearMonth aReportPeriod,
                              @Nonnull LocalDateTime aReportCreationDT,
                              @Nonnull @Nonempty byte [] aReportXMLBytes,
                              @Nonnull ESuccess eReportValid);

  /**
   * Store a new Peppol report in the persistent storage.
   *
   * @param eReportType
   *        The report type. May not be <code>null</code>.
   * @param aReportPeriod
   *        The year and month for which the report was created. May not be <code>null</code>.
   * @param aReportCreationDT
   *        The date and time, when the report was created. May not be <code>null</code>.
   * @param sSendingReportBytes
   *        The effective bytes of the sending report. May be <code>null</code> if the sending
   *        callback returned no content.
   * @return Never <code>null</code>.
   */
  @Nonnull
  ESuccess storePeppolSendingReport (@Nonnull EPeppolReportType eReportType,
                                     @Nonnull YearMonth aReportPeriod,
                                     @Nonnull LocalDateTime aReportCreationDT,
                                     @Nullable String sSendingReportBytes);
}
