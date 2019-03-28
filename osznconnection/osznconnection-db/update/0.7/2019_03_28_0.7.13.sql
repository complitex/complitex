ALTER TABLE oschadbank_request ADD COLUMN `account_number` VARCHAR(100) NULL COMMENT 'Номер счета';
ALTER TABLE oschadbank_request ADD COLUMN `status` INTEGER NOT NULL DEFAULT 240 COMMENT 'Статус';

INSERT INTO `update` (`version`) VALUE ('20190328_0.7.13');