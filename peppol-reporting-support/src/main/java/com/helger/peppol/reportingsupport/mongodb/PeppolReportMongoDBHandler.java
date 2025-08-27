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
package com.helger.peppol.reportingsupport.mongodb;

import java.util.function.Supplier;

import com.helger.annotation.Nonempty;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.config.IConfig;
import com.helger.peppol.reporting.backend.mongodb.MongoClientWrapper;
import com.helger.peppol.reporting.backend.mongodb.PeppolReportingBackendMongoDBSPI;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * A wrapper for {@link MongoClientWrapper} that must be closed when it goes out of scope.
 *
 * @author Philip Helger
 */
public class PeppolReportMongoDBHandler implements Supplier <MongoClientWrapper>, AutoCloseable
{
  private MongoClientWrapper m_aMongoDBClient;

  /**
   * Constructor
   *
   * @param aMongoDBClient
   *        The MongoDB client wrapper. May be <code>null</code>.
   */
  protected PeppolReportMongoDBHandler (@Nullable final MongoClientWrapper aMongoDBClient)
  {
    m_aMongoDBClient = aMongoDBClient;
  }

  public void close ()
  {
    if (m_aMongoDBClient != null)
    {
      m_aMongoDBClient.close ();
      m_aMongoDBClient = null;
    }
  }

  @Nullable
  public MongoClientWrapper get ()
  {
    return m_aMongoDBClient;
  }

  /**
   * Create a handler that uses the same configuration (=same database) as Peppol Reporting.
   *
   * @param aConfig
   *        The configuration object to use
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static PeppolReportMongoDBHandler createPeppolReportingConfigured (@Nonnull final IConfig aConfig)
  {
    ValueEnforcer.notNull (aConfig, "Config");

    return new PeppolReportMongoDBHandler (PeppolReportingBackendMongoDBSPI.createDefaultClientWrapper (aConfig));
  }

  /**
   * Create a handler with custom connection string and DB name
   *
   * @param sConnectionString
   *        MongoDB connection string. May neither be <code>null</code> nor empty.
   * @param sDBName
   *        MongoDB database name. May neither be <code>null</code> nor empty.
   * @return Never null
   */
  @Nonnull
  public static PeppolReportMongoDBHandler create (@Nonnull @Nonempty final String sConnectionString,
                                                   @Nonnull @Nonempty final String sDBName)
  {
    ValueEnforcer.notEmpty (sConnectionString, "ConnectionString");
    ValueEnforcer.notEmpty (sDBName, "DBName");

    return new PeppolReportMongoDBHandler (new MongoClientWrapper (sConnectionString, sDBName));
  }
}
