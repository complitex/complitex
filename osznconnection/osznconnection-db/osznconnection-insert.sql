-- --------------------------------
-- Current database version
-- --------------------------------
INSERT INTO `update` (`version`) VALUE ('20180125_0.7.7');


-- admin user --
INSERT INTO `user_info` (`object_id`) VALUES (1);
INSERT INTO `first_name`(`id`, `name`) VALUES (1,'admin');
INSERT INTO `middle_name`(`id`, `name`) VALUES (1,'admin');
INSERT INTO `last_name`(`id`, `name`) VALUES (1,'admin');
INSERT INTO `user_info_attribute` (`attribute_id`, `object_id`, `entity_attribute_id`, `value_id`)
  VALUES (1,1,1000,1), (1,1,1001,1), (1,1,1002,1);
INSERT INTO `user` (`id`, `login`, `password`, `user_info_object_id`) VALUES (1, 'admin', '21232f297a57a5a743894a0e4a801fc3', 1);
INSERT INTO `usergroup` (`id`, `login`, `group_name`) VALUES (1, 'admin', 'ADMINISTRATORS');
INSERT INTO `usergroup` (`id`, `login`, `group_name`) VALUES (2, 'admin', 'EMPLOYEES');
INSERT INTO `usergroup` (`id`, `login`, `group_name`) VALUES (3, 'admin', 'EMPLOYEES_CHILD_VIEW');

-- --------------------------------
-- Config
-- --------------------------------

INSERT INTO `config` (`name`,`value`) VALUES ('PAYMENT_FILENAME_PREFIX','A');
INSERT INTO `config` (`name`,`value`) VALUES ('BENEFIT_FILENAME_PREFIX','AF');
INSERT INTO `config` (`name`,`value`) VALUES ('PAYMENT_BENEFIT_FILENAME_SUFFIX','(\\d{8}|\\d{10}){MM}{YY}\\d\\d\\.DBF');
INSERT INTO `config` (`name`,`value`) VALUES ('ACTUAL_PAYMENT_FILENAME_MASK','.*{MM}{YY}\\.DBF');
INSERT INTO `config` (`name`,`value`) VALUES ('SUBSIDY_FILENAME_MASK','.*{MM}{YY}\\.DBF');
INSERT INTO `config` (`name`,`value`) VALUES ('DWELLING_CHARACTERISTICS_INPUT_FILENAME_MASK','\\d{8}\\.a\\d{2}');
INSERT INTO `config` (`name`,`value`) VALUES ('DWELLING_CHARACTERISTICS_OUTPUT_FILE_EXTENSION_PREFIX','c');
INSERT INTO `config` (`name`,`value`) VALUES ('FACILITY_SERVICE_TYPE_INPUT_FILENAME_MASK','\\d{8}\\.b\\d{2}');
INSERT INTO `config` (`name`,`value`) VALUES ('FACILITY_SERVICE_TYPE_OUTPUT_FILE_EXTENSION_PREFIX','d');
INSERT INTO `config` (`name`,`value`) VALUES ('SUBSIDY_TARIF_FILENAME_MASK','TARIF12\\.DBF');
INSERT INTO `config` (`name`,`value`) VALUES ('FACILITY_STREET_TYPE_REFERENCE_FILENAME_MASK','KLKATUL\\.DBF');
INSERT INTO `config` (`name`,`value`) VALUES ('FACILITY_STREET_REFERENCE_FILENAME_MASK','KLUL\\.DBF');
INSERT INTO `config` (`name`,`value`) VALUES ('FACILITY_TARIF_REFERENCE_FILENAME_MASK','TARIF\\.DBF');
INSERT INTO `config` (`name`,`value`) VALUES ('PRIVILEGE_PROLONGATION_S_FILENAME_MASK','(\\d{8}|\\d{10})\\.s\\d{2}');
INSERT INTO `config` (`name`,`value`) VALUES ('PRIVILEGE_PROLONGATION_P_FILENAME_MASK','(\\d{8}|\\d{10})\\.p\\d{2}');
INSERT INTO `config` (`name`,`value`) VALUES ('LOAD_THREAD_SIZE','2');
INSERT INTO `config` (`name`,`value`) VALUES ('BIND_THREAD_SIZE','4');
INSERT INTO `config` (`name`,`value`) VALUES ('FILL_THREAD_SIZE','4');
INSERT INTO `config` (`name`,`value`) VALUES ('SAVE_THREAD_SIZE','4');
INSERT INTO `config` (`name`,`value`) VALUES ('LOAD_BATCH_SIZE','16');
INSERT INTO `config` (`name`,`value`) VALUES ('BIND_BATCH_SIZE','64');
INSERT INTO `config` (`name`,`value`) VALUES ('FILL_BATCH_SIZE','64');
INSERT INTO `config` (`name`,`value`) VALUES ('LOAD_MAX_ERROR_COUNT','10');
INSERT INTO `config` (`name`,`value`) VALUES ('BIND_MAX_ERROR_COUNT','10');
INSERT INTO `config` (`name`,`value`) VALUES ('FILL_MAX_ERROR_COUNT','10');
INSERT INTO `config` (`name`,`value`) VALUES ('SAVE_MAX_ERROR_COUNT','10');
INSERT INTO `config` (`name`,`value`) VALUES ('DEFAULT_REQUEST_FILE_CITY','');

-- --------------------------------
-- Sequence
-- --------------------------------
INSERT INTO `sequence` (`sequence_name`, `sequence_value`) VALUES
('ownership',1), ('ownership_string_value',1),
('privilege',1), ('privilege_string_value',1);
-- --------------------------------
-- Organization type
-- --------------------------------
# INSERT INTO `organization_type`(`object_id`) VALUES (2),(3);
# INSERT INTO `organization_type_string_value`(`id`, `locale_id`, `value`) VALUES (2, 1, UPPER('ОСЗН')), (2, 2,UPPER('ОСЗН')),
# (3, 1, UPPER('Модуль начислений')), (3, 2,UPPER('Центр нарахувань'));
# INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `entity_attribute_id`, `value_id`, `value_type_id`) VALUES (1,2,2300,2,2300),(1,3,2300,3,2300);

INSERT INTO `organization_type`(`object_id`) VALUES (7);
INSERT INTO `organization_type_string_value`(`id`, `locale_id`, `value`)
VALUES (7, 1, UPPER('Отдел субсидий')), (7, 2, UPPER('Отдел субсидий'));
INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `entity_attribute_id`, `value_id`)
  VALUES (1, 7, 2300, 7);

INSERT INTO `organization_type`(`object_id`) VALUES (8);
INSERT INTO `organization_type_string_value`(`id`, `locale_id`, `value`)
VALUES (8, 1, UPPER('Отдел льгот')), (8, 2, UPPER('Отдел льгот'));
INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `entity_attribute_id`, `value_id`)
  VALUES (1, 8, 2300, 8);

-- --------------------------------
-- Organization
-- --------------------------------

-- Reference to `service_association` helper table. It is user organization only attribute. --
# INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (915, 1, UPPER('Ассоцияции тип услуги - модуль начислений')), (915, 2, UPPER('Ассоцияции тип услуги - модуль начислений'));
# INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`) VALUES (914, 900, 1, 915, 1);
# INSERT INTO `entity_attribute_value_type`(`id`, `entity_attribute_id`, `value_type`) VALUES (914, 914, 'service_association');

-- -------------------------------
-- Request files paths attributes
-- -------------------------------

-- Load payments/benefits directory. It is OSZN only attribute. --
INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (915, 1, UPPER('Директория входящих запросов на субсидию')),
  (915, 2, UPPER('Директория входящих запросов на субсидию'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (915, 900, 0, 915, 1, 1);

-- Save payments/benefits directory. It is OSZN only attribute. --
INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (916, 1, UPPER('Директория исходящих ответов на запросы на субсидию')),
  (916, 2, UPPER('Директория исходящих ответов на запросы на субсидию'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (916, 900, 0, 916, 1, 1);

-- Load actual payments directory. It is OSZN only attribute. --
INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (917, 1, UPPER('Директория входящих запросов фактического начисления')),
  (917, 2, UPPER('Директория входящих запросов фактического начисления'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (917, 900, 0, 917, 1, 1);

-- Save actual payments directory. It is OSZN only attribute. --
INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (918, 1, UPPER('Директория исходящих ответов на запросы фактического начисления')),
  (918, 2, UPPER('Директория исходящих ответов на запросы фактического начисления'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (918, 900, 0, 918, 1, 1);

-- Load subsidies directory. It is OSZN only attribute. --
INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (919, 1, UPPER('Директория входящих файлов субсидий')),
  (919, 2, UPPER('Директория входящих файлов субсидий'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (919, 900, 0, 919, 1, 1);

-- Save subsidies directory. It is OSZN only attribute. --
INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (920, 1, UPPER('Директория исходящих файлов субсидий')),
  (920, 2, UPPER('Директория исходящих файлов субсидий'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (920, 900, 0, 920, 1, 1);

-- Load dwelling characteristics directory. It is OSZN only attribute. --
INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (921, 1, UPPER('Директория входящих файлов характеристик жилья')),
  (921, 2, UPPER('Директория входящих файлов характеристик жилья'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (921, 900, 0, 921, 1, 1);

-- Save dwelling characteristics directory. It is OSZN only attribute. --
INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (922, 1, UPPER('Директория исходящих файлов характеристик жилья')),
  (922, 2, UPPER('Директория исходящих файлов характеристик жилья'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (922, 900, 0, 922, 1, 1);

-- Load facility service type directory. It is OSZN only attribute. --
INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (923, 1, UPPER('Директория входящих файлов-запросов видов услуг')),
  (923, 2, UPPER('Директория входящих файлов-запросов видов услуг'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (923, 900, 0, 923, 1, 1);

-- Save facility service type directory. It is OSZN only attribute. --
INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (924, 1, UPPER('Директория исходящих файлов-запросов видов услуг')),
  (924, 2, UPPER('Директория исходящих файлов-запросов видов услуг'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (924, 900, 0, 924, 1, 1);

-- References directory. It is OSZN only attribute. --
INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (925, 1, UPPER('Директория справочников')),
  (925, 2, UPPER('Директория справочников'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (925, 900, 0, 925, 1, 1);

-- EDRPOU(ЕДРПОУ) attribute. It is user organization only attribute. --
INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (926, 1, UPPER('ЕДРПОУ')), (926, 2, UPPER('ЕДРПОУ'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (926, 900, 1, 926, 1, 1);

-- Root directory for loading and saving request files. It is user organization only attribute. --
INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (927, 1, UPPER('Корневой каталог для файлов запросов')),
  (927, 2, UPPER('Корневой каталог для файлов запросов'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (927, 900, 1, 927, 1, 1);

-- Save facility form2 directory --
INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (928, 1, UPPER('Директория исходящих файлов форма-2 льгота')),
  (928, 2, UPPER('Директория исходящих файлов форма-2 льгота'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (928, 900, 0, 928, 1, 1);

-- Save facility local directory
INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (929, 1, UPPER('Директория исходящих файлов местной льготы')),
  (929, 2, UPPER('Директория исходящих файлов местной льготы'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (929, 900, 0, 929, 1, 1);

-- Export subsidy directory--
INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (930, 1, UPPER('Корневой каталог для экспорта файлов'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (930, 900, 0, 930, 1, 1);

-- Load privilege prolongation directory --
INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (931, 1, UPPER('Директория входящих файлов продления льгот')),
  (931, 2, UPPER('Директория входящих файлов продления льгот'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (931, 900, 0, 931, 1, 1);

-- --------------------------------
-- Ownership
-- --------------------------------
INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (1100, 1, 'Форма собственности'), (1100, 2, 'Форма власності');
INSERT INTO `entity`(`id`, `entity`, `name_id`, `strategy_factory`) VALUES (1100, 'ownership', 1100, '');

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (1101, 1, UPPER('Название')), (1101, 2, UPPER('Назва'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (1100, 1100, 1, 1101, 1, 0);

-- --------------------------------
-- Forms of ownerships
-- --------------------------------
INSERT INTO `ownership`(`object_id`) VALUES (1),(2),(3),(4),(5),(6);
INSERT INTO `ownership_string_value`(`id`, `locale_id`, `value`) VALUES (1, 1, UPPER('мiсцевих Рад')), (1, 2,UPPER('мiсцевих Рад')),
(2, 1, UPPER('кооперативна')), (2, 2, UPPER('кооперативна')), (3, 1, UPPER('вiдомча')), (3, 2, UPPER('вiдомча')),
(4, 1, UPPER('громадська')), (4, 2, UPPER('громадська')), (5, 1, UPPER('приватна')), (5, 2, UPPER('приватна')),
(6, 1, UPPER('приватизована')), (6, 2, UPPER('приватизована'));
INSERT INTO `ownership_attribute`(`attribute_id`, `object_id`, `entity_attribute_id`, `value_id`) 
  VALUES (1,1,1100,1),(1,2,1100,2),(1,3,1100,3),(1,4,1100,4),(1,5,1100,5),(1,6,1100,6);

-- --------------------------------
-- Privilege
-- --------------------------------

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (1200, 1, 'Льгота'), (1200, 2, 'Привілей');
INSERT INTO `entity`(`id`, `entity`, `name_id`, `strategy_factory`) VALUES (1200, 'privilege', 1200, '');

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (1201, 1, UPPER('Название')), (1201, 2, UPPER('Назва'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (1200, 1200, 1, 1201, 1, 0);

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (1202, 1, UPPER('Код')), (1202, 2, UPPER('Код'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (1201, 1200, 1, 1202, 1, 1);

-- Privileges
INSERT INTO `privilege`(`object_id`) VALUES
(1),(2),(3),(4),(5),(6),(7),(8),(9),(10),(11),(12),(13),(14),(15),(16),(17),(18),(19),(20),(21),(22),(23),(24),(25),(26),(27),(28),(29),(30),
(31),(32),(33),(34),(35),(36),(37),(38),(39),(40),(41),(42),(43),(44),(45),(46),(47),(48),(49),(50),(51),(52),(53),(54),(55),(56),(57),(58),(59),(60),
(61),(62),(63),(64),(65),(66),(67),(68),(69),(70),(71),(72),(73),(74),(75),(76),(77),(78),(79),(80),(81),(82),(83),(84),(85),(86),(87),(88),(89),(90),
(91),(92),(93),(94),(95),(96),(97),(98),(99),(100),(101),(102),(103),(104);

INSERT INTO `privilege_string_value`(`id`, `locale_id`, `value`) VALUES
(1,1,UPPER('УЧАСТНИК БОЕВЫХ ДЕЙСТВИЙ')), (1,2,UPPER('УЧАСТНИК БОЕВЫХ ДЕЙСТВИЙ')), (2,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('1')),
(3,1,UPPER('УЧАСТНИК ВОЙНЫ')), (3,2,UPPER('УЧАСТНИК ВОЙНЫ')), (4,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('2')),
(5,1,UPPER('ЧЛЕН СЕМЬИ ПОГИБШЕГО/УМЕРШЕГО ВЕТЕРАНА ВОЙНЫ')), (5,2,UPPER('ЧЛЕН СЕМЬИ ПОГИБШЕГО/УМЕРШЕГО ВЕТЕРАНА ВОЙНЫ')), (6,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('3')),
(7,1,UPPER('ИНВАЛИД ВОЙНЫ ПЕРВОЙ ГРУППЫ')), (7,2,UPPER('ИНВАЛИД ВОЙНЫ ПЕРВОЙ ГРУППЫ')), (8,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('11')),
(9,1,UPPER('ИНВАЛИД ВОЙНЫ ВТОРОЙ ГРУППЫ')), (9,2,UPPER('ИНВАЛИД ВОЙНЫ ВТОРОЙ ГРУППЫ')), (10,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('12')),
(11,1,UPPER('ИНВАЛИД ВОЙНЫ ТРЕТЬЕЙ ГРУППЫ')), (11,2,UPPER('ИНВАЛИД ВОЙНЫ ТРЕТЬЕЙ ГРУППЫ')), (12,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('13')),
(13,1,UPPER('РЕБЕНОК ВОЙНЫ')), (13,2,UPPER('РЕБЕНОК ВОЙНЫ')), (14,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('15')),
(15,1,UPPER('ЛИЦО С ОСОБЫМИ ЗАСЛУГАМИ')), (15,2,UPPER('ЛИЦО С ОСОБЫМИ ЗАСЛУГАМИ')), (16,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('20')),
(17,1,UPPER('РОДИТЕЛИ УМЕРШЕГО ЛИЦА С ОСОБЫМИ ЗАСЛУГАМИ')), (17,2,UPPER('РОДИТЕЛИ УМЕРШЕГО ЛИЦА С ОСОБЫМИ ЗАСЛУГАМИ')), (18,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('22')),
(19,1,UPPER('ВДОВА/ВДОВЕЦ ЛИЦА С ОСОБЫМИ ЗАСЛУГАМИ')), (19,2,UPPER('ВДОВА/ВДОВЕЦ ЛИЦА С ОСОБЫМИ ЗАСЛУГАМИ')), (20,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('23')),
(21,1,UPPER('ВДОВА/ВДОВЕЦ ЛИЦА С ОСОБЫМИ ТРУДОВЫМИ ЗАСЛУГАМИ')), (21,2,UPPER('ВДОВА/ВДОВЕЦ ЛИЦА С ОСОБЫМИ ТРУДОВЫМИ ЗАСЛУГАМИ')), (22,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('26')),
(23,1,UPPER('ЛИЦО С ОСОБЫМИ ТРУДОВЫМИ ЗАСЛУГАМИ')), (23,2,UPPER('ЛИЦО С ОСОБЫМИ ТРУДОВЫМИ ЗАСЛУГАМИ')), (24,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('30')),
(25,1,UPPER('РОДИТЕЛИ УМЕРШЕГО ЛИЦА С ОСОБЫМИ ТРУДОВЫМИ ЗАСЛУГАМИ')), (25,2,UPPER('РОДИТЕЛИ УМЕРШЕГО ЛИЦА С ОСОБЫМИ ТРУДОВЫМИ ЗАСЛУГАМИ')), (26,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('32')),
(27,1,UPPER('ВЕТЕРАН ТРУДА')), (27,2,UPPER('ВЕТЕРАН ТРУДА')), (28,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('33')),
(29,1,UPPER('ПЕНСИОНЕР ПО ВОЗРАСТУ')), (29,2,UPPER('ПЕНСИОНЕР ПО ВОЗРАСТУ')), (30,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('34')),
(31,1,UPPER('МНОГОДЕТНЫЕ СЕМЬИ')), (31,2,UPPER('МНОГОДЕТНЫЕ СЕМЬИ')), (32,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('35')),
(33,1,UPPER('ЧЛЕН  МНОГОДЕТНОЙ СЕМЬИ')), (33,2,UPPER('ЧЛЕН  МНОГОДЕТНОЙ СЕМЬИ')), (34,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('36')),
(35,1,UPPER('ВЕТЕРАН СЛУЖБЫ ГРАЖДАНСКОЙ ЗИЩИТЫ')), (35,2,UPPER('ВЕТЕРАН СЛУЖБЫ ГРАЖДАНСКОЙ ЗИЩИТЫ')), (36,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('37')),
(37,1,UPPER('ВЕТЕРАН ГОСУДАРСТВЕННОЙ КРИМ.-ИСПОЛНИТЕЛЬНОЙ СЛУЖБЫ')), (37,2,UPPER('ВЕТЕРАН ГОСУДАРСТВЕННОЙ КРИМ.-ИСПОЛНИТЕЛЬНОЙ СЛУЖБЫ')), (38,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('39')),
(39,1,UPPER('СЕЛЬСКИЙ ПЕДАГОГ НА ПЕНСИИ')), (39,2,UPPER('СЕЛЬСКИЙ ПЕДАГОГ НА ПЕНСИИ')), (40,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('40')),
(41,1,UPPER('СЕЛЬСКИЙ БИБЛИОТЕКАРЬ НА ПЕНСИИ')), (41,2,UPPER('СЕЛЬСКИЙ БИБЛИОТЕКАРЬ НА ПЕНСИИ')), (42,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('41')),
(43,1,UPPER('СЕЛЬСКИЙ СПЕЦИАЛИСТ ПО ЗАЩИТЕ РАСТЕНИЙ НА ПЕНСИИ')), (43,2,UPPER('СЕЛЬСКИЙ СПЕЦИАЛИСТ ПО ЗАЩИТЕ РАСТЕНИЙ НА ПЕНСИИ')), (44,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('42')),
(45,1,UPPER('СЕЛЬСКИЙ МЕДИК НА ПЕНСИИ')), (45,2,UPPER('СЕЛЬСКИЙ МЕДИК НА ПЕНСИИ')), (46,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('43')),
(47,1,UPPER('СУДЬЯ В ОТСТАВКЕ')), (47,2,UPPER('СУДЬЯ В ОТСТАВКЕ')), (48,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('47')),
(49,1,UPPER('СЛЕДОВАТЕЛЬ ПРОКУРАТУРЫ НА ПЕНСИИ')), (49,2,UPPER('СЛЕДОВАТЕЛЬ ПРОКУРАТУРЫ НА ПЕНСИИ')), (50,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('49')),
(51,1,UPPER('НАЛОГОВЫЙ МИЛИЦИОНЕР НА ПЕНСИИ')), (51,2,UPPER('НАЛОГОВЫЙ МИЛИЦИОНЕР НА ПЕНСИИ')), (52,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('50')),
(53,1,UPPER('НЕТРУДОСПОСОБНЫЙ ЧЛЕН СЕМЬИ ПОГИБШЕГО НАЛОГОВОГО МИЛИЦИОНЕРА')), (53,2,UPPER('НЕТРУДОСПОСОБНЫЙ ЧЛЕН СЕМЬИ ПОГИБШЕГО НАЛОГОВОГО МИЛИЦИОНЕРА')), (54,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('51')),
(55,1,UPPER('СЕЛЬСКИЙ НАЛОГОВЫЙ МИЛИЦИОНЕР НА ПЕНСИИ')), (55,2,UPPER('СЕЛЬСКИЙ НАЛОГОВЫЙ МИЛИЦИОНЕР НА ПЕНСИИ')), (56,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('52')),
(57,1,UPPER('НЕТРУДОСПОСОБНЫЙ ЧЛЕН СЕМЬИ СЕЛЬСКОГО НАЛОГОВОГО МИЛИЦИОНЕРА')), (57,2,UPPER('НЕТРУДОСПОСОБНЫЙ ЧЛЕН СЕМЬИ СЕЛЬСКОГО НАЛОГОВОГО МИЛИЦИОНЕРА')), (58,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('53')),
(59,1,UPPER('ВОЕННОСЛУЖАЩИЙ СБУ НА ПЕНСИИ')), (59,2,UPPER('ВОЕННОСЛУЖАЩИЙ СБУ НА ПЕНСИИ')), (60,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('58')),
(61,1,UPPER('ЛИЦО (ЧАЭС) - 1 КАТЕГОРИЯ')), (61,2,UPPER('ЛИЦО (ЧАЭС) - 1 КАТЕГОРИЯ')), (62,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('61')),
(63,1,UPPER('ЛИЦО (ЧАЭС) - 2 КАТЕГОРИЯ - ЛИКВИДАТОР')), (63,2,UPPER('ЛИЦО (ЧАЭС) - 2 КАТЕГОРИЯ - ЛИКВИДАТОР')), (64,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('62')),
(65,1,UPPER('ЛИЦО (ЧАЭС) - 2 КАТЕГОРИЯ - ПОТЕРПЕВШИЙ')), (65,2,UPPER('ЛИЦО (ЧАЭС) - 2 КАТЕГОРИЯ - ПОТЕРПЕВШИЙ')), (66,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('63')),
(67,1,UPPER('ЛИЦО (ЧАЭС) - 3 КАТЕГОРИЯ')), (67,2,UPPER('ЛИЦО (ЧАЭС) - 3 КАТЕГОРИЯ')), (68,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('64')),
(69,1,UPPER('ЛИЦО (ЧАЭС) - 4 КАТЕГОРИЯ')), (69,2,UPPER('ЛИЦО (ЧАЭС) - 4 КАТЕГОРИЯ')), (70,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('65')),
(71,1,UPPER('ЖЕНА/МУЖ (ЧАЭС) УМЕРШЕГО ГРАЖДАНИНА')), (71,2,UPPER('ЖЕНА/МУЖ (ЧАЭС) УМЕРШЕГО ГРАЖДАНИНА')), (72,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('66')),
(73,1,UPPER('РЕБЕНОК (ЧАЭС) УМЕРШЕГО ГРАЖДАНИНА')), (73,2,UPPER('РЕБЕНОК (ЧАЭС) УМЕРШЕГО ГРАЖДАНИНА')), (74,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('67')),
(75,1,UPPER('РЕБЕНОК (ЧАЭС) ПОТЕРПЕВШЕГО')), (75,2,UPPER('РЕБЕНОК (ЧАЭС) ПОТЕРПЕВШЕГО')), (76,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('68')),
(77,1,UPPER('РЕБЕНОК (ЧАЭС) - ИНВАЛИД')), (77,2,UPPER('РЕБЕНОК (ЧАЭС) - ИНВАЛИД')), (78,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('69')),
(79,1,UPPER('ЛИЦО (ЧАЭС), РАБОТАВШЕЕ ЗА ПРЕДЕЛАМИ ЗОНЫ ОТЧУЖДЕНИЯ (ЛИКВИДАЦИЯ ПОСЛЕДСТВИЙ АВАРИИ)')), (79,2,UPPER('ЛИЦО (ЧАЭС), РАБОТАВШЕЕ ЗА ПРЕДЕЛАМИ ЗОНЫ ОТЧУЖДЕНИЯ (ЛИКВИДАЦИЯ ПОСЛЕДСТВИЙ АВАРИИ)')), (80,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('70')),
(81,1,UPPER('СОТРУДНИК УГОЛОВНО-ИСПОЛНИТЕЛЬНОЙ СИСТЕМЫ НА ПЕНСИИ')), (81,2,UPPER('СОТРУДНИК УГОЛОВНО-ИСПОЛНИТЕЛЬНОЙ СИСТЕМЫ НА ПЕНСИИ')), (82,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('71')),
(83,1,UPPER('НЕТРУДОСПОСОБНЫЙ ЧЛЕН СЕМЬИ ПОГИБШЕГО СОТРУДНИКА УГОЛОВНО-ИСПОЛНИТЕЛЬНОЙ СИСТЕМЫ')), (83,2,UPPER('НЕТРУДОСПОСОБНЫЙ ЧЛЕН СЕМЬИ ПОГИБШЕГО СОТРУДНИКА УГОЛОВНО-ИСПОЛНИТЕЛЬНОЙ СИСТЕМЫ')), (84,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('72')),
(85,1,UPPER('СЕЛЬСКИЙ СОТРУДНИК УГОЛОВНО-ИСПОЛНИТЕЛЬНОЙ СИСТЕМЫ НА ПЕНСИИ')), (85,2,UPPER('СЕЛЬСКИЙ СОТРУДНИК УГОЛОВНО-ИСПОЛНИТЕЛЬНОЙ СИСТЕМЫ НА ПЕНСИИ')), (86,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('73')),
(87,1,UPPER('НЕТРУДОСПОСОБНЫЙ ЧЛЕН СЕМЬИ ПОГИБШЕГО СЕЛЬСКОГО СОТРУДНИКА УГОЛОВНО-ИСПОЛНИТЕЛЬНОЙ СИСТЕМЫ')), (87,2,UPPER('НЕТРУДОСПОСОБНЫЙ ЧЛЕН СЕМЬИ ПОГИБШЕГО СЕЛЬСКОГО СОТРУДНИКА УГОЛОВНО-ИСПОЛНИТЕЛЬНОЙ СИСТЕМЫ')), (88,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('74')),
(89,1,UPPER('МИЛИЦИОНЕР НА ПЕНСИИ')), (89,2,UPPER('МИЛИЦИОНЕР НА ПЕНСИИ')), (90,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('75')),
(91,1,UPPER('НЕТРУДОСПОСОБНЫЙ ЧЛЕН СЕМЬИ ПОГИБШЕГО МИЛИЦИОНЕРА')), (91,2,UPPER('НЕТРУДОСПОСОБНЫЙ ЧЛЕН СЕМЬИ ПОГИБШЕГО МИЛИЦИОНЕРА')), (92,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('76')),
(93,1,UPPER('СЕЛЬСКИЙ МИЛИЦИОНЕР НА ПЕНСИИ')), (93,2,UPPER('СЕЛЬСКИЙ МИЛИЦИОНЕР НА ПЕНСИИ')), (94,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('77')),
(95,1,UPPER('НЕТРУДОСПОСОБНЫЙ ЧЛЕН СЕМЬИ ПОГИБШЕГО СЕЛЬСКОГО МИЛИЦИОНЕРА')), (95,2,UPPER('НЕТРУДОСПОСОБНЫЙ ЧЛЕН СЕМЬИ ПОГИБШЕГО СЕЛЬСКОГО МИЛИЦИОНЕРА')), (96,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('78')),
(97,1,UPPER('ВЕТЕРАН ВОИНСКОЙ СЛУЖБЫ')), (97,2,UPPER('ВЕТЕРАН ВОИНСКОЙ СЛУЖБЫ')), (98,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('80')),
(99,1,UPPER('ВДОВА/ВДОВЕЦ ВЕТЕРАНА ВОИНСКОЙ СЛУЖБЫ')), (99,2,UPPER('ВДОВА/ВДОВЕЦ ВЕТЕРАНА ВОИНСКОЙ СЛУЖБЫ')), (100,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('81')),
(101,1,UPPER('ЧЛЕН СЕМЬИ ВОЕННОСЛУЖАЩЕГО, ПОГИБШЕГО, УМЕРШЕГО, ПРОПАВШЕГО БЕЗ ВЕСТИ ИЛИ СТАВШЕГО ИНВАЛИДОМ')), (101,2,UPPER('ЧЛЕН СЕМЬИ ВОЕННОСЛУЖАЩЕГО, ПОГИБШЕГО, УМЕРШЕГО, ПРОПАВШЕГО БЕЗ ВЕСТИ ИЛИ СТАВШЕГО ИНВАЛИДОМ')), (102,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('83')),
(103,1,UPPER('РОДИТЕЛИ ВОЕННОСЛУЖАЩЕГО, СТАВШЕГО ИНВАЛИДОМ')), (103,2,UPPER('РОДИТЕЛИ ВОЕННОСЛУЖАЩЕГО, СТАВШЕГО ИНВАЛИДОМ')), (104,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('84')),
(105,1,UPPER('ВДОВА/ВДОВЕЦ ВОЕННОСЛУЖАЩЕГО И ЕГО ДЕТИ')), (105,2,UPPER('ВДОВА/ВДОВЕЦ ВОЕННОСЛУЖАЩЕГО И ЕГО ДЕТИ')), (106,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('85')),
(107,1,UPPER('ЖЕНА/МУЖ ВОЕННОСЛУЖАЩЕГО, ПРОПАВШЕГО БЕЗ ВЕСТИ')), (107,2,UPPER('ЖЕНА/МУЖ ВОЕННОСЛУЖАЩЕГО, ПРОПАВШЕГО БЕЗ ВЕСТИ')), (108,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('86')),
(109,1,UPPER('РОДИТЕЛИ ПОГИБШЕГО ВОЕННОСЛУЖАЩЕГО')), (109,2,UPPER('РОДИТЕЛИ ПОГИБШЕГО ВОЕННОСЛУЖАЩЕГО')), (110,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('87')),
(111,1,UPPER('ИНВАЛИД ВОИНСКОЙ СЛУЖБЫ')), (111,2,UPPER('ИНВАЛИД ВОИНСКОЙ СЛУЖБЫ')), (112,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('88')),
(113,1,UPPER('ВЕТЕРАН ОРГАНОВ ВНУТРЕННИХ ДЕЛ')), (113,2,UPPER('ВЕТЕРАН ОРГАНОВ ВНУТРЕННИХ ДЕЛ')), (114,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('90')),
(115,1,UPPER('ВДОВА/ВДОВЕЦ, ВЕТЕРАНА ОРГАНОВ ВНУТРЕННИХ ДЕЛ')), (115,2,UPPER('ВДОВА/ВДОВЕЦ, ВЕТЕРАНА ОРГАНОВ ВНУТРЕННИХ ДЕЛ')), (116,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('91')),
(117,1,UPPER('ПОЖАРНЫЙ НА ПЕНСИИ')), (117,2,UPPER('ПОЖАРНЫЙ НА ПЕНСИИ')), (118,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('95')),
(119,1,UPPER('НЕТРУДОСПОСОБНЫЙ ЧЛЕН СЕМЬИ ПОГИБШЕГО ПОЖАРНОГО')), (119,2,UPPER('НЕТРУДОСПОСОБНЫЙ ЧЛЕН СЕМЬИ ПОГИБШЕГО ПОЖАРНОГО')), (120,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('96')),
(121,1,UPPER('ВЕТЕРАН ГОСУДАРСТВЕННОЙ ПОЖАРНОЙ ОХРАНЫ')), (121,2,UPPER('ВЕТЕРАН ГОСУДАРСТВЕННОЙ ПОЖАРНОЙ ОХРАНЫ')),(122,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('98')),
(123,1,UPPER('ВДОВА/ВДОВЕЦ ВЕТЕРАНА ГОСУДАРСТВЕННОЙ ПОЖАРНОЙ ОХРАНЫ')), (123,2,UPPER('ВДОВА/ВДОВЕЦ ВЕТЕРАНА ГОСУДАРСТВЕННОЙ ПОЖАРНОЙ ОХРАНЫ')), (124,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('99')),
(125,1,UPPER('РЕАБИЛИТИРОВАННЫЕ, СТАВШИЕ ИНВАЛИДАМИ ВСЛЕДСТВИИ РЕПРЕССИЙ, ЛИБО ЯВЛЯЮЩИЕСЯ ПЕНСИОНЕРАМИ, ИМЕЮЩИМИ П')), (125,2,UPPER('РЕАБИЛИТИРОВАННЫЕ, СТАВШИЕ ИНВАЛИДАМИ ВСЛЕДСТВИИ РЕПРЕССИЙ, ЛИБО ЯВЛЯЮЩИЕСЯ ПЕНСИОНЕРАМИ, ИМЕЮЩИМИ П')), (126,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('100')),
(127,1,UPPER('РЕБЕНОК-ИНВАЛИД')), (127,2,UPPER('РЕБЕНОК-ИНВАЛИД')), (128,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('110')),
(129,1,UPPER('ИНВАЛИД 1 ГРУППЫ ПО ЗРЕНИЮ ИЛИ С ВРАЖДЕННЫМ ОРА')), (129,2,UPPER('ИНВАЛИД 1 ГРУППЫ ПО ЗРЕНИЮ ИЛИ С ВРАЖДЕННЫМ ОРА')), (130,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('111')),
(131,1,UPPER('ИНВАЛИД 2 ГРУППЫ ПО ЗРЕНИЮ ИЛИ С ВРАЖДЕННЫМ ОРА')), (131,2,UPPER('ИНВАЛИД 2 ГРУППЫ ПО ЗРЕНИЮ ИЛИ С ВРАЖДЕННЫМ ОРА')), (132,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('112')),
(133,1,UPPER('ИНВАЛИД 1 ГРУППЫ, КРОМЕ ИНВАЛИДОВ ПО ЗРЕНИЮ ИЛИ С ВРОЖДЕННЫМ ОРА')), (133,2,UPPER('ИНВАЛИД 1 ГРУППЫ, КРОМЕ ИНВАЛИДОВ ПО ЗРЕНИЮ ИЛИ С ВРОЖДЕННЫМ ОРА')), (134,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('113')),
(135,1,UPPER('ИНВАЛИД 2 ГРУППЫ, КРОМЕ ИНВАЛИДОВ ПО ЗРЕНИЮ ИЛИ С ВРОЖДЕННЫМ ОРА')), (135,2,UPPER('ИНВАЛИД 2 ГРУППЫ, КРОМЕ ИНВАЛИДОВ ПО ЗРЕНИЮ ИЛИ С ВРОЖДЕННЫМ ОРА')), (136,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('114')),
(137,1,UPPER('ИНВАЛИД 3 ГРУППЫ')), (137,2,UPPER('ИНВАЛИД 3 ГРУППЫ')), (138,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('115')),
(139,1,UPPER('ЖЕРТВА НАЦИСТСКИХ ПРЕСЛЕДОВАНИЙ, СТ. 6(1)')), (139,2,UPPER('ЖЕРТВА НАЦИСТСКИХ ПРЕСЛЕДОВАНИЙ, СТ. 6(1)')), (140,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('120')),
(141,1,UPPER('ЖЕРТВА НАЦИСТСКИХ ПРЕСЛЕДОВАНИЙ, СТ. 6(2) - ИНВАЛИД 1 ГРУППЫ')), (141,2,UPPER('ЖЕРТВА НАЦИСТСКИХ ПРЕСЛЕДОВАНИЙ, СТ. 6(2) - ИНВАЛИД 1 ГРУППЫ')), (142,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('121')),
(143,1,UPPER('ЖЕРТВА НАЦИСТСКИХ ПРЕСЛЕДОВАНИЙ, СТ. 6(2) - ИНВАЛИД 2 ГРУППЫ')), (143,2,UPPER('ЖЕРТВА НАЦИСТСКИХ ПРЕСЛЕДОВАНИЙ, СТ. 6(2) - ИНВАЛИД 2 ГРУППЫ')), (144,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('122')),
(145,1,UPPER('ЖЕРТВА НАЦИСТСКИХ ПРЕСЛЕДОВАНИЙ, СТ. 6(2) - ИНВАЛИД 3 ГРУППЫ')), (145,2,UPPER('ЖЕРТВА НАЦИСТСКИХ ПРЕСЛЕДОВАНИЙ, СТ. 6(2) - ИНВАЛИД 3 ГРУППЫ')), (146,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('123')),
(147,1,UPPER('ЖЕРТВА НАЦИСТСКИХ ПРЕСЛЕДОВАНИЙ, СТ. 6(3)')), (147,2,UPPER('ЖЕРТВА НАЦИСТСКИХ ПРЕСЛЕДОВАНИЙ, СТ. 6(3)')), (148,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('124')),
(149,1,UPPER('ЖЕРТВА НАЦИСТСКИХ ПРЕСЛЕДОВАНИЙ, СТ. 6(4)')), (149,2,UPPER('ЖЕРТВА НАЦИСТСКИХ ПРЕСЛЕДОВАНИЙ, СТ. 6(4)')), (150,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('125')),
(151,1,UPPER('ГОРНЯКИ - НЕТРУДОСПОСОБНЫЕ РАБОТНИКИ')), (151,2,UPPER('ГОРНЯКИ - НЕТРУДОСПОСОБНЫЕ РАБОТНИКИ')), (152,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('126')),
(153,1,UPPER('ГОРНЯКИ - НЕРАБОТАЮЩИЕ ПЕНСИОНЕРЫ')), (153,2,UPPER('ГОРНЯКИ - НЕРАБОТАЮЩИЕ ПЕНСИОНЕРЫ')), (154,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('127')),
(155,1,UPPER('ГОРНЯКИ - ИНВАЛИДЫ')), (155,2,UPPER('ГОРНЯКИ - ИНВАЛИДЫ')), (156,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('128')),
(157,1,UPPER('ГОРНЯКИ - СЕМЬИ ПОГИБШИХ ТРУЖЕНИКОВ')), (157,2,UPPER('ГОРНЯКИ - СЕМЬИ ПОГИБШИХ ТРУЖЕНИКОВ')), (158,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('129')),
(159,1,UPPER('ВЕТЕРАН ГОСУДАРСТВЕННОЙ СЛУЖБЫ СПЕЦ.СВЯЗИ')), (159,2,UPPER('ВЕТЕРАН ГОСУДАРСТВЕННОЙ СЛУЖБЫ СПЕЦ.СВЯЗИ')), (160,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('130')),
(161,1,UPPER('ВДОВА/ВДОВЕЦ ВЕТЕРАНА ГОСУДАРСТВЕННОЙ СЛУЖБЫ СПЕЦ.СВЯЗИ')), (161,2,UPPER('ВДОВА/ВДОВЕЦ ВЕТЕРАНА ГОСУДАРСТВЕННОЙ СЛУЖБЫ СПЕЦ.СВЯЗИ')), (162,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('131')),
(163,1,UPPER('РОДИТЕЛИ И ЧЛЕНЫ СЕМЬИ ПОГИБШЕГО/УМЕРШЕГО СОТРУДНИКА ГОСУДАРСТВЕННОЙ СЛУЖБЫ СПЕЦ.СВЯЗИ')), (163,2,UPPER('РОДИТЕЛИ И ЧЛЕНЫ СЕМЬИ ПОГИБШЕГО/УМЕРШЕГО СОТРУДНИКА ГОСУДАРСТВЕННОЙ СЛУЖБЫ СПЕЦ.СВЯЗИ')), (164,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('132')),
(165,1,UPPER('РОДИТЕЛИ И ЧЛЕНЫ СЕМЬИ СОТРУДНИКА ГРАЖДАНСКОЙ ОБОРОНЫ, ПОГИБШЕГО, УМЕРШЕГО, ПРОПАВШЕГО БЕЗ ВЕСТИ ИЛИ')), (165,2,UPPER('РОДИТЕЛИ И ЧЛЕНЫ СЕМЬИ СОТРУДНИКА ГРАЖДАНСКОЙ ОБОРОНЫ, ПОГИБШЕГО, УМЕРШЕГО, ПРОПАВШЕГО БЕЗ ВЕСТИ ИЛИ')), (166,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('135')),
(167,1,UPPER('МАТЕРИ-ГЕРОИНИ')), (167,2,UPPER('МАТЕРИ-ГЕРОИНИ')), (168,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('200')),
(169,1,UPPER('ДЕТИ-ИНВАЛИДЫ, ПРИКОВАННЫЕ К КРОВАТИ')), (169,2,UPPER('ДЕТИ-ИНВАЛИДЫ, ПРИКОВАННЫЕ К КРОВАТИ')), (170,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('201')),
(171,1,UPPER('ДЕТИ-ИНВАЛИДЫ ДО 18 ЛЕТ, ГДЕ ОБА РОДИТЕЛИ ИНВАЛИДЫ')), (171,2,UPPER('ДЕТИ-ИНВАЛИДЫ ДО 18 ЛЕТ, ГДЕ ОБА РОДИТЕЛИ ИНВАЛИДЫ')), (172,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('202')),
(173,1,UPPER('МНОГОДЕТНЫЕ СЕМЬИ (3 И БОЛЕЕ ДЕТЕЙ ДО 18 ЛЕТ)')), (173,2,UPPER('МНОГОДЕТНЫЕ СЕМЬИ (3 И БОЛЕЕ ДЕТЕЙ ДО 18 ЛЕТ)')), (174,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('203')),
(175,1,UPPER('МАТЕРИ-ОДИНОЧКИ (2 И БОЛЕЕ ДЕТЕЙ ДО 18 ЛЕТ)')), (175,2,UPPER('МАТЕРИ-ОДИНОЧКИ (2 И БОЛЕЕ ДЕТЕЙ ДО 18 ЛЕТ)')), (176,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('204')),
(177,1,UPPER('ДЕТИ-ИНВАЛИДЫ С ОНКОЛОГИЧЕСКИМИ ЗАБОЛЕВАНИЯМИ ДО 18 ЛЕТ')), (177,2,UPPER('ДЕТИ-ИНВАЛИДЫ С ОНКОЛОГИЧЕСКИМИ ЗАБОЛЕВАНИЯМИ ДО 18 ЛЕТ')), (178,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('205')),
(179,1,UPPER('ИНВАЛИДЫ 1 ГРУППЫ, ПОЛУЧАЮЩИЕ СОЦИАЛЬНУЮ ПЕНСИЮ ИЛИ ГОСУДАРСТВЕННУЮ ПОМОЩЬ')), (179,2,UPPER('ИНВАЛИДЫ 1 ГРУППЫ, ПОЛУЧАЮЩИЕ СОЦИАЛЬНУЮ ПЕНСИЮ ИЛИ ГОСУДАРСТВЕННУЮ ПОМОЩЬ')), (180,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('206')),
(181,1,UPPER('НЕРАБОТАЮЩИЕ РОДИТЕЛИ, ОСУЩЕСТВЛЯЮЩИЕ УХОД ЗА РЕБЕНКОМ-ИНВАЛИДОМ ДО 18 ЛЕТ')), (181,2,UPPER('НЕРАБОТАЮЩИЕ РОДИТЕЛИ, ОСУЩЕСТВЛЯЮЩИЕ УХОД ЗА РЕБЕНКОМ-ИНВАЛИДОМ ДО 18 ЛЕТ')), (182,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('207')),
(183,1,UPPER('ИНВАЛИДЫ 1, 2 ГРУППЫ ПО ЗРЕНИЮ')), (183,2,UPPER('ИНВАЛИДЫ 1, 2 ГРУППЫ ПО ЗРЕНИЮ')), (184,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('208')),
(185,1,UPPER('СЕМЬИ ДЕТЕЙ ДО 18 ЛЕТ, БОЛЬНЫХ ДЦП')), (185,2,UPPER('СЕМЬИ ДЕТЕЙ ДО 18 ЛЕТ, БОЛЬНЫХ ДЦП')), (186,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('209')),
(187,1,UPPER('ГРАЖДАНЕ, РЕАБИЛИТИРОВАННЫЕ СОГЛАСНО')), (187,2,UPPER('ГРАЖДАНЕ, РЕАБИЛИТИРОВАННЫЕ СОГЛАСНО')), (188,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('210')),
(189,1,UPPER('СЕМЬИ ПОГИБШИХ (РЯДОВОЙ СОСТАВ) ПРИ ПРОХОЖДЕНИИ СРОЧНОЙ ВОИНСКОЙ СЛУЖБЫ, ИСПОЛНЯВШИХ СВОЙ ДОЛГ В МИ')), (189,2,UPPER('СЕМЬИ ПОГИБШИХ (РЯДОВОЙ СОСТАВ) ПРИ ПРОХОЖДЕНИИ СРОЧНОЙ ВОИНСКОЙ СЛУЖБЫ, ИСПОЛНЯВШИХ СВОЙ ДОЛГ В МИ')), (190,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('211')),
(191,1,UPPER('ПРИЕМНЫЕ СЕМЬИ')), (191,2,UPPER('ПРИЕМНЫЕ СЕМЬИ')), (192,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('212')),
(193,1,UPPER('ДВОРНИКИ')), (193,2,UPPER('ДВОРНИКИ')), (194,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('300')),
(195,1,UPPER('АВАРИЙНО-ДИСПЕТЧЕРСКАЯ СЛУЖБА')), (195,2,UPPER('АВАРИЙНО-ДИСПЕТЧЕРСКАЯ СЛУЖБА')), (196,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('301')),
(197,1,UPPER('ПРИЕМНЫЕ СЕМЬИ')), (197,2,UPPER('ПРИЕМНЫЕ СЕМЬИ')), (198,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('303')),
(199,1,UPPER('СОЦИАЛЬНЫЕ РАБОЧИЕ')), (199,2,UPPER('СОЦИАЛЬНЫЕ РАБОЧИЕ')), (200,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('304')),
(201,1,UPPER('УХОД ЗА ИНВАЛИДОМ 1 ГРУППЫ ВОВ')), (201,2,UPPER('УХОД ЗА ИНВАЛИДОМ 1 ГРУППЫ ВОВ')), (202,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('305')),
(203,1,UPPER('РАБОТНИКИ ХКП "ГОРЭЛЕКТРОТРАНС"')), (203,2,UPPER('РАБОТНИКИ ХКП "ГОРЭЛЕКТРОТРАНС"')), (204,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('306')),
(205,1,UPPER('АФГАНИСТАН')), (205,2,UPPER('АФГАНИСТАН')), (206,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('633')),
(207,1,UPPER('ВЕТЕРАН НАЛОГОВОЙ МИЛИЦИИ')), (207,2,UPPER('ВЕТЕРАН НАЛОГОВОЙ МИЛИЦИИ')), (208,(SELECT `id` FROM `locale` WHERE `system` = 1),UPPER('45'));

INSERT INTO `privilege_attribute`(`attribute_id`, `object_id`, `entity_attribute_id`, `value_id`)
VALUES (1,1,1200,1),(1,1,1201,2),
  (1,2,1200,3),(1,2,1201,4),
  (1,3,1200,5),(1,3,1201,6),
  (1,4,1200,7),(1,4,1201,8),
  (1,5,1200,9),(1,5,1201,10),
  (1,6,1200,11),(1,6,1201,12),
  (1,7,1200,13),(1,7,1201,14),
  (1,8,1200,15),(1,8,1201,16),
  (1,9,1200,17),(1,9,1201,18),
  (1,10,1200,19),(1,10,1201,20),
  (1,11,1200,21),(1,11,1201,22),
  (1,12,1200,23),(1,12,1201,24),
  (1,13,1200,25),(1,13,1201,26),
  (1,14,1200,27),(1,14,1201,28),
  (1,15,1200,29),(1,15,1201,30),
  (1,16,1200,31),(1,16,1201,32),
  (1,17,1200,33),(1,17,1201,34),
  (1,18,1200,35),(1,18,1201,36),
  (1,19,1200,37),(1,19,1201,38),
  (1,20,1200,39),(1,20,1201,40),
  (1,21,1200,41),(1,21,1201,42),
  (1,22,1200,43),(1,22,1201,44),
  (1,23,1200,45),(1,23,1201,46),
  (1,24,1200,47),(1,24,1201,48),
  (1,25,1200,49),(1,25,1201,50),
  (1,26,1200,51),(1,26,1201,52),
  (1,27,1200,53),(1,27,1201,54),
  (1,28,1200,55),(1,28,1201,56),
  (1,29,1200,57),(1,29,1201,58),
  (1,30,1200,59),(1,30,1201,60),
  (1,31,1200,61),(1,31,1201,62),
  (1,32,1200,63),(1,32,1201,64),
  (1,33,1200,65),(1,33,1201,66),
  (1,34,1200,67),(1,34,1201,68),
  (1,35,1200,69),(1,35,1201,70),
  (1,36,1200,71),(1,36,1201,72),
  (1,37,1200,73),(1,37,1201,74),
  (1,38,1200,75),(1,38,1201,76),
  (1,39,1200,77),(1,39,1201,78),
  (1,40,1200,79),(1,40,1201,80),
  (1,41,1200,81),(1,41,1201,82),
  (1,42,1200,83),(1,42,1201,84),
  (1,43,1200,85),(1,43,1201,86),
  (1,44,1200,87),(1,44,1201,88),
  (1,45,1200,89),(1,45,1201,90),
  (1,46,1200,91),(1,46,1201,92),
  (1,47,1200,93),(1,47,1201,94),
  (1,48,1200,95),(1,48,1201,96),
  (1,49,1200,97),(1,49,1201,98),
  (1,50,1200,99),(1,50,1201,100),
  (1,51,1200,101),(1,51,1201,102),
  (1,52,1200,103),(1,52,1201,104),
  (1,53,1200,105),(1,53,1201,106),
  (1,54,1200,107),(1,54,1201,108),
  (1,55,1200,109),(1,55,1201,110),
  (1,56,1200,111),(1,56,1201,112),
  (1,57,1200,113),(1,57,1201,114),
  (1,58,1200,115),(1,58,1201,116),
  (1,59,1200,117),(1,59,1201,118),
  (1,60,1200,119),(1,60,1201,120),
  (1,61,1200,121),(1,61,1201,122),
  (1,62,1200,123),(1,62,1201,124),
  (1,63,1200,125),(1,63,1201,126),
  (1,64,1200,127),(1,64,1201,128),
  (1,65,1200,129),(1,65,1201,130),
  (1,66,1200,131),(1,66,1201,132),
  (1,67,1200,133),(1,67,1201,134),
  (1,68,1200,135),(1,68,1201,136),
  (1,69,1200,137),(1,69,1201,138),
  (1,70,1200,139),(1,70,1201,140),
  (1,71,1200,141),(1,71,1201,142),
  (1,72,1200,143),(1,72,1201,144),
  (1,73,1200,145),(1,73,1201,146),
  (1,74,1200,147),(1,74,1201,148),
  (1,75,1200,149),(1,75,1201,150),
  (1,76,1200,151),(1,76,1201,152),
  (1,77,1200,153),(1,77,1201,154),
  (1,78,1200,155),(1,78,1201,156),
  (1,79,1200,157),(1,79,1201,158),
  (1,80,1200,159),(1,80,1201,160),
  (1,81,1200,161),(1,81,1201,162),
  (1,82,1200,163),(1,82,1201,164),
  (1,83,1200,165),(1,83,1201,166),
  (1,84,1200,167),(1,84,1201,168),
  (1,85,1200,169),(1,85,1201,170),
  (1,86,1200,171),(1,86,1201,172),
  (1,87,1200,173),(1,87,1201,174),
  (1,88,1200,175),(1,88,1201,176),
  (1,89,1200,177),(1,89,1201,178),
  (1,90,1200,179),(1,90,1201,180),
  (1,91,1200,181),(1,91,1201,182),
  (1,92,1200,183),(1,92,1201,184),
  (1,93,1200,185),(1,93,1201,186),
  (1,94,1200,187),(1,94,1201,188),
  (1,95,1200,189),(1,95,1201,190),
  (1,96,1200,191),(1,96,1201,192),
  (1,97,1200,193),(1,97,1201,194),
  (1,98,1200,195),(1,98,1201,196),
  (1,99,1200,197),(1,99,1201,198),
  (1,100,1200,199),(1,100,1201,200),
  (1,101,1200,201),(1,101,1201,202),
  (1,102,1200,203),(1,102,1201,204),
  (1,103,1200,205),(1,103,1201,206),
  (1,104,1200,207),(1,104,1201,208);

-- Status descriptions
INSERT INTO `status_description`(`code`, `name`) VALUES
(110,'Загружено'), (111,'Ошибка загрузки'), (112,'Загружается'),
(120,'Связано'), (121,'Ошибка связывания'), (122,'Связывается'),
(130,'Обработано'), (131,'Ошибка обработки'), (132,'Обрабатывается'),
(140,'Выгружено'), (141,'Ошибка выгрузки'), (142,'Выгружается'),
(240,'Загружена'),
(200,'Неизвестный населенный пункт'), (237, 'Неизвестный тип улицы'), (201,'Неизвестная улица'),
(231,'Соответсвие для дома не может быть установлено'), (202,'Неизвестный номер дома'),
(234,'Найдено более одного населенного пункта в адресной базе'), (238, 'Найдено более одного типа улицы в адресной базе'), (235,'Найдено более одной улицы в адресной базе'),
(236,'Найдено более одного дома в адресной базе'), (210,'Найдено более одного соответствия для населенного пункта'), (239, 'Найдено более одного соответствия для типа улицы'),
(211,'Найдено более одного соответствия для улицы'), (228,'Найдено более одного соответствия для дома'),
(204,'Адрес откорректирован'), (205,'Населенный пункт не найден в соответствиях МН'), (206,'Район не найден в соответствиях МН'),
(207,'Тип улицы не найден в соответствиях МН'), (208,'Улица не найдена в соответствиях МН'), (209,'Дом не найден в соответствиях МН'),
(229, 'Более одного населенного пункта найдено в соответствиях МН'), (230,'Более одного района найдено в соответствиях МН'),
(232,'Более одной улицы найдено в соответствиях МН'),(233,'Более одного дома найдено в соответствиях МН'),
(212,'Номер личного счета не разрешён'), (213,'Больше одного личного счета'), (242,'Более одного л/с в таблице счетов абонентов'), (241,'Несоответствие номера л/с'), (214,'Номер личного счета разрешен'),
(215,'Запись обработана'), (216,'Код тарифа на оплату жилья не найден в справочнике тарифов для запросов по субсидиям'), (217,'Не сопоставлен носитель льготы'),
(218,'Льгота не найдена в справочнике соответствий'), (219,'Неверный формат данных на этапе обработки'), (203,'Неверный формат данных на этапе связывания'),
(220, 'Нет запроса оплаты'), (221, 'Населенный пункт не найден в МН'), (222, 'Район не найден в МН'), (223, 'Тип улицы не найден в МН'),
(224, 'Улица не найдена в МН'), (225, 'Дом не найден в МН'), (226, 'Корпус дома не найден в МН'), (227, 'Квартира не найдена в МН'),
(300, 'Тариф не найден в справочнике тарифов для запросов по субсидиям'), (301, 'Объект формы собственности не найден в справочнике соответствий для МН'),
(302, 'Код формы собственности не найден в справочнике соответствий для ОСЗН'), (303, 'Нечисловой код формы собственности в справочнике соответствий для ОСЗН'),
(304, 'Объект льготы не найден в справочнике соответствий для МН'), (305, 'Код льготы не найден в справочнике соответствий для ОСЗН'),
(306, 'Нечисловой код льготы в справочнике соответствий для ОСЗН'), (307, 'Нечисловой порядок льготы'),
(308, 'Номер л/с ЖЭКа в МН не соответствует шаблону: <номер ЖЭКа>.<номер л/с ЖЭКа>.');

-- Type description
INSERT INTO `type_description`(`code`, `name`) VALUES
(1, 'Льгота запроса на субсидию'), (2, 'Начисление запроса на субсидию'), (3, 'Тариф запроса на субсидию'),
(4, 'Фактическое начисление'),(5, 'Субсидия'),(6, 'Характеристики жилья'),(7, 'Виды услуг'),(8, 'Форма-2 льгота'),
(9, 'Типы улиц запроса по льготам'),(10, 'Улицы запроса по льготам'),(11, 'Тарифы запроса по льготам');

-- Itself organization
INSERT INTO `organization`(`object_id`) VALUES (0);
INSERT INTO `organization_string_value`(`id`, `locale_id`, `value`) VALUES
(1, 1, UPPER('Модуль №1')), (1,2,UPPER('Модуль №1')), (2, (SELECT `id` FROM `locale` WHERE `system` = 1), UPPER('0'));
INSERT INTO `organization_attribute`(`attribute_id`, `object_id`, `entity_attribute_id`, `value_id`) VALUES
(1,0,900,1), (1,0,901,2);

