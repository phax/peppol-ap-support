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

import com.helger.commons.state.ESuccess;
import com.helger.peppol.reportingsupport.model.PeppolReportData;

/**
 * Interface for storing Peppol Reports and their sending reports
 *
 * @author Philip Helger
 */
public interface IPeppolReportingStorage
{
  /**
   * Store a new Peppol Report in the persistent storage.
   *
   * @param aReportData
   *        The report data. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  ESuccess storePeppolReport (@Nonnull PeppolReportData aReportData);

  /**
   * Store a new Peppol Report Sending Report in the persistent storage.
   *
   * @param eReportType
   *        The report type. May not be <code>null</code>.
   * @param aReportPeriod
   *        The year and month for which the report was created. May not be <code>null</code>.
   * @param aReportCreationDT
   *        The date and time, when the report was created. May not be <code>null</code>.
   * @param sSendingReportContent
   *        The effective content of the sending report. The exact syntax is undefined. May be
   *        <code>null</code> if the sending callback returned no content.
   * @return Never <code>null</code>.
   */
  @Nonnull
  ESuccess storePeppolReportSendingReport (@Nonnull EPeppolReportType eReportType,
                                           @Nonnull YearMonth aReportPeriod,
                                           @Nonnull LocalDateTime aReportCreationDT,
                                           @Nullable String sSendingReportContent);
}
