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
package com.helger.peppol.reportingsupport.sql;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.db.api.config.IJdbcConfiguration;
import com.helger.db.jdbc.IHasDataSource;
import com.helger.db.jdbc.executor.DBExecutor;

/**
 * The Peppol Report Storage specific DB Executor
 *
 * @author Philip Helger
 */
public final class PeppolReportDBExecutor extends DBExecutor
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolReportDBExecutor.class);

  /**
   * Specific DB executor that uses configuration properties for JDBC data.
   *
   * @param aDataSourceProvider
   *        The SQL DataSource provider. May not be <code>null</code>.
   * @param aJdbcConfig
   *        The JDBC configuration. May not be <code>null</code>.
   */
  public PeppolReportDBExecutor (@Nonnull final IHasDataSource aDataSourceProvider,
                                 @Nonnull final IJdbcConfiguration aJdbcConfig)
  {
    super (aDataSourceProvider);
    ValueEnforcer.notNull (aJdbcConfig, "JDBCConfig");

    // This is ONLY for debugging
    setDebugConnections (aJdbcConfig.isJdbcDebugConnections ());
    setDebugTransactions (aJdbcConfig.isJdbcDebugTransactions ());
    setDebugSQLStatements (aJdbcConfig.isJdbcDebugSQL ());

    if (aJdbcConfig.isJdbcExecutionTimeWarningEnabled ())
    {
      final long nMillis = aJdbcConfig.getJdbcExecutionTimeWarningMilliseconds ();
      if (nMillis > 0)
        setExecutionDurationWarnMS (nMillis);
      else
        if (LOGGER.isDebugEnabled ())
          LOGGER.debug ("Ignoring JDBC Execution Time Warning Milliseconds because it is invalid.");
    }
    else
    {
      // Zero means none
      setExecutionDurationWarnMS (0);
    }
  }
}
