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
package com.helger.peppol.apsupport;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.state.EChange;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.cache.impl.MappedKeyProviderCache;
import com.helger.httpclient.HttpClientManager;
import com.helger.httpclient.HttpClientSettings;
import com.helger.httpclient.response.ResponseHandlerByteArray;
import com.helger.peppol.businesscard.generic.PDBusinessCard;
import com.helger.peppol.businesscard.helper.PDBusinessCardHelper;
import com.helger.peppol.sml.ISMLInfo;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.smpclient.url.PeppolNaptrURLProvider;
import com.helger.smpclient.url.SMPDNSResolutionException;

/**
 * Cache for BusinessCards of participants queried from an SMP.
 *
 * @author Philip Helger
 * @since 10.0.0
 */
public class BusinessCardCache
{
  private static final Logger LOGGER = LoggerFactory.getLogger (BusinessCardCache.class);

  @NonNull
  private static PDBusinessCard _fetchBC (@NonNull final ISMLInfo aSMLInfo,
                                          @NonNull final HttpClientSettings aHCS,
                                          @NonNull final IParticipantIdentifier aPI)
  {
    try
    {
      String sBCURL = PeppolNaptrURLProvider.INSTANCE.getSMPURIOfParticipant (aPI, aSMLInfo).toString ();
      if (!sBCURL.endsWith ("/"))
        sBCURL += '/';
      sBCURL += "businesscard/" + aPI.getURIPercentEncoded ();
      LOGGER.info ("Fetching Business Card from '" + sBCURL + "'");

      byte [] aData = null;
      try (final HttpClientManager aHttpClientMgr = HttpClientManager.create (aHCS))
      {
        final HttpGet aGet = new HttpGet (sBCURL);
        aData = aHttpClientMgr.execute (aGet, new ResponseHandlerByteArray ());
      }
      catch (final Exception ex)
      {
        // Ignore - means no BC
      }

      PDBusinessCard aBC = null;
      if (aData != null)
      {
        // Try parsing
        aBC = PDBusinessCardHelper.parseBusinessCard (aData, StandardCharsets.UTF_8);
      }

      // Data to cache
      return aBC;
    }
    catch (final SMPDNSResolutionException ex)
    {
      throw new IllegalStateException (ex);
    }
  }

  private final MappedKeyProviderCache <IParticipantIdentifier, String, PDBusinessCard> m_aCache;

  /**
   * Constructor. Caches the entries for 1 hour with a maximum of 1000 entries
   *
   * @param aSMLInfo
   *        SML to use. To differentiate between test and production.
   * @param aHCS
   *        The Http client settings to be used. Could be e.g.
   *        {@link com.helger.smpclient.httpclient.SMPHttpClientSettings}
   */
  public BusinessCardCache (@NonNull final ISMLInfo aSMLInfo, @NonNull final HttpClientSettings aHCS)
  {
    m_aCache = MappedKeyProviderCache.<IParticipantIdentifier, String, PDBusinessCard> builder ()
                                     .name ("PeppolBusinessCardCache")
                                     .maxSize (1_000)
                                     .allowNullValues (true)
                                     .expireAfterWrite (Duration.ofHours (1))
                                     .evictionInterval (Duration.ofMinutes (1))
                                     .keyMapper (IParticipantIdentifier::getURIEncoded)
                                     .valueProvider (pi -> _fetchBC (aSMLInfo, aHCS, pi))
                                     .build ();
  }

  @Nullable
  private PDBusinessCard _getActive (@NonNull final IParticipantIdentifier aParticipantID)
  {
    return m_aCache.getFromCache (aParticipantID);
  }

  /**
   * Get the cached Business Card of the provided participant ID.
   *
   * @param aParticipantID
   *        The participant ID to query. May not be <code>null</code>.
   * @return <code>null</code> if no Business Card is present.
   */
  @Nullable
  public PDBusinessCard getBusinessCard (@NonNull final IParticipantIdentifier aParticipantID)
  {
    return _getActive (aParticipantID);
  }

  /**
   * Get the country code contained in the Business Card of the provided participant ID.
   *
   * @param aParticipantID
   *        The participant ID to query. May not be <code>null</code>.
   * @return <code>null</code> if no Business Card or no Business Card Entity with a country code is
   *         present.
   */
  @Nullable
  public String getCountryCode (@NonNull final IParticipantIdentifier aParticipantID)
  {
    final PDBusinessCard aBC = getBusinessCard (aParticipantID);
    if ((aBC == null) || aBC.businessEntities ().isEmpty ())
      return null;
    return aBC.businessEntities ().getFirstOrNull ().getCountryCode ();
  }

  /**
   * Clear the cache
   *
   * @return {@link EChange#CHANGED} if something was contained in the cache,
   *         {@link EChange#UNCHANGED} otherwise.
   */
  @NonNull
  public EChange clearCache ()
  {
    return m_aCache.clearCache ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("Cache", m_aCache).getToString ();
  }
}
