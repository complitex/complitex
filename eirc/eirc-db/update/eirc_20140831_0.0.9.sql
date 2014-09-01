SET autocommit=0;

-- --------------------------------
-- Current database version
-- --------------------------------
INSERT INTO `update` (`version`) VALUE ('eirc_20140831_0.0.9');

alter table `charge` drop foreign key `fk_charge__registry_record_container`;
alter table `charge` add constraint `fk_charge__registry_record_container` FOREIGN KEY (`registry_record_container_id`) REFERENCES `registry_record_container` (`id`) ON DELETE SET NULL;

alter table `saldo_out` drop foreign key `fk_saldo_out__registry_record_container`;
alter table `saldo_out` add constraint `fk_saldo_out__registry_record_container` FOREIGN KEY (`registry_record_container_id`) REFERENCES `registry_record_container` (`id`) ON DELETE SET NULL;

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (6007, 1, UPPER('Л/с на закрытие')), (6007, 2, UPPER('Л/п на закриття'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (6006, 6000, 0, 6007, 1);
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (6006, 6006, UPPER('other'));

COMMIT;
SET autocommit=1;