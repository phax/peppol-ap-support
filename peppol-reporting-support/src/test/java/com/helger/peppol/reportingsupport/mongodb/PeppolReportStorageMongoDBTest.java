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

import org.junit.Test;

import com.helger.commons.concurrent.ThreadHelper;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.config.Config;
import com.helger.config.IConfig;
import com.helger.config.source.res.ConfigurationSourceProperties;
import com.helger.peppol.reportingsupport.TestHelper;

/**
 * Test class for class {@link PeppolReportStorageMongoDB}.
 *
 * @author Philip Helger
 */
public final class PeppolReportStorageMongoDBTest
{
  @Test
  public void testBasic ()
  {
    // Use Test specific configuration
    final IConfig aConfig = new Config (new ConfigurationSourceProperties (new ClassPathResource ("application-mongodb.properties")));

    try (final PeppolReportMongoDBHandler aHdl = PeppolReportMongoDBHandler.createPeppolReportingConfigured (aConfig))
    {
      // Required in some cases
      ThreadHelper.sleep (50);

      final PeppolReportStorageMongoDB aStorage = new PeppolReportStorageMongoDB (aHdl);
      TestHelper.runCases (aStorage);
    }
  }
}
