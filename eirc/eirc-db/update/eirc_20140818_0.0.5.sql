SET autocommit=0;

-- --------------------------------
-- Current database version
-- --------------------------------
INSERT INTO `update` (`version`) VALUE ('eirc_20140818_0.0.5');

-- --------------------------------
-- Изменение дополнительных атрибутов л/с ПУ из записи реестра
-- --------------------------------

DROP TABLE IF EXISTS `registry_changing_spa_attribute`;

CREATE TABLE `registry_changing_spa_attribute` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор изменения',
  `spa_old_attribute_pk_id` BIGINT(20) COMMENT 'Идентификатор предыдущего значения дополнительного атрибута л/с ПУ',
  `spa_new_attribute_pk_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор нового значения дополнительного атрибута л/с ПУ',
  `registry_record_container_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор контейнера записи реестра сделавшей изменение',
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_registry_changing_spa_attribute__spa_attribute_new` FOREIGN KEY (`spa_new_attribute_pk_id`)
  REFERENCES `service_provider_account_attribute` (`pk_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_registry_changing_spa_attribute__spa_attribute_old` FOREIGN KEY (`spa_old_attribute_pk_id`)
  REFERENCES `service_provider_account_attribute` (`pk_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_registry_changing_spa_attribute__registry_record_container` FOREIGN KEY (`registry_record_container_id`)
  REFERENCES `registry_record_container` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Изменение дополнительных атрибутов л/с ПУ из записи реестра';

COMMIT;
SET autocommit=1;