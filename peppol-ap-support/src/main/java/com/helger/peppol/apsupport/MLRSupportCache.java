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
package com.helger.peppol.apsupport;

import com.helger.peppol.servicedomain.EPeppolNetwork;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.peppol.doctype.EPredefinedDocumentTypeIdentifier;
import com.helger.peppolid.peppol.process.EPredefinedProcessIdentifier;
import com.helger.xsds.peppol.smp1.EndpointType;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * This class contains a local cache that checks, if a sender is capable of receiving Peppol MLR
 * messages or not.
 *
 * @author Philip Helger
 * @since 1.0.2
 */
public final class MLRSupportCache extends AbstractDocTypeSupportCache <MLRSupportCache>
{
  /**
   * Constructor
   *
   * @param ePeppolNetwork
   *        Defines whether to use the Peppol production or test Network. May not be
   *        <code>null</code>.
   */
  public MLRSupportCache (@Nonnull final EPeppolNetwork ePeppolNetwork)
  {
    super (ePeppolNetwork,
           EPredefinedDocumentTypeIdentifier.APPLICATIONRESPONSE_FDC_PEPPOL_EU_POACC_TRNS_MLR_3,
           EPredefinedProcessIdentifier.BIS3_MLR,
           "MLR");
  }

  /**
   * Get the MLR SMP Endpoint registered for the provided participant ID.
   *
   * @param aC1ID
   *        The participant ID of C1 of the original business document to be queried.
   * @return <code>null</code> if no such endpoint is registered.
   */
  @Nullable
  public EndpointType getMLREndpoint (@Nonnull final IParticipantIdentifier aC1ID)
  {
    return resolveSmpEndpoint (aC1ID);
  }
}
