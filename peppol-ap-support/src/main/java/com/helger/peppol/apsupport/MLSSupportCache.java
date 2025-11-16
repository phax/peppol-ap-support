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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.peppol.servicedomain.EPeppolNetwork;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.peppol.doctype.EPredefinedDocumentTypeIdentifier;
import com.helger.peppolid.peppol.process.EPredefinedProcessIdentifier;
import com.helger.xsds.peppol.smp1.EndpointType;

/**
 * This class contains a local cache that checks, if a sender is capable of receiving Peppol MLS
 * messages or not.
 *
 * @author Philip Helger
 * @since 1.0.2
 */
public final class MLSSupportCache extends AbstractDocTypeSupportCache <MLSSupportCache>
{
  /**
   * Constructor
   *
   * @param ePeppolNetwork
   *        Defines whether to use the Peppol production or test Network. May not be
   *        <code>null</code>.
   */
  public MLSSupportCache (@NonNull final EPeppolNetwork ePeppolNetwork)
  {
    super (ePeppolNetwork,
           EPredefinedDocumentTypeIdentifier.PEPPOL_MLS_1_0,
           EPredefinedProcessIdentifier.urn_peppol_edec_mls,
           "MLS");
  }

  /**
   * Get the MLS SMP Endpoint registered for the provided participant ID.
   *
   * @param aC2ID
   *        The participant ID of C2 of the original business document to be queried.
   * @return <code>null</code> if no such endpoint is registered.
   */
  @Nullable
  public EndpointType getMLSEndpoint (@NonNull final IParticipantIdentifier aC2ID)
  {
    return resolveSmpEndpoint (aC2ID);
  }
}
