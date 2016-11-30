INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (929, 1, UPPER('Директория исходящих файлов местной льготы')), (929, 2, UPPER('Директория исходящих файлов местной льготы'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (929, 900, 0, 929, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (929, 929, UPPER('string'));

INSERT INTO `update` (`version`) VALUE ('20161130_0.5.7');