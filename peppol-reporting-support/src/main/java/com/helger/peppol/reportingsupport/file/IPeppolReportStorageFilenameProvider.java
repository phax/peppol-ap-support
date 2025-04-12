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
package com.helger.peppol.reportingsupport.file;

import java.time.LocalDateTime;
import java.time.YearMonth;

import javax.annotation.Nonnull;

import com.helger.commons.string.StringHelper;
import com.helger.datetime.util.PDTIOHelper;
import com.helger.peppol.reportingsupport.EPeppolReportType;

/**
 * Callback interface to create a filename for storage
 *
 * @author Philip Helger
 */
public interface IPeppolReportStorageFilenameProvider
{
  @Nonnull
  String getFilename (@Nonnull YearMonth aReportPeriod,
                      @Nonnull EPeppolReportType eReportType,
                      @Nonnull LocalDateTime aReportCreationDT,
                      @Nonnull String sFilenameSuffix);

  /** The default filename provider */
  IPeppolReportStorageFilenameProvider DEFAULT = (period, type, creationDT, suffix) -> StringHelper.getLeadingZero (
                                                                                                                    period.getYear (),
                                                                                                                    4) +
                                                                                       '/' +
                                                                                       StringHelper.getLeadingZero (period.getMonthValue (),
                                                                                                                    2) +
                                                                                       '/' +
                                                                                       type.getID () +
                                                                                       '-' +
                                                                                       PDTIOHelper.getLocalDateTimeForFilename (creationDT) +
                                                                                       '-' +
                                                                                       suffix;
}
