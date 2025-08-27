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

import com.helger.base.enforce.ValueEnforcer;
import com.helger.config.IConfig;
import com.helger.db.api.config.IJdbcConfiguration;
import com.helger.db.api.flyway.FlywayConfigurationBuilderConfig;

import jakarta.annotation.Nonnull;

/**
 * The specific Flyway Configuration builder for Peppol Report Storage.
 *
 * @author Philip Helger
 */
public class PeppolReportFlywayConfigurationBuilder extends FlywayConfigurationBuilderConfig
{
  /**
   * The Flyway configuration prefix.
   */
  public static final String FLYWAY_CONFIG_PREFIX = "peppol.report.flyway.";

  /**
   * Constructor
   *
   * @param aConfig
   *        The configuration object. May not be <code>null</code>.
   * @param aJdbcConfig
   *        The JDBC configuration to act as a potential fallback for JDBC connection data. May not
   *        be <code>null</code>.
   */
  public PeppolReportFlywayConfigurationBuilder (@Nonnull final IConfig aConfig,
                                                 @Nonnull final IJdbcConfiguration aJdbcConfig)
  {
    super (aConfig, FLYWAY_CONFIG_PREFIX);
    ValueEnforcer.notNull (aJdbcConfig, "JdbcConfig");

    // Fallback to other configuration values
    if (jdbcUrl () == null)
      jdbcUrl (aJdbcConfig.getJdbcUrl ());
    if (jdbcUser () == null)
      jdbcUser (aJdbcConfig.getJdbcUser ());
    if (jdbcPassword () == null)
      jdbcPassword (aJdbcConfig.getJdbcPassword ());
  }
}
