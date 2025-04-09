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

import java.time.LocalDateTime;
import java.time.YearMonth;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.state.ESuccess;
import com.helger.peppol.reportingsupport.EPeppolReportType;
import com.helger.peppol.reportingsupport.IPeppolReportingStorage;
import com.helger.peppol.reportingsupport.model.PeppolReportData;

/**
 * Implementation of {@link IPeppolReportingStorage} for SQL backend.
 */
public final class PeppolReportingStorageSQL implements IPeppolReportingStorage
{
  @Nonnull
  public ESuccess storePeppolReport (@Nonnull final PeppolReportData aReportData)
  {
    ValueEnforcer.notNull (aReportData, "ReportData");

    // TODO

    return ESuccess.SUCCESS;
  }

  @Nonnull
  public ESuccess storePeppolReportSendingReport (@Nonnull final EPeppolReportType eReportType,
                                                  @Nonnull final YearMonth aReportPeriod,
                                                  @Nonnull final LocalDateTime aReportCreationDT,
                                                  @Nullable final String sSendingReportBytes)
  {
    ValueEnforcer.notNull (eReportType, "ReportType");
    ValueEnforcer.notNull (aReportPeriod, "ReportPeriod");
    ValueEnforcer.notNull (aReportCreationDT, "ReportCreationDT");

    // TODO

    return ESuccess.SUCCESS;
  }
}
