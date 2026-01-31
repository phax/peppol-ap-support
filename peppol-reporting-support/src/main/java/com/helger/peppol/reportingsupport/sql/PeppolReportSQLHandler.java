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
package com.helger.peppol.reportingsupport.sql;

import java.io.IOException;
import java.util.EnumSet;
import java.util.function.Supplier;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.string.StringImplode;
import com.helger.config.IConfig;
import com.helger.db.api.EDatabaseSystemType;
import com.helger.db.api.config.IJdbcConfiguration;
import com.helger.db.api.flyway.FlywayConfiguration;
import com.helger.db.api.helper.DBSystemHelper;
import com.helger.db.jdbc.DataSourceProviderFromJdbcConfiguration;

/**
 * The handler that acts as a provider for SQL Data Sources. It reads everything from configuration
 * and executes Flyway if necessary. So don't instantiate over and over again if performance is an
 * issue for you.
 *
 * @author Philip Helger
 */
public class PeppolReportSQLHandler implements Supplier <PeppolReportDBExecutor>, AutoCloseable
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolReportSQLHandler.class);
  private static final EnumSet <EDatabaseSystemType> ALLOWED_DB_TYPES = EnumSet.of (EDatabaseSystemType.MYSQL,
                                                                                    EDatabaseSystemType.POSTGRESQL);

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  private final IJdbcConfiguration m_aJdbcConfig;
  private final DataSourceProviderFromJdbcConfiguration m_aDSP;
  private final String m_sTableNamePrefix;

  /**
   * Constructor
   *
   * @param aConfig
   *        The configuration object to use. May not be <code>null</code>.
   */
  public PeppolReportSQLHandler (@NonNull final IConfig aConfig)
  {
    // Init JDBC configuration
    final IJdbcConfiguration aJdbcConfig = new PeppolReportJdbcConfiguration (aConfig);

    // Resolve database type
    final EDatabaseSystemType eDBType = aJdbcConfig.getJdbcDatabaseSystemType ();
    if (eDBType == null || !ALLOWED_DB_TYPES.contains (eDBType))
      throw new IllegalStateException ("The database type MUST be provided and MUST be one of " +
                                       StringImplode.imploder ()
                                                    .source (ALLOWED_DB_TYPES, EDatabaseSystemType::getID)
                                                    .separator (", ")
                                                    .build () +
                                       " - provided value is '" +
                                       aJdbcConfig.getJdbcDatabaseType () +
                                       "'");

    // Build Flyway configuration
    final PeppolReportFlywayConfigurationBuilder aBuilder = new PeppolReportFlywayConfigurationBuilder (aConfig,
                                                                                                        aJdbcConfig);
    final FlywayConfiguration aFlywayConfig = aBuilder.build ();

    // Run Flyway
    if (aFlywayConfig.isFlywayEnabled ())
      PeppolReportFlywayMigrator.Singleton.INSTANCE.runFlyway (eDBType, aJdbcConfig, aFlywayConfig);
    else
      LOGGER.warn ("Peppol Reporting Flyway Migration is disabled according to the configuration key '" +
                   aBuilder.getConfigKeyEnabled () +
                   "'");

    // Remember stuff
    m_aJdbcConfig = aJdbcConfig;
    m_aDSP = new DataSourceProviderFromJdbcConfiguration (aJdbcConfig);
    if (m_aDSP == null)
      throw new IllegalStateException ("Failed to create Peppol Report SQL DB DataSource provider");
    m_sTableNamePrefix = DBSystemHelper.getTableNamePrefix (eDBType, aJdbcConfig.getJdbcSchema ());
  }

  /**
   * Check if the handler is correctly initialized
   *
   * @return <code>true</code> if it is initialized, <code>false</code> if not.
   */
  public boolean isInitialized ()
  {
    return m_aRWLock.readLockedBoolean ( () -> m_aDSP != null && m_sTableNamePrefix != null);
  }

  public void close ()
  {
    if (isInitialized ())
    {
      m_aRWLock.writeLocked ( () -> {
        LOGGER.info ("Shutting down Peppol Report SQL DB client");
        if (m_aDSP != null)
          try
          {
            m_aDSP.close ();
          }
          catch (final IOException ex)
          {
            LOGGER.error ("Failed to close Peppol Report DataSource provider", ex);
          }
      });
    }
    else
      LOGGER.warn ("The Peppol Report SQL DB backend cannot be shutdown, because it was never properly initialized");
  }

  @NonNull
  public PeppolReportDBExecutor get ()
  {
    return new PeppolReportDBExecutor (m_aDSP, m_aJdbcConfig);
  }

  /**
   * Get an eventually existing table name prefix
   *
   * @return A non-<code>null</code> but maybe empty table name prefix (like <code>schema.</code>).
   */
  @NonNull
  public String getTableNamePrefix ()
  {
    return m_sTableNamePrefix;
  }
}
