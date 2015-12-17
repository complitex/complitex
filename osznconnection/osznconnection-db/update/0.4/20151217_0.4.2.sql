-- Add street code attribute
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (303, 1, UPPER('Код улицы')),(303, 2, UPPER('Код улицы'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (303, 300, 0, 303, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (303, 303, 'STREET_CODES');

-- --------------------------------
-- Current database version
-- --------------------------------
INSERT INTO `update` (`version`) VALUE ('20151217_0.4.2');