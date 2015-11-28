-- --------------------------------
-- Organization type
-- --------------------------------

INSERT INTO `organization_type`(`object_id`) VALUES (7);
INSERT INTO `organization_type_string_culture`(`id`, `locale_id`, `value`)
VALUES (7, 1, UPPER('Отдел субсидий')), (7, 2, UPPER('Отдел субсидий'));
INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`)
VALUES (1, 7, 2300, 7, 2300);

INSERT INTO `organization_type`(`object_id`) VALUES (8);
INSERT INTO `organization_type_string_culture`(`id`, `locale_id`, `value`)
VALUES (8, 1, UPPER('Отдел льгот')), (8, 2, UPPER('Отдел льгот'));
INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`)
VALUES (1, 8, 2300, 8, 2300);

-- --------------------------------
-- Current database version
-- --------------------------------
INSERT INTO `update` (`version`) VALUE ('20151128_0.4.1');