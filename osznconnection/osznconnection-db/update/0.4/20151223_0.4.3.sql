ALTER TABLE `address_sync` ADD COLUMN `servicing_organization` VARCHAR(16) COMMENT 'Обслуживающая организация';
ALTER TABLE `address_sync` ADD COLUMN `balance_holder` VARCHAR(16) COMMENT 'Балансодержатель';

ALTER TABLE `address_sync` ADD KEY `key_servicing_organization` (`servicing_organization`);
ALTER TABLE `address_sync` ADD KEY `key_balance_holder` (`balance_holder`);

-- --------------------------------
-- Current database version
-- --------------------------------
INSERT INTO `update` (`version`) VALUE ('20151223_0.4.3');