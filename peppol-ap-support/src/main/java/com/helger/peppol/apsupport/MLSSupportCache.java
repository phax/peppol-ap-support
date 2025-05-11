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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.peppol.servicedomain.EPeppolNetwork;
import com.helger.peppolid.IDocumentTypeIdentifier;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.peppolid.factory.PeppolIdentifierFactory;
import com.helger.peppolid.peppol.PeppolIdentifierHelper;
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
  // TODO replace with official code list entries, once they are approved
  private static final IDocumentTypeIdentifier DOC_TYPE_ID = PeppolIdentifierFactory.INSTANCE.createDocumentTypeIdentifier (PeppolIdentifierHelper.DOCUMENT_TYPE_SCHEME_BUSDOX_DOCID_QNS,
                                                                                                                            "urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2::ApplicationResponse##urn:peppol:edec:mls:1.0::2.1");
  private static final IProcessIdentifier PROCESS_ID = PeppolIdentifierFactory.INSTANCE.createProcessIdentifier (PeppolIdentifierHelper.PROCESS_SCHEME_CENBII_PROCID_UBL,
                                                                                                                 "urn:peppol:edec:mls");

  /**
   * Constructor
   *
   * @param ePeppolNetwork
   *        Defines whether to use the Peppol production or test Network. May not be
   *        <code>null</code>.
   */
  public MLSSupportCache (@Nonnull final EPeppolNetwork ePeppolNetwork)
  {
    super (ePeppolNetwork, DOC_TYPE_ID, PROCESS_ID, "MLS");
  }

  /**
   * Get the MLS SMP Endpoint registered for the provided participant ID.
   *
   * @param aC2ID
   *        The participant ID of C2 of the original business document to be queried.
   * @return <code>null</code> if no such endpoint is registered.
   */
  @Nullable
  public EndpointType getMLSEndpoint (@Nonnull final IParticipantIdentifier aC2ID)
  {
    return resolveSmpEndpoint (aC2ID);
  }
}
