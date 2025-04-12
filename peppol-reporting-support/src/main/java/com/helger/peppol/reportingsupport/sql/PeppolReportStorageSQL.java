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

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.state.ESuccess;
import com.helger.db.api.helper.DBValueHelper;
import com.helger.db.jdbc.callback.ConstantPreparedStatementDataProvider;
import com.helger.db.jdbc.executor.DBExecutor;
import com.helger.peppol.reportingsupport.EPeppolReportType;
import com.helger.peppol.reportingsupport.IPeppolReportStorage;
import com.helger.peppol.reportingsupport.domain.PeppolReportData;
import com.helger.peppol.reportingsupport.domain.PeppolReportSendingReportData;

/**
 * Implementation of {@link IPeppolReportStorage} for SQL backend.
 */
public class PeppolReportStorageSQL implements IPeppolReportStorage
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolReportStorageSQL.class);

  private final Supplier <? extends PeppolReportDBExecutor> m_aDBExecutorSupplier;
  private final String m_sTableNamePrefix;

  /**
   * Constructor
   *
   * @param aDBExecutorSupplier
   *        The supplier for specific DB executors. May not be <code>null</code>.
   * @param sTableNamePrefix
   *        The table name prefix to use. May not be <code>null</code> but maybe empty.
   */
  public PeppolReportStorageSQL (@Nonnull final Supplier <? extends PeppolReportDBExecutor> aDBExecutorSupplier,
                                 @Nonnull final String sTableNamePrefix)
  {
    ValueEnforcer.notNull (aDBExecutorSupplier, "DBExecutorSupplier");
    ValueEnforcer.notNull (sTableNamePrefix, "TableNamePrefix");

    m_aDBExecutorSupplier = aDBExecutorSupplier;
    m_sTableNamePrefix = sTableNamePrefix;
  }

  @Nonnull
  public ESuccess storePeppolReport (@Nonnull final PeppolReportData aReportData)
  {
    ValueEnforcer.notNull (aReportData, "ReportData");

    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("Trying to store Peppol Report in SQL DB");

    final DBExecutor aExecutor = m_aDBExecutorSupplier.get ();
    final ESuccess eSuccess = aExecutor.performInTransaction ( () -> {
      // Create new
      final long nCreated = aExecutor.insertOrUpdateOrDelete ("INSERT INTO " +
                                                              m_sTableNamePrefix +
                                                              "peppol_report (reptype, repyear, repmonth, repcreatedt, report, repvalid)" +
                                                              " VALUES (?, ?, ?, ?, ?, ?)",
                                                              new ConstantPreparedStatementDataProvider (DBValueHelper.getTrimmedToLength (aReportData.getReportType ()
                                                                                                                                                      .getID (),
                                                                                                                                           EPeppolReportType.MAX_LEN_ID),
                                                                                                         Integer.valueOf (aReportData.getReportPeriod ()
                                                                                                                                     .getYear ()),
                                                                                                         Integer.valueOf (aReportData.getReportPeriod ()
                                                                                                                                     .getMonthValue ()),
                                                                                                         DBValueHelper.toTimestamp (aReportData.getReportCreationDT ()),
                                                                                                         aReportData.getReportXMLString (),
                                                                                                         Boolean.valueOf (aReportData.isReportValid ())));
      if (nCreated != 1)
        throw new IllegalStateException ("Failed to create new SQL DB entry (" + nCreated + ")");
    });
    if (eSuccess.isFailure ())
      throw new IllegalStateException ("Failed to insert Peppol Report into SQL DB");

    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("Successfully stored Peppol Report in SQL DB");

    return ESuccess.SUCCESS;
  }

  @Nonnull
  public ESuccess storePeppolReportingSendingReport (@Nonnull final PeppolReportSendingReportData aSendingReportData)
  {
    ValueEnforcer.notNull (aSendingReportData, "aSendingReportData");

    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("Trying to store Peppol Report Sending Report in SQL DB");

    final DBExecutor aExecutor = m_aDBExecutorSupplier.get ();
    final ESuccess eSuccess = aExecutor.performInTransaction ( () -> {
      // Create new
      final long nCreated = aExecutor.insertOrUpdateOrDelete ("INSERT INTO " +
                                                              m_sTableNamePrefix +
                                                              "peppol_sending_report (reptype, repyear, repmonth, repcreatedt, sendingreport)" +
                                                              " VALUES (?, ?, ?, ?, ?)",
                                                              new ConstantPreparedStatementDataProvider (DBValueHelper.getTrimmedToLength (aSendingReportData.getReportType ()
                                                                                                                                                             .getID (),
                                                                                                                                           EPeppolReportType.MAX_LEN_ID),
                                                                                                         Integer.valueOf (aSendingReportData.getReportPeriod ()
                                                                                                                                            .getYear ()),
                                                                                                         Integer.valueOf (aSendingReportData.getReportPeriod ()
                                                                                                                                            .getMonthValue ()),
                                                                                                         DBValueHelper.toTimestamp (aSendingReportData.getReportCreationDT ()),
                                                                                                         aSendingReportData.getSendingReportContent ()));
      if (nCreated != 1)
        throw new IllegalStateException ("Failed to create new SQL DB entry (" + nCreated + ")");
    });
    if (eSuccess.isFailure ())
      throw new IllegalStateException ("Failed to insert Peppol Report Sending Report into SQL DB");

    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("Successfully stored Peppol Report Sending Report in SQL DB");

    return ESuccess.SUCCESS;
  }
}
