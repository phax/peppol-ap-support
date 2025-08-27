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

import com.helger.annotation.Nonempty;
import com.helger.base.id.IHasID;
import com.helger.base.lang.EnumHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Contains the list of possible Peppol Reporting report types.<br>
 * Each ID must not be longer than 12 characters, due to database column length constraint.
 *
 * @author Philip Helger
 */
public enum EPeppolReportType implements IHasID <String>
{
  /**
   * Peppol Transaction Statistics Report v1.0
   */
  TSR_V10 ("tsr10"),
  /**
   * Peppol End User Statistics Report v1.1
   */
  EUSR_V11 ("eusr11");

  /** The maximum length a report ID may used. This is required for DB column length */
  public static final int MAX_LEN_ID = 12;

  private final String m_sID;

  EPeppolReportType (@Nonnull @Nonempty final String sID)
  {
    m_sID = sID;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  /**
   * Get the {@link EPeppolReportType} matching the provided ID.
   *
   * @param sID
   *        The ID to search. May be <code>null</code>.
   * @return <code>null</code> if no hit was found
   */
  @Nullable
  public static EPeppolReportType getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EPeppolReportType.class, sID);
  }
}
