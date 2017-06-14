-- Set mysql user-defined variable - system locale id.
SELECT (@system_locale_id := `id`) FROM `locale` WHERE `system` = 1;

-- Owner relationship --
INSERT INTO `owner_relationship`(`object_id`) VALUES (4),(5),(6),(7),(8);
INSERT INTO `owner_relationship_string_value`(`id`, `locale_id`, `value`) VALUES
(4, 1, UPPER('ответственный квартиросьемщик')), (4, 2,UPPER('УПОВНОВАЖЕНИЙ ВЛАСНИК')),
(5, 1, UPPER('гость')), (5, 2, UPPER('IНША ОСОБА')),
(6, 1, UPPER('муж')), (6, 2, UPPER('ЧОЛОВIК')),
(7, 1, UPPER('жена')), (7, 2, UPPER('ДРУЖИНА')),
(8, 1, UPPER('дальний родственник')), (8, 2, UPPER('IНШИЙ РОДИЧ'));
INSERT INTO `owner_relationship_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`) VALUES
(1,4,2200,4,2200),(1,5,2200,5,2200),(1,6,2200,6,2200),(1,7,2200,7,2200),(1,8,2200,8,2200);

-- Forms of ownerships -- 
INSERT INTO `ownership_form`(`object_id`) VALUES (1),(2),(3),(4),(5),(6),(7),(8);
INSERT INTO `ownership_form_string_value`(`id`, `locale_id`, `value`) VALUES (1, 1, UPPER('мiсцевих Рад')), (1, 2,UPPER('мiсцевих Рад')),
(2, 1, UPPER('кооперативна')), (2, 2, UPPER('кооперативна')), (3, 1, UPPER('вiдомча')), (3, 2, UPPER('вiдомча')),
(4, 1, UPPER('громадська')), (4, 2, UPPER('громадська')), (5, 1, UPPER('приватна')), (5, 2, UPPER('приватна')),
(6, 1, UPPER('приватизована')), (6, 2, UPPER('приватизована')), (7, 1, UPPER('викуплена')), (7, 2, UPPER('викуплена')),
(8, 1, UPPER('службова')), (8, 2, UPPER('службова'));
INSERT INTO `ownership_form_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`) VALUES
(1,1,2500,1,2500),(1,2,2500,2,2500),(1,3,2500,3,2500),(1,4,2500,4,2500),(1,5,2500,5,2500),(1,6,2500,6,2500), (1,7,2500,7,2500),
 (1,8,2500,8,2500);

-- Registration type -- 
INSERT INTO `registration_type`(`object_id`) VALUES (2);
INSERT INTO `registration_type_string_value`(`id`, `locale_id`, `value`) VALUES (2, 1, UPPER('временная')), (2, 2, UPPER('временная'));
INSERT INTO `registration_type_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`) VALUES
(1,2,2600,2,2600);

-- Custom document types --
INSERT INTO `document_type`(`object_id`) VALUES (3);
INSERT INTO `document_type_string_value`(`id`, `locale_id`, `value`) VALUES (3, 1, UPPER('военный билет')), (3, 2, UPPER('военный билет'));
INSERT INTO `document_type_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`) VALUES
(1,3,2700,3,2700);

-- Militar service relation --
INSERT INTO `military_service_relation`(`object_id`) VALUES (1),(2),(3);
INSERT INTO `military_service_relation_string_value`(`id`, `locale_id`, `value`) VALUES (1, 1, UPPER('годен')), (1, 2, UPPER('годен'));
INSERT INTO `military_service_relation_string_value`(`id`, `locale_id`, `value`) VALUES (2, 1, UPPER('служил')), (2, 2, UPPER('служил'));
INSERT INTO `military_service_relation_string_value`(`id`, `locale_id`, `value`) VALUES (3, 1, UPPER('не годен')), (3, 2, UPPER('не годен'));
INSERT INTO `military_service_relation_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`) VALUES
(1,1,2900,1,2900),(1,2,2900,2,2900),(1,3,2900,3,2900);

-- Test user organizations
insert into `organization`(`object_id`) values (1),(2);
insert into `organization_string_value`(`id`, `locale_id`, `value`) values (1,@system_locale_id, UPPER('Паспортный стол №1')),(2,@system_locale_id, UPPER('3002')),
(3,@system_locale_id, UPPER('Паспортный стол №2')),(4,@system_locale_id, UPPER('3003'));
insert into `organization_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`) values
(1,1,900,1,900), (1,1,901,2,901), (1,1,904,1,904),
(1,2,900,3,900), (1,2,901,4,901), (1,2,904,1,904);

-- Test users
-- Plain employee user '1'
insert into `first_name` (`id`, `name`) values(3,'1');
insert into `last_name` (`id`, `name`) values(3,'1');
insert into `middle_name` (`id`, `name`) values(3,'1');

insert into `user_info` (`object_id`) values(3);
insert into `user_info_attribute` (`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`) values(1,3,1000,3,1000);
insert into `user_info_attribute` (`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`) values(1,3,1001,3,1001);
insert into `user_info_attribute` (`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`) values(1,3,1002,3,1002);

insert into `user` (`id`, `login`, `password`, `user_info_object_id`) values(3,'1','6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b',3);
insert into `user_organization` (`id`, `user_id`, `organization_object_id`, `main`) values(1,3,1,1);
insert into `usergroup` (`id`, `login`, `group_name`) values(4,'1','EMPLOYEES');

-- Employee-child-view  user '2'
insert into `first_name` (`id`, `name`) values(4,'2');
insert into `last_name` (`id`, `name`) values(4,'2');
insert into `middle_name` (`id`, `name`) values(4,'2');

insert into `user_info` (`object_id`) values(4);
insert into `user_info_attribute` (`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`) values(1,4,1000,4,1000);
insert into `user_info_attribute` (`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`) values(1,4,1001,4,1001);
insert into `user_info_attribute` (`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`) values(1,4,1002,4,1002);

insert into `user` (`id`, `login`, `password`, `user_info_object_id`) values(4,'2','d4735e3a265e16eee03f59718b9b5d03019c07d8b6c51f90da3a666eec13ab35',4);
insert into `user_organization` (`id`, `user_id`, `organization_object_id`, `main`) values(2,4,1,1);
insert into `usergroup` (`id`, `login`, `group_name`) values(5,'2','EMPLOYEES_CHILD_VIEW');