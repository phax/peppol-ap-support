package com.helger.peppol.reportingsupport.sql;

import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.YearMonth;

import javax.annotation.Nonnull;

import org.junit.Test;

import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.state.ESuccess;
import com.helger.config.Config;
import com.helger.config.IConfig;
import com.helger.config.source.res.ConfigurationSourceProperties;
import com.helger.peppol.reporting.api.backend.PeppolReportingBackendException;
import com.helger.peppol.reportingsupport.EPeppolReportType;
import com.helger.peppol.reportingsupport.domain.PeppolReportData;
import com.helger.peppol.reportingsupport.domain.PeppolReportSendingReportData;

/**
 * Test class for class {@link PeppolReportStorageSQL}.
 *
 * @author Philip Helger
 */
public final class PeppolReportStorageSQLTest
{
  private void _runTests (@Nonnull final IConfig aConfig) throws PeppolReportingBackendException
  {
    try (final PeppolReportDBHandler aHdl = new PeppolReportDBHandler (aConfig))
    {
      final PeppolReportStorageSQL aStorage = new PeppolReportStorageSQL (aHdl, aHdl.getTableNamePrefix ());
      for (final EPeppolReportType e : EPeppolReportType.values ())
      {
        final LocalDateTime aNow = PDTFactory.getCurrentLocalDateTime ();
        ESuccess eSuccess = aStorage.storePeppolReport (new PeppolReportData (e,
                                                                              YearMonth.now ().minusMonths (1),
                                                                              aNow,
                                                                              "<Dummy />",
                                                                              true));
        assertTrue (eSuccess.isSuccess ());

        eSuccess = aStorage.storePeppolReportSendingReport (new PeppolReportSendingReportData (e,
                                                                                               YearMonth.now ()
                                                                                                        .minusMonths (1),
                                                                                               aNow,
                                                                                               "<DummySendingReport />"));
        assertTrue (eSuccess.isSuccess ());
      }
    }
  }

  @Test
  public void testMySQL () throws PeppolReportingBackendException
  {
    _runTests (new Config (new ConfigurationSourceProperties (new ClassPathResource ("application-mysql.properties"))));
  }

  @Test
  public void testPostgreSQL () throws PeppolReportingBackendException
  {
    _runTests (new Config (new ConfigurationSourceProperties (new ClassPathResource ("application-postgresql.properties"))));
  }
}
