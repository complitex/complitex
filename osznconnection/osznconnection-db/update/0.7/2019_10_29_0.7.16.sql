UPDATE `update` SET `version` =  '20190529_0.7.15' WHERE `version` = '20190529_0.7.14';

INSERT INTO `request_file_field_description` (`request_file_description_id`, `length`, `type`, `name`)
    VALUE (14, 1, 'java.lang.Integer', 'MONEY');

ALTER TABLE `privilege_prolongation` ADD COLUMN `MONEY` INTEGER(1);

INSERT INTO `request_file_field_description` (`request_file_description_id`, `length`, `type`, `name`)
    VALUE (14, 10, 'java.lang.String', 'EBK');

ALTER TABLE `privilege_prolongation` ADD COLUMN `EBK` VARCHAR(10);

INSERT INTO `update` (`version`) VALUE ('20191029_0.7.16');