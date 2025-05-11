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

import java.time.Duration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.traits.IGenericImplTrait;
import com.helger.datetime.expiration.ExpiringObject;
import com.helger.peppol.servicedomain.EPeppolNetwork;
import com.helger.peppol.smp.ESMPTransportProfile;
import com.helger.peppolid.IDocumentTypeIdentifier;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.smpclient.exception.SMPClientException;
import com.helger.smpclient.peppol.SMPClientReadOnly;
import com.helger.smpclient.url.PeppolConfigurableURLProvider;
import com.helger.smpclient.url.SMPDNSResolutionException;
import com.helger.xsds.peppol.smp1.EndpointType;

/**
 * This class contains a local cache that checks, if a sender is capable of receiving a specific
 * document type or not.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        The actual implementation type
 * @since 1.0.2
 */
public abstract class AbstractDocTypeSupportCache <IMPLTYPE extends AbstractDocTypeSupportCache <IMPLTYPE>> implements
                                                  IGenericImplTrait <IMPLTYPE>
{
  // Assume 6 hours caching duration by default
  public static final Duration DEFAULT_MAX_CACHE_DURATION = Duration.ofHours (6);
  private static final Logger LOGGER = LoggerFactory.getLogger (AbstractDocTypeSupportCache.class);

  private final EPeppolNetwork m_ePeppolNetwork;
  private Duration m_aMaxCacheDuration = DEFAULT_MAX_CACHE_DURATION;
  private final ICommonsMap <String, ExpiringObject <EndpointType>> m_aMap = new CommonsHashMap <> ();
  private final IDocumentTypeIdentifier m_aDocTypeID;
  private final IProcessIdentifier m_aProcessID;
  private final String m_sDocTypeName;

  /**
   * Constructor
   *
   * @param ePeppolNetwork
   *        Defines whether to use the Peppol production or test Network. May not be
   *        <code>null</code>.
   * @param aDocTypeID
   *        The document type ID to lookup. May not be <code>null</code>.
   * @param aProcessID
   *        The process ID to lookup. May not be <code>null</code>.
   * @param sDocTypeName
   *        The document type display name to lookup. Only used for logging. May neither be
   *        <code>null</code> nor empty.
   */
  public AbstractDocTypeSupportCache (@Nonnull final EPeppolNetwork ePeppolNetwork,
                                      @Nonnull final IDocumentTypeIdentifier aDocTypeID,
                                      @Nonnull final IProcessIdentifier aProcessID,
                                      @Nonnull @Nonempty final String sDocTypeName)
  {
    ValueEnforcer.notNull (ePeppolNetwork, "PeppolNetwork");
    ValueEnforcer.notNull (aDocTypeID, "DocTypeID");
    ValueEnforcer.notNull (aProcessID, "ProcessID");
    ValueEnforcer.notEmpty (sDocTypeName, "DocTypeName");

    m_ePeppolNetwork = ePeppolNetwork;
    m_aDocTypeID = aDocTypeID;
    m_aProcessID = aProcessID;
    m_sDocTypeName = sDocTypeName;
  }

  /**
   * @return The Peppol Network as defined in the constructor. Never <code>null</code>.
   */
  @Nonnull
  public final EPeppolNetwork getPeppolNetwork ()
  {
    return m_ePeppolNetwork;
  }

  /**
   * @return The max caching duration to use. Never <code>null</code>.
   */
  @Nonnull
  public final Duration getMaxCacheDuration ()
  {
    return m_aMaxCacheDuration;
  }

  /**
   * Set the maximum cache duration to use.
   *
   * @param aMaxCacheDuration
   *        The maximum cache duration to use. May not be <code>null</code>.
   * @return this for chaining
   */
  @Nonnull
  public final IMPLTYPE setMaxCacheDuration (@Nonnull final Duration aMaxCacheDuration)
  {
    ValueEnforcer.notNull (aMaxCacheDuration, "MaxCacheDuration");
    m_aMaxCacheDuration = aMaxCacheDuration;
    return thisAsT ();
  }

  @Nullable
  protected EndpointType resolveSmpEndpoint (@Nonnull final IParticipantIdentifier aPID)
  {
    ValueEnforcer.notNull (aPID, "PID");
    final String sKey = aPID.getURIEncoded ();

    // Check in cache
    final ExpiringObject <EndpointType> aItem = m_aMap.get (sKey);
    if (aItem != null)
    {
      if (aItem.isExpiredNow ())
        m_aMap.remove (sKey);
      else
      {
        final EndpointType ret = aItem.getObject ();
        if (LOGGER.isDebugEnabled ())
          LOGGER.debug (m_sDocTypeName + " support for '" + sKey + "' is taken from cache: " + (ret != null));
        return ret;
      }
    }

    // Query from SMP
    EndpointType aEndpoint = null;
    try
    {
      LOGGER.info ("Performing SMP query to check if '" + sKey + "' supports " + m_sDocTypeName + " or not");
      final SMPClientReadOnly aSMPClient = new SMPClientReadOnly (PeppolConfigurableURLProvider.INSTANCE,
                                                                  aPID,
                                                                  m_ePeppolNetwork.getSMLInfo ());
      aEndpoint = aSMPClient.getEndpoint (aPID,
                                          m_aDocTypeID,
                                          m_aProcessID,
                                          ESMPTransportProfile.TRANSPORT_PROFILE_PEPPOL_AS4_V2);
      LOGGER.info ("'" + sKey + "' does support " + m_sDocTypeName + ": " + (aEndpoint != null));
    }
    catch (final SMPDNSResolutionException | SMPClientException ex)
    {
      // Fall through - not supported
      LOGGER.error ("Error performing SMP query for " + m_sDocTypeName, ex);
    }

    // Remember in cache
    m_aMap.put (sKey, ExpiringObject.ofDuration (aEndpoint, m_aMaxCacheDuration));

    return aEndpoint;
  }
}
