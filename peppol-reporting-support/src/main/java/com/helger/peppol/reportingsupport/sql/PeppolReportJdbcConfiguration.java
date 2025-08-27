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

import com.helger.annotation.concurrent.Immutable;
import com.helger.config.IConfig;
import com.helger.db.api.config.JdbcConfigurationConfig;

import jakarta.annotation.Nonnull;

/**
 * Peppol Report Storage JDBC configuration with lazy initialization.
 *
 * @author Philip Helger
 */
@Immutable
public class PeppolReportJdbcConfiguration extends JdbcConfigurationConfig
{
  /**
   * The JDBC configuration prefix.
   */
  public static final String CONFIG_PREFIX = "peppol.report.jdbc.";

  /**
   * Constructor
   *
   * @param aConfig
   *        The configuration object to use. May not be <code>null</code>.
   */
  public PeppolReportJdbcConfiguration (@Nonnull final IConfig aConfig)
  {
    super (aConfig, CONFIG_PREFIX);
  }
}
