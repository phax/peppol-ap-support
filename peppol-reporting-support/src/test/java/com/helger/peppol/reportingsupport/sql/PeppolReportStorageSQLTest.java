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

import org.jspecify.annotations.NonNull;
import org.junit.Test;

import com.helger.config.Config;
import com.helger.config.IConfig;
import com.helger.config.source.resource.properties.ConfigurationSourceProperties;
import com.helger.io.resource.ClassPathResource;
import com.helger.peppol.reportingsupport.TestHelper;

/**
 * Test class for class {@link PeppolReportStorageSQL}.
 *
 * @author Philip Helger
 */
public final class PeppolReportStorageSQLTest
{
  private void _runTests (@NonNull final IConfig aConfig)
  {
    try (final PeppolReportSQLHandler aHdl = new PeppolReportSQLHandler (aConfig))
    {
      final PeppolReportStorageSQL aStorage = new PeppolReportStorageSQL (aHdl, aHdl.getTableNamePrefix ());
      TestHelper.runCases (aStorage);
    }
  }

  @Test
  public void testMySQL ()
  {
    _runTests (new Config (new ConfigurationSourceProperties (new ClassPathResource ("application-mysql.properties"))));
  }

  @Test
  public void testPostgreSQL ()
  {
    _runTests (new Config (new ConfigurationSourceProperties (new ClassPathResource ("application-postgresql.properties"))));
  }
}
