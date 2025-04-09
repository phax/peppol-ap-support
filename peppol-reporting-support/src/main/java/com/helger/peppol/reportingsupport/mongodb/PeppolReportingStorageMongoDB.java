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

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Date;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.state.ESuccess;
import com.helger.commons.typeconvert.TypeConverter;
import com.helger.config.IConfig;
import com.helger.peppol.reporting.backend.mongodb.MongoClientWrapper;
import com.helger.peppol.reporting.backend.mongodb.PeppolReportingBackendMongoDBSPI;
import com.helger.peppol.reportingsupport.EPeppolReportType;
import com.helger.peppol.reportingsupport.IPeppolReportingStorage;
import com.helger.peppol.reportingsupport.model.PeppolReportData;

/**
 * Implementation of {@link IPeppolReportingStorage} for MongoDB backend.
 */
public class PeppolReportingStorageMongoDB implements IPeppolReportingStorage, AutoCloseable
{
  public static final String DEFAULT_COLLECTION_PEPPOL_REPORTS = "peppol-reports";
  public static final String DEFAULT_COLLECTION_PEPPOL_SENDING_REPORTS = "peppol-sending-reports";

  public static final String BSON_REPORT_TYPE = "reporttype";
  public static final String BSON_YEAR = "year";
  public static final String BSON_MONTH = "month";
  public static final String BSON_CREATION_DT = "creationdt";
  public static final String BSON_PAYLOAD = "payload";
  public static final String BSON_PAYLOAD_VALID = "payloadvalid";

  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolReportingStorageMongoDB.class);

  private MongoClientWrapper m_aMongoDBClient;
  private String m_sCollectionPeppolReports;
  private String m_sCollectionPeppolSendingReports;

  public PeppolReportingStorageMongoDB (@Nonnull final IConfig aConfig)
  {
    ValueEnforcer.notNull (aConfig, "Config");
    m_aMongoDBClient = PeppolReportingBackendMongoDBSPI.createDefaultClientWrapper (aConfig);
    m_sCollectionPeppolReports = DEFAULT_COLLECTION_PEPPOL_REPORTS;
    m_sCollectionPeppolSendingReports = DEFAULT_COLLECTION_PEPPOL_SENDING_REPORTS;
  }

  public void close ()
  {
    if (m_aMongoDBClient != null)
      m_aMongoDBClient.close ();
  }

  /**
   * Set the MongoDB client to be used.
   *
   * @param aClient
   *        The new client to be used. May not be <code>null</code>.
   * @return this for chaining
   */
  @Nonnull
  public PeppolReportingStorageMongoDB setMongoDBClient (@Nonnull final MongoClientWrapper aClient)
  {
    ValueEnforcer.notNull (aClient, "Client");
    m_aMongoDBClient = aClient;
    return this;
  }

  @Nonnull
  @Nonempty
  public String getCollectionPeppolReports ()
  {
    return m_sCollectionPeppolReports;
  }

  /**
   * Change the collection name for Peppol Reports
   *
   * @param s
   *        Collection name to use. May neither be <code>null</code> nor empty.
   * @return this for chaining
   */
  @Nonnull
  public PeppolReportingStorageMongoDB setCollectionPeppolReports (@Nonnull @Nonempty final String s)
  {
    ValueEnforcer.notEmpty (s, "CollectionPeppolReports");

    if (!m_sCollectionPeppolReports.equals (s))
    {
      LOGGER.info ("Using MongoDB collection name '" + s + "' to store Peppol Reports");
      m_sCollectionPeppolReports = s;
    }
    return this;
  }

  @Nonnull
  @Nonempty
  public String getCollectionPeppolSendingReports ()
  {
    return m_sCollectionPeppolSendingReports;
  }

  /**
   * Change the collection name for Peppol Sending Reports
   *
   * @param s
   *        Collection name to use. May neither be <code>null</code> nor empty.
   * @return this for chaining
   */
  @Nonnull
  public PeppolReportingStorageMongoDB setCollectionPeppolSendingReports (@Nonnull @Nonempty final String s)
  {
    ValueEnforcer.notEmpty (s, "CollectionPeppolSendingReports");

    if (!m_sCollectionPeppolSendingReports.equals (s))
    {
      LOGGER.info ("Using MongoDB collection name '" + s + "' to store Peppol Sending Reports");
      m_sCollectionPeppolSendingReports = s;
    }
    return this;
  }

  @Nonnull
  static Document toBson (@Nonnull final PeppolReportData aReportData)
  {
    return new Document ().append (BSON_REPORT_TYPE, aReportData.getReportType ().getID ())
                          .append (BSON_YEAR, Integer.valueOf (aReportData.getReportPeriod ().getYear ()))
                          .append (BSON_MONTH, Integer.valueOf (aReportData.getReportPeriod ().getMonthValue ()))
                          .append (BSON_CREATION_DT,
                                   TypeConverter.convert (aReportData.getReportCreationDT (), Date.class))
                          .append (BSON_PAYLOAD,
                                   aReportData.getReportXMLBytes ().getBytesAsString (StandardCharsets.UTF_8))
                          .append (BSON_PAYLOAD_VALID, Boolean.valueOf (aReportData.isReportValid ()));
  }

  @Nonnull
  public ESuccess storePeppolReport (@Nonnull final PeppolReportData aReportData)
  {
    ValueEnforcer.notNull (aReportData, "ReportData");

    if (m_aMongoDBClient == null)
    {
      LOGGER.error ("Failed to init MongoDB client - not storing Peppol report");
      return ESuccess.FAILURE;
    }
    if (!m_aMongoDBClient.isDBWritable ())
    {
      LOGGER.error ("MongoDB is not writable - not storing Peppol report");
      return ESuccess.FAILURE;
    }

    // Create MongoDB document
    final Document aBson = toBson (aReportData);

    // Write to collection
    if (!m_aMongoDBClient.getCollection (m_sCollectionPeppolReports).insertOne (aBson).wasAcknowledged ())
      throw new IllegalStateException ("Failed to insert into Peppol Reporting MongoDB Collection");

    return ESuccess.SUCCESS;
  }

  @Nonnull
  public ESuccess storePeppolReportSendingReport (@Nonnull final EPeppolReportType eReportType,
                                                  @Nonnull final YearMonth aReportPeriod,
                                                  @Nonnull final LocalDateTime aReportCreationDT,
                                                  @Nullable final String sSendingReportBytes)
  {
    ValueEnforcer.notNull (eReportType, "ReportType");
    ValueEnforcer.notNull (aReportPeriod, "ReportPeriod");
    ValueEnforcer.notNull (aReportCreationDT, "ReportCreationDT");

    if (m_aMongoDBClient == null)
    {
      LOGGER.error ("Failed to init MongoDB client - not storing Peppol sending report");
      return ESuccess.FAILURE;
    }
    if (!m_aMongoDBClient.isDBWritable ())
    {
      LOGGER.error ("MongoDB is not writable - not storing Peppol sending report");
      return ESuccess.FAILURE;
    }

    // Create MongoDB document
    final Document aBson = new Document ().append (BSON_REPORT_TYPE, eReportType.getID ())
                                          .append (BSON_YEAR, Integer.valueOf (aReportPeriod.getYear ()))
                                          .append (BSON_MONTH, Integer.valueOf (aReportPeriod.getMonthValue ()))
                                          .append (BSON_CREATION_DT,
                                                   TypeConverter.convert (aReportCreationDT, Date.class));
    if (sSendingReportBytes != null)
      aBson.append (BSON_PAYLOAD, sSendingReportBytes);

    // Write to collection
    if (!m_aMongoDBClient.getCollection (m_sCollectionPeppolSendingReports).insertOne (aBson).wasAcknowledged ())
      throw new IllegalStateException ("Failed to insert into Peppol Reporting MongoDB Collection");

    return ESuccess.SUCCESS;
  }
}
