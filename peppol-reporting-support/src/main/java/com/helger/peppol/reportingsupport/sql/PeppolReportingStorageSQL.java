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
package com.helger.peppol.reportingsupport.sql;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.state.ESuccess;
import com.helger.peppol.reportingsupport.IPeppolReportingStorage;
import com.helger.peppol.reportingsupport.domain.PeppolReportData;
import com.helger.peppol.reportingsupport.domain.PeppolReportSendingReportData;

/**
 * Implementation of {@link IPeppolReportingStorage} for SQL backend.
 */
public class PeppolReportingStorageSQL implements IPeppolReportingStorage
{
  @Nonnull
  public ESuccess storePeppolReport (@Nonnull final PeppolReportData aReportData)
  {
    ValueEnforcer.notNull (aReportData, "ReportData");

    // TODO

    return ESuccess.SUCCESS;
  }

  @Nonnull
  public ESuccess storePeppolReportSendingReport (@Nonnull final PeppolReportSendingReportData aSendingReportData)
  {
    ValueEnforcer.notNull (aSendingReportData, "aSendingReportData");

    // TODO

    return ESuccess.SUCCESS;
  }
}
