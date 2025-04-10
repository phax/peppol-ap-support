package com.helger.peppol.reportingsupport.sql;

import java.io.IOException;
import java.util.EnumSet;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.string.StringHelper;
import com.helger.config.IConfig;
import com.helger.db.api.EDatabaseSystemType;
import com.helger.db.api.flyway.FlywayConfiguration;
import com.helger.db.jdbc.DataSourceProviderFromJdbcConfiguration;
import com.helger.peppol.reporting.backend.sql.PeppolReportingBackendSqlSPI;

public class PeppolReportDBHandler implements Supplier <PeppolReportDBExecutor>, AutoCloseable
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolReportDBHandler.class);
  private static final EnumSet <EDatabaseSystemType> ALLOWED_DB_TYPES = EnumSet.of (EDatabaseSystemType.MYSQL,
                                                                                    EDatabaseSystemType.POSTGRESQL);

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  private final PeppolReportJdbcConfiguration m_aJdbcConfig;
  private final DataSourceProviderFromJdbcConfiguration m_aDSP;
  private final String m_sTableNamePrefix;

  public PeppolReportDBHandler (@Nonnull final IConfig aConfig)
  {
    // Init JDBC configuration
    final PeppolReportJdbcConfiguration aJdbcConfig = new PeppolReportJdbcConfiguration (aConfig);

    // Resolve database type
    final EDatabaseSystemType eDBType = aJdbcConfig.getJdbcDatabaseSystemType ();
    if (eDBType == null || !ALLOWED_DB_TYPES.contains (eDBType))
      throw new IllegalStateException ("The database type MUST be provided and MUST be one of " +
                                       StringHelper.imploder ()
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
    m_sTableNamePrefix = PeppolReportingBackendSqlSPI.getTableNamePrefix (eDBType, aJdbcConfig.getJdbcSchema ());
  }

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

  @Nonnull
  public PeppolReportDBExecutor get ()
  {
    return new PeppolReportDBExecutor (m_aDSP, m_aJdbcConfig);
  }

  @Nonnull
  public String getTableNamePrefix ()
  {
    return m_sTableNamePrefix;
  }
}
