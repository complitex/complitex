SET autocommit=0;

-- --------------------------------
-- Current database version
-- --------------------------------
INSERT INTO `update` (`version`) VALUE ('eirc_20140822_0.0.7');

INSERT INTO `container_type` (`code`, `name`) values (504,'Реквизиты платежного документа');

-- --------------------------------
-- Добавление оплат по л/с ПУ из записи реестра
-- --------------------------------

alter table `cash_payment` add column `registry_record_container_id` BIGINT(20) COMMENT 'Идентификатор контейнера записи реестра сделавшей изменение';
alter table `cash_payment` add constraint `fk_cash_payment__registry_record_container` FOREIGN KEY (`registry_record_container_id`) REFERENCES `registry_record_container` (`id`) ON UPDATE SET NULL;

alter table `cashless_payment` add column `registry_record_container_id` BIGINT(20) COMMENT 'Идентификатор контейнера записи реестра сделавшей изменение';
alter table `cashless_payment` add constraint `fk_cashless_payment__registry_record_container` FOREIGN KEY (`registry_record_container_id`) REFERENCES `registry_record_container` (`id`) ON UPDATE SET NULL;

COMMIT;
SET autocommit=1;