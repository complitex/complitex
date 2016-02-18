ALTER TABLE payment MODIFY COLUMN `OWN_NUM` BIGINT(15) COMMENT 'Номер дела';
ALTER TABLE benefit MODIFY COLUMN `OWN_NUM` BIGINT(15) COMMENT 'Номер дела';

UPDATE  request_file_field_description SET type = 'java.lang.Long' WHERE name = 'OWN_NUM' AND type = 'java.lang.Integer';

INSERT INTO `update` (`version`) VALUE ('20160219_0.4.9');