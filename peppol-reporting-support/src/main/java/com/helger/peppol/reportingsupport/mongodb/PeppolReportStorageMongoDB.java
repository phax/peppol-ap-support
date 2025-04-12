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

import java.util.Date;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.state.ESuccess;
import com.helger.commons.typeconvert.TypeConverter;
import com.helger.peppol.reporting.backend.mongodb.MongoClientWrapper;
import com.helger.peppol.reportingsupport.IPeppolReportStorage;
import com.helger.peppol.reportingsupport.domain.PeppolReportData;
import com.helger.peppol.reportingsupport.domain.PeppolReportSendingReportData;

/**
 * Implementation of {@link IPeppolReportStorage} for MongoDB backend.
 */
public class PeppolReportStorageMongoDB implements IPeppolReportStorage
{
  /** Default collection name */
  public static final String DEFAULT_COLLECTION_NAME_PEPPOL_REPORTS = "peppol-reports";
  /** Default collection name */
  public static final String DEFAULT_COLLECTION_NAME_PEPPOL_REPORTING_SENDING_REPORTS = "peppol-reporting-sending-reports";

  /** BSON field report type */
  public static final String BSON_REPORT_TYPE = "reporttype";
  /** BSON field report year */
  public static final String BSON_YEAR = "year";
  /** BSON field report month */
  public static final String BSON_MONTH = "month";
  /** BSON field report creation date and time */
  public static final String BSON_CREATION_DT = "creationdt";
  /** BSON field report payload */
  public static final String BSON_PAYLOAD = "payload";
  /** BSON field report validity */
  public static final String BSON_PAYLOAD_VALID = "payloadvalid";

  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolReportStorageMongoDB.class);

  private final Supplier <? extends MongoClientWrapper> m_aMongoClientSupplier;
  private String m_sCollectionNamePeppolReports;
  private String m_sCollectionNamePeppolReportingSendingReports;

  /**
   * Constructor
   *
   * @param aMongoClientSupplier
   *        The support for the MongoDB client wrapper. May not be <code>null</code>.
   */
  public PeppolReportStorageMongoDB (@Nonnull final Supplier <? extends MongoClientWrapper> aMongoClientSupplier)
  {
    ValueEnforcer.notNull (aMongoClientSupplier, "MongoClientSupplier");
    m_aMongoClientSupplier = aMongoClientSupplier;
    m_sCollectionNamePeppolReports = DEFAULT_COLLECTION_NAME_PEPPOL_REPORTS;
    m_sCollectionNamePeppolReportingSendingReports = DEFAULT_COLLECTION_NAME_PEPPOL_REPORTING_SENDING_REPORTS;
  }

  /**
   * Get the collection name for Peppol Reports
   *
   * @return The collection name for Peppol Reports. Neither <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  public String getCollectionNamePeppolReports ()
  {
    return m_sCollectionNamePeppolReports;
  }

  /**
   * Change the collection name for Peppol Reports
   *
   * @param s
   *        Collection name to use. May neither be <code>null</code> nor empty.
   * @return this for chaining
   */
  @Nonnull
  public PeppolReportStorageMongoDB setCollectionNamePeppolReports (@Nonnull @Nonempty final String s)
  {
    ValueEnforcer.notEmpty (s, "CollectionNamePeppolReports");

    if (!m_sCollectionNamePeppolReports.equals (s))
    {
      LOGGER.info ("Using MongoDB collection name '" + s + "' to store Peppol Reports");
      m_sCollectionNamePeppolReports = s;
    }
    return this;
  }

  /**
   * Get the collection name for Peppol Reporting Sending Reports
   *
   * @return The collection name for Peppol Reporting Sending Reports. Neither <code>null</code> nor
   *         empty.
   */
  @Nonnull
  @Nonempty
  public String getCollectionNamePeppolReportingSendingReports ()
  {
    return m_sCollectionNamePeppolReportingSendingReports;
  }

  /**
   * Change the collection name for Peppol Reporting Sending Reports
   *
   * @param s
   *        Collection name to use. May neither be <code>null</code> nor empty.
   * @return this for chaining
   */
  @Nonnull
  public PeppolReportStorageMongoDB setCollectionNamePeppolReportingSendingReports (@Nonnull @Nonempty final String s)
  {
    ValueEnforcer.notEmpty (s, "CollectionPeppolReportingSendingReports");

    if (!m_sCollectionNamePeppolReportingSendingReports.equals (s))
    {
      LOGGER.info ("Using MongoDB collection name '" + s + "' to store Peppol Reporting Sending Reports");
      m_sCollectionNamePeppolReportingSendingReports = s;
    }
    return this;
  }

  /**
   * Convert the object to BSON
   *
   * @param aReportData
   *        Object to convert. May not be <code>null</code>.
   * @return The MongoDB document and never <code>null</code>.
   */
  @Nonnull
  static Document toBson (@Nonnull final PeppolReportData aReportData)
  {
    return new Document ().append (BSON_REPORT_TYPE, aReportData.getReportType ().getID ())
                          .append (BSON_YEAR, Integer.valueOf (aReportData.getReportPeriod ().getYear ()))
                          .append (BSON_MONTH, Integer.valueOf (aReportData.getReportPeriod ().getMonthValue ()))
                          .append (BSON_CREATION_DT,
                                   TypeConverter.convert (aReportData.getReportCreationDT (), Date.class))
                          .append (BSON_PAYLOAD, aReportData.getReportXMLString ())
                          .append (BSON_PAYLOAD_VALID, Boolean.valueOf (aReportData.isReportValid ()));
  }

  @Nonnull
  public ESuccess storePeppolReport (@Nonnull final PeppolReportData aReportData)
  {
    ValueEnforcer.notNull (aReportData, "ReportData");

    final MongoClientWrapper aMongoDBClient = m_aMongoClientSupplier.get ();
    if (aMongoDBClient == null)
    {
      LOGGER.error ("Failed to init MongoDB client - not storing Peppol Report");
      return ESuccess.FAILURE;
    }
    if (!aMongoDBClient.isDBWritable ())
    {
      LOGGER.error ("MongoDB is not writable - not storing Peppol Report");
      return ESuccess.FAILURE;
    }

    // Create MongoDB document
    final Document aBson = toBson (aReportData);

    // Write to collection
    if (!aMongoDBClient.getCollection (m_sCollectionNamePeppolReports).insertOne (aBson).wasAcknowledged ())
      throw new IllegalStateException ("Failed to insert into Peppol Reports MongoDB Collection");

    return ESuccess.SUCCESS;
  }

  /**
   * Convert the object to BSON
   *
   * @param aSendingReportData
   *        Object to convert. May not be <code>null</code>.
   * @return The MongoDB document and never <code>null</code>.
   */
  @Nonnull
  static Document toBson (@Nonnull final PeppolReportSendingReportData aSendingReportData)
  {
    final Document ret = new Document ().append (BSON_REPORT_TYPE, aSendingReportData.getReportType ().getID ())
                                        .append (BSON_YEAR,
                                                 Integer.valueOf (aSendingReportData.getReportPeriod ().getYear ()))
                                        .append (BSON_MONTH,
                                                 Integer.valueOf (aSendingReportData.getReportPeriod ()
                                                                                    .getMonthValue ()))
                                        .append (BSON_CREATION_DT,
                                                 TypeConverter.convert (aSendingReportData.getReportCreationDT (),
                                                                        Date.class));
    if (aSendingReportData.hasSendingReportContent ())
      ret.append (BSON_PAYLOAD, aSendingReportData.getSendingReportContent ());
    return ret;
  }

  @Nonnull
  public ESuccess storePeppolReportingSendingReport (@Nonnull final PeppolReportSendingReportData aSendingReportData)
  {
    ValueEnforcer.notNull (aSendingReportData, "aSendingReportData");

    final MongoClientWrapper aMongoDBClient = m_aMongoClientSupplier.get ();
    if (aMongoDBClient == null)
    {
      LOGGER.error ("Failed to init MongoDB client - not storing Peppol Reporting Sending Report");
      return ESuccess.FAILURE;
    }
    if (!aMongoDBClient.isDBWritable ())
    {
      LOGGER.error ("MongoDB is not writable - not storing Peppol Reporting Sending Report");
      return ESuccess.FAILURE;
    }

    // Create MongoDB document
    final Document aBson = toBson (aSendingReportData);

    // Write to collection
    if (!aMongoDBClient.getCollection (m_sCollectionNamePeppolReportingSendingReports)
                       .insertOne (aBson)
                       .wasAcknowledged ())
      throw new IllegalStateException ("Failed to insert into Peppol Reporting Sending Reports MongoDB Collection");

    return ESuccess.SUCCESS;
  }
}
