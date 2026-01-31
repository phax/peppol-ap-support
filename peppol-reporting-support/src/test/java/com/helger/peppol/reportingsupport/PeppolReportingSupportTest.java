/*
 * Copyright (C) 2025-2026 Philip Helger
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

import java.time.OffsetDateTime;

import org.jspecify.annotations.NonNull;
import org.junit.Test;

import com.helger.base.state.ESuccess;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.datetime.helper.PDTFactory;
import com.helger.peppol.reporting.api.PeppolReportingItem;
import com.helger.peppol.reporting.eusr.EndUserStatisticsReport;
import com.helger.peppol.reporting.jaxb.eusr.v110.EndUserStatisticsReportType;
import com.helger.peppol.reporting.jaxb.tsr.v101.TransactionStatisticsReportType;
import com.helger.peppol.reporting.tsr.TransactionStatisticsReport;
import com.helger.peppol.reportingsupport.domain.PeppolReportData;
import com.helger.peppol.reportingsupport.domain.PeppolReportSendingReportData;
import com.helger.peppolid.peppol.doctype.EPredefinedDocumentTypeIdentifier;
import com.helger.peppolid.peppol.process.EPredefinedProcessIdentifier;

/**
 * Test class for class {@link PeppolReportingSupport}.
 *
 * @author Philip Helger
 */
public final class PeppolReportingSupportTest
{
  private static final String MY_SPID = "PDE000001";

  @Test
  public void testEUSR ()
  {
    final OffsetDateTime aNow = PDTFactory.getCurrentOffsetDateTime ();
    final String sOtherSPID = "POP000002";
    final String sEndUserID = "abc";

    final PeppolReportingItem aItem = PeppolReportingItem.builder ()
                                                         .exchangeDateTime (aNow)
                                                         .directionSending ()
                                                         .c2ID (MY_SPID)
                                                         .c3ID (sOtherSPID)
                                                         .docTypeID (EPredefinedDocumentTypeIdentifier.INVOICE_EN16931_PEPPOL_V30)
                                                         .processID (EPredefinedProcessIdentifier.BIS3_BILLING)
                                                         .transportProtocolPeppolAS4v2 ()
                                                         .c1CountryCode ("FI")
                                                         .endUserID (sEndUserID)
                                                         .build ();

    final TransactionStatisticsReportType aTSR = TransactionStatisticsReport.builder ()
                                                                            .monthOf (aNow)
                                                                            .reportingServiceProviderID (MY_SPID)
                                                                            .reportingItemList (new CommonsArrayList <> (aItem))
                                                                            .build ();
    final EndUserStatisticsReportType aEUSR = EndUserStatisticsReport.builder ()
                                                                     .monthOf (aNow)
                                                                     .reportingServiceProviderID (MY_SPID)
                                                                     .reportingItemList (new CommonsArrayList <> (aItem))
                                                                     .build ();

    // Handler that doesn't store anything
    final IPeppolReportStorage aStorage = new IPeppolReportStorage ()
    {
      @NonNull
      public ESuccess storePeppolReport (@NonNull final PeppolReportData aReportData)
      {
        return ESuccess.SUCCESS;
      }

      @NonNull
      public ESuccess storePeppolReportingSendingReport (@NonNull final PeppolReportSendingReportData aSendingReportData)
      {
        return ESuccess.SUCCESS;
      }
    };
    final PeppolReportingSupport aPRS = new PeppolReportingSupport (aStorage);

    // TSR
    ESuccess eSuccess = aPRS.validateAndStorePeppolTSR10 (aTSR, x -> {});
    assertTrue (eSuccess.isSuccess ());

    // EUSR
    eSuccess = aPRS.validateAndStorePeppolEUSR11 (aEUSR, x -> {});
    assertTrue (eSuccess.isSuccess ());
  }
}
