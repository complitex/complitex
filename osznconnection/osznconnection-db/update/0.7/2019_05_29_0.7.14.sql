INSERT INTO `request_file_field_description` (`request_file_description_id`, `length`, `type`, `name`)
    VALUE (4, 10, 'java.lang.String', 'EBK');

ALTER TABLE subsidy ADD COLUMN `EBK` VARCHAR(10);

INSERT INTO `update` (`version`) VALUE ('20190529_0.7.14');