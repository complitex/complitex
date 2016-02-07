ALTER TABLE payment MODIFY COLUMN `ENT_COD` BIGINT(10) COMMENT 'Код ЖЭО ОКПО';
UPDATE  request_file_field_description SET type = 'java.lang.Long' WHERE name = 'ENT_COD' AND type = 'java.lang.Integer';

ALTER TABLE payment MODIFY COLUMN `RESERV1` BIGINT(10) COMMENT 'Резерв';
UPDATE  request_file_field_description SET type = 'java.lang.Long' WHERE name = 'RESERV1' AND type = 'java.lang.Integer';

UPDATE config SET `value` =  '{\d{8}|\d{10}}{MM}{YY}\d\d\.DBF' WHERE `name` = 'PAYMENT_BENEFIT_FILENAME_SUFFIX';

INSERT INTO `update` (`version`) VALUE ('20160208_0.4.7');