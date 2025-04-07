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
package com.helger.peppol.reportingsupport;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.peppolid.IDocumentTypeIdentifier;
import com.helger.peppolid.IProcessIdentifier;

/**
 * Callback to actually send Peppol messages. This is meant to be an abstraction layer to either
 * call phase4 locally or e.g. an HTTP service to trigger the actual sending.
 *
 * @author Philip Helger
 */
interface IPeppolReportSenderCallback
{
  /**
   * Send a Peppol message via AS4
   *
   * @param aDocTypeID
   *        The document type identifier to be used. Never <code>null</code>.
   * @param aProcessID
   *        The process identifier to be used. Never <code>null</code>.
   * @param aMessagePayload
   *        The message payload to be send. Neither <code>null</code> nor empty.
   * @return The sending report. If an error occurred, it must be part of the sending report.
   */
  @Nonnull
  byte [] sendPeppolMessage (@Nonnull IDocumentTypeIdentifier aDocTypeID,
                             @Nonnull IProcessIdentifier aProcessID,
                             @Nonnull @Nonempty byte [] aMessagePayload);
}
