SET autocommit=0;

-- --------------------------------
-- Current database version
-- --------------------------------
INSERT INTO `update` (`version`) VALUE ('eirc_20140826_0.0.8');

-- --------------------------------
-- Изменение изменяемых во времени данных из записи реестра
-- --------------------------------

DROP TABLE IF EXISTS `registry_changing_spa_attribute`;

DROP TABLE IF EXISTS `registry_changing`;

CREATE TABLE `registry_changing` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор изменения',
  `old_pk_id` BIGINT(20) COMMENT 'Идентификатор предыдущего значения',
  `new_pk_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор нового значения',
  `registry_record_container_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор контейнера записи реестра сделавшей изменение',
  `object_type` VARCHAR(255),
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_registry_changing_spa_attribute__registry_record_container` FOREIGN KEY (`registry_record_container_id`)
  REFERENCES `registry_record_container` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Изменение изменяемых во времени данных из записи реестра';

alter table `service_provider_account` drop index `fk_service_provider_account__registry_record_container`;

ALTER TABLE `service_provider_account` ADD COLUMN `registry_record_container_id` BIGINT(20) COMMENT 'Идентификатор контейнера записи реестра сделавшей изменение';
ALTER TABLE `service_provider_account` ADD CONSTRAINT `fk_service_provider_account__registry_record_container` FOREIGN KEY (`registry_record_container_id`)
REFERENCES `registry_record_container` (`id`) ON DELETE SET NULL;

ALTER TABLE `eirc_account` ADD COLUMN `created_from_registry` BOOLEAN COMMENT 'В TRUE, если ЕИРЦ счет создан из реестра';

COMMIT;
SET autocommit=1;