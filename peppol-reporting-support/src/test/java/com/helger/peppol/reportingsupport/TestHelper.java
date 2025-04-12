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

import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.YearMonth;

import javax.annotation.Nonnull;

import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.state.ESuccess;
import com.helger.peppol.reportingsupport.domain.PeppolReportData;
import com.helger.peppol.reportingsupport.domain.PeppolReportSendingReportData;

public final class TestHelper
{
  private TestHelper ()
  {}

  public static void runCases (@Nonnull final IPeppolReportStorage aStorage)
  {
    for (final EPeppolReportType e : EPeppolReportType.values ())
    {
      final LocalDateTime aNow = PDTFactory.getCurrentLocalDateTime ();
      final YearMonth aPeriod = YearMonth.now ().minusMonths (1);

      ESuccess eSuccess = aStorage.storePeppolReport (new PeppolReportData (e, aPeriod, aNow, "<DummyReport />", true));
      assertTrue (eSuccess.isSuccess ());

      // Test with non-null sending report
      eSuccess = aStorage.storePeppolReportingSendingReport (new PeppolReportSendingReportData (e,
                                                                                             aPeriod,
                                                                                             aNow,
                                                                                             "<DummySendingReport />"));
      assertTrue (eSuccess.isSuccess ());

      // Test with null sending report
      eSuccess = aStorage.storePeppolReportingSendingReport (new PeppolReportSendingReportData (e,
                                                                                             aPeriod,
                                                                                             aNow.plusSeconds (1),
                                                                                             null));
      assertTrue (eSuccess.isSuccess ());
    }
  }
}
