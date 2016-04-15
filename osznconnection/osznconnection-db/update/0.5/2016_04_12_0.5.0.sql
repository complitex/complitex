ALTER TABLE `request_file` DROP COLUMN `registry`;
ALTER TABLE `request_file` DROP FOREIGN KEY `fk_request_file__request_file_group`;

INSERT INTO `update` (`version`) VALUE ('20160412_0.5.0');