--
-- Copyright (C) 2025 Philip Helger
-- philip[at]helger[dot]com
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--         http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

CREATE TABLE peppol_report (
  reptype     varchar(12)  NOT NULL,
  repyear     smallint     NOT NULL,
  repmonth    smallint     NOT NULL,
  -- Use up to millisecond precision
  repcreatedt timestamp(3) NOT NULL,
  report      text         NOT NULL,
  repvalid    boolean      NOT NULL
);

CREATE INDEX peppol_report_idx ON peppol_report (reptype, repyear, repmonth);

CREATE TABLE peppol_sending_report (
  reptype       varchar(12)  NOT NULL,
  repyear       smallint     NOT NULL,
  repmonth      smallint     NOT NULL,
  -- Use up to millisecond precision
  repcreatedt   timestamp(3) NOT NULL,
  sendingreport text         DEFAULT NULL
);

CREATE INDEX peppol_sending_report_idx ON peppol_sending_report (reptype, repyear, repmonth);
