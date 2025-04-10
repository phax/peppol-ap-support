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

import javax.annotation.Nonnull;

import com.helger.commons.state.ESuccess;
import com.helger.peppol.reportingsupport.domain.PeppolReportData;
import com.helger.peppol.reportingsupport.domain.PeppolReportSendingReportData;

/**
 * Interface for storing Peppol Reports and their sending reports
 *
 * @author Philip Helger
 */
public interface IPeppolReportStorage
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
   * @param aSendingReportData
   *        The sending report data. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  ESuccess storePeppolReportSendingReport (@Nonnull PeppolReportSendingReportData aSendingReportData);
}
