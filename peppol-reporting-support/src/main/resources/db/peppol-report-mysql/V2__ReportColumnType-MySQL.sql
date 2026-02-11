-- Change type from "text" to "mediumtext"
ALTER TABLE `peppol_report`         MODIFY `report` MEDIUMTEXT NOT NULL;
ALTER TABLE `peppol_sending_report` MODIFY `sendingreport` MEDIUMTEXT DEFAULT NULL;
