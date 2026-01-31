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
package com.helger.peppol.reportingsupport.file;

import java.io.File;

import org.jspecify.annotations.NonNull;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.state.ESuccess;
import com.helger.peppol.reportingsupport.IPeppolReportStorage;
import com.helger.peppol.reportingsupport.domain.PeppolReportData;
import com.helger.peppol.reportingsupport.domain.PeppolReportSendingReportData;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.MicroDocument;
import com.helger.xml.microdom.serialize.MicroWriter;

/**
 * Implementation of {@link IPeppolReportStorage} to write the data as XML on disk
 *
 * @author Philip Helger
 */
public class PeppolReportStorageFileXML implements IPeppolReportStorage
{
  private final File m_aBaseDir;
  private final IPeppolReportStorageFilenameProvider m_aFilenameProvider;

  /**
   * Constructor
   *
   * @param aBaseDir
   *        The base directory to store all files. May not be <code>null</code>.
   * @param aFilenameProvider
   *        The dynamic filename provider. May not be <code>null</code>.
   */
  public PeppolReportStorageFileXML (@NonNull final File aBaseDir,
                                     @NonNull final IPeppolReportStorageFilenameProvider aFilenameProvider)
  {
    ValueEnforcer.notNull (aBaseDir, "BaseDir");
    ValueEnforcer.notNull (aFilenameProvider, "FilenameProvider");
    m_aBaseDir = aBaseDir;
    m_aFilenameProvider = aFilenameProvider;
  }

  @NonNull
  public ESuccess storePeppolReport (@NonNull final PeppolReportData aReportData)
  {
    final IMicroDocument aDoc = new MicroDocument ();
    aDoc.addChild (aReportData.getAsMicroElement (null, "PeppolReportData"));
    final File aTarget = new File (m_aBaseDir,
                                   m_aFilenameProvider.getFilename (aReportData.getReportPeriod (),
                                                                    aReportData.getReportType (),
                                                                    aReportData.getReportCreationDT (),
                                                                    "peppol-report.xml"));
    return MicroWriter.writeToFile (aDoc, aTarget);
  }

  @NonNull
  public ESuccess storePeppolReportingSendingReport (@NonNull final PeppolReportSendingReportData aSendingReportData)
  {
    final IMicroDocument aDoc = new MicroDocument ();
    aDoc.addChild (aSendingReportData.getAsMicroElement (null, "SendingReportData"));
    final File aTarget = new File (m_aBaseDir,
                                   m_aFilenameProvider.getFilename (aSendingReportData.getReportPeriod (),
                                                                    aSendingReportData.getReportType (),
                                                                    aSendingReportData.getReportCreationDT (),
                                                                    "sending-report.xml"));
    return MicroWriter.writeToFile (aDoc, aTarget);
  }
}
