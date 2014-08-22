SET autocommit=0;

-- --------------------------------
-- Current database version
-- --------------------------------
INSERT INTO `update` (`version`) VALUE ('eirc_20140821_0.0.6');

-- --------------------------------
-- Изменение финансовых атрибутов л/с ПУ из записи реестра
-- --------------------------------

alter table `saldo_out` add column `registry_record_container_id` BIGINT(20) COMMENT 'Идентификатор контейнера записи реестра сделавшей изменение';
alter table `saldo_out` add constraint `fk_saldo_out__registry_record_container` FOREIGN KEY (`registry_record_container_id`) REFERENCES `registry_record_container` (`id`) ON UPDATE SET NULL;

alter table `charge` add column `registry_record_container_id` BIGINT(20) COMMENT 'Идентификатор контейнера записи реестра сделавшей изменение';
alter table `charge` add constraint `fk_charge__registry_record_container` FOREIGN KEY (`registry_record_container_id`) REFERENCES `registry_record_container` (`id`) ON UPDATE SET NULL;

COMMIT;
SET autocommit=1;