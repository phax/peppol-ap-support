package com.helger.peppol.reportingsupport.sql;

import javax.annotation.Nonnull;

import com.helger.config.IConfig;
import com.helger.db.api.flyway.FlywayConfigurationBuilderConfig;

/**
 * The specific Flyway Configuration builder for Peppol Report Storage.
 *
 * @author Philip Helger
 */
public class PeppolReportFlywayConfigurationBuilder extends FlywayConfigurationBuilderConfig
{
  public static final String FLYWAY_CONFIG_PREFIX = "peppol.report.flyway.";

  public PeppolReportFlywayConfigurationBuilder (@Nonnull final IConfig aConfig,
                                                 @Nonnull final PeppolReportJdbcConfiguration aJdbcConfig)
  {
    super (aConfig, FLYWAY_CONFIG_PREFIX);

    // Fallback to other configuration values
    if (jdbcUrl () == null)
      jdbcUrl (aJdbcConfig.getJdbcUrl ());
    if (jdbcUser () == null)
      jdbcUser (aJdbcConfig.getJdbcUser ());
    if (jdbcPassword () == null)
      jdbcPassword (aJdbcConfig.getJdbcPassword ());
  }
}
