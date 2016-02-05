ALTER TABLE request_file MODIFY COLUMN `name` VARCHAR(22) NOT NULL COMMENT 'Имя файла';

INSERT INTO `update` (`version`) VALUE ('20160206_0.4.6');