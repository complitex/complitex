-- --------------------------------
-- Config
-- --------------------------------

INSERT INTO `config` (`name`,`value`) VALUES ('IMPORT_FILE_STORAGE_DIR','c:\\storage\\import');
INSERT INTO `config` (`name`,`value`) VALUES ('SYNC_DATA_SOURCE','2');
INSERT INTO `config` (`name`,`value`) VALUES ('MODULE_ID','0');

-- --------------------------------
-- Locale
-- --------------------------------

INSERT INTO `locales`(`id`, `locale`, `system`) VALUES (1, 'ru', 1);
INSERT INTO `locales`(`id`, `locale`, `system`) VALUES (2, 'uk', 0);

-- --------------------------------
-- Sequence
-- --------------------------------

INSERT INTO `sequence` (`sequence_name`, `sequence_value`) VALUES
('string_culture',1),
('apartment',1), ('apartment_string_culture',1),
('building',1), ('building_string_culture',1),
('building_address',1), ('building_address_string_culture',1),
('country',1), ('country_string_culture',1),
('district',1), ('district_string_culture',1),
('city',1), ('city_string_culture',1),
('city_type',1), ('city_type_string_culture',1),
('region',1), ('region_string_culture',1),
('room',1), ('room_string_culture',1),
('street',1), ('street_string_culture',1),
('street_type',1), ('street_type_string_culture',1),
('organization',1), ('organization_string_culture',1),
('organization_type',1), ('organization_type_string_culture',1),
('user_info', 3), ('user_info_string_culture', 1);

-- Permission
INSERT INTO `permission` (`permission_id`, `table`, `entity`, `object_id`) VALUES (0, 'ALL', 'ALL', 0);
INSERT INTO `sequence` (`sequence_name`, `sequence_value`) VALUES ('permission', 1);

-- --------------------------------
-- Apartment
-- --------------------------------

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (100, 1, 'Квартира'), (100, 2, 'Квартира');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (100, 'apartment', 100, '');
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (101, 1, UPPER('Номер квартиры')), (101, 2, UPPER('Номер квартири'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (100, 100, 1, 101, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (100, 100, UPPER('string_culture'));

-- --------------------------------
-- Room
-- --------------------------------

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (200, 1, 'Комната'), (200, 2, 'Кімната');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (200, 'room', 200, '');
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (201, 1, UPPER('Номер комнаты')), (201, 2, UPPER('Номер кімнати'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (200, 200, 1, 201, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (200, 200, UPPER('string_culture'));

-- --------------------------------
-- Street
-- --------------------------------

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (300, 1, 'Улица'), (300, 2, 'Вулиця');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (300, 'street', 300, '');
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (301, 1, UPPER('Наименование улицы')), (301, 2, UPPER('Найменування вулиці'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (300, 300, 1, 301, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (300, 300, UPPER('string_culture'));
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (302, 1, UPPER('Тип улицы')),(302, 2, UPPER('Тип улицы'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (301, 300, 1, 302, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (301, 301, 'street_type');

-- --------------------------------
-- Street Type
-- --------------------------------

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (1400, 1, 'Тип улицы'), (1400, 2, 'Тип улицы');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (1400, 'street_type', 1400, '');
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (1401, 1, UPPER('Краткое название')), (1401, 2, UPPER('Краткое название'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (1400, 1400, 1, 1401, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (1400, 1400, UPPER('string_culture'));
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (1402, 1, UPPER('Название')), (1402, 2, UPPER('Название'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (1401, 1400, 1, 1402, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (1401, 1401, UPPER('string_culture'));

-- --------------------------------
-- City
-- --------------------------------

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (400, 1, 'Населенный пункт'), (400, 2, 'Населений пункт');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (400, 'city', 400, '');
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (401, 1, UPPER('Наименование населенного пункта')), (401, 2, UPPER('Найменування населеного пункту'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (400, 400, 1, 401, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (400, 400, UPPER('string_culture'));
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (402, 1, UPPER('Тип населенного пункта')), (402, 2, UPPER('Тип населенного пункта'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (401, 400, 1, 402, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (401, 401, 'city_type');

-- --------------------------------
-- City Type
-- --------------------------------

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (1300, 1, 'Тип нас. пункта'), (1300, 2, 'Тип населенного пункта');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (1300, 'city_type', 1300, '');
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (1301, 1, UPPER('Краткое название')), (1301, 2, UPPER('Краткое название'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (1300, 1300, 1, 1301, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (1300, 1300, UPPER('string_culture'));
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (1302, 1, UPPER('Название')), (1302, 2, UPPER('Название'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (1301, 1300, 1, 1302, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (1301, 1301, UPPER('string_culture'));

-- --------------------------------
-- Building
-- --------------------------------

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (500, 1, 'Дом'), (500, 2, 'Будинок');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (500, 'building', 500, '');
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (501, 1, UPPER('Район')), (501, 2, UPPER('Район'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (500, 500, 0, 501, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (500, 500, 'district');
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (502, 1, UPPER('Альтернативный адрес')), (502, 2, UPPER('Альтернативный адрес'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (501, 500, 0, 502, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (501, 501, 'building_address');
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (503, 1, UPPER('Список кодов дома')), (503, 2, UPPER('Список кодов дома'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (502, 500, 0, 503, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (502, 502, 'building_organization_association');


-- --------------------------------
-- Building Address
-- --------------------------------

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (1500, 1, 'Адрес здания'), (1500, 2, 'Адрес здания');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (1500, 'building_address', 1500, '');
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (1501, 1, UPPER('Номер дома')), (1501, 2, UPPER('Номер будинку'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (1500, 1500, 1, 1501, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (1502, 1, UPPER('Корпус')), (1502, 2, UPPER('Корпус'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (1501, 1500, 0, 1502, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (1503, 1, UPPER('Строение')), (1503, 2, UPPER('Будова'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (1502, 1500, 0, 1503, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (1500, 1500, UPPER('string_culture'));
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (1501, 1501, UPPER('string_culture'));
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (1502, 1502, UPPER('string_culture'));

-- --------------------------------
-- District
-- --------------------------------

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (600, 1, 'Район'), (600, 2, 'Район');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (600, 'district', 600, '');
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (601, 1, UPPER('Наименование района')), (601, 2, UPPER('Найменування району'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (600, 600, 1, 601, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (600, 600, UPPER('string_culture'));
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (602, 1, UPPER('Код района')), (602, 2, UPPER('Код району'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (601, 600, 1, 602, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (601, 601, UPPER('string'));

-- --------------------------------
-- Region
-- --------------------------------

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (700, 1, 'Регион'), (700, 2, 'Регіон');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (700, 'region', 700, '');
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (701, 1, UPPER('Наименование региона')), (701, 2, UPPER('Найменування регіону'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (700, 700, 1, 701, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (700, 700, UPPER('string_culture'));

-- --------------------------------
-- Country
-- --------------------------------

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (800, 1, 'Страна'), (800, 2, 'Країна');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (800, 'country', 800, '');
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (801, 1, UPPER('Наименование страны')), (801, 2, UPPER('Найменування країни'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (800, 800, 1, 801, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (800, 800, UPPER('string_culture'));


-- --------------------------------
-- User Info
-- --------------------------------

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (1000, 1, 'Пользователь'), (1000, 2, 'Користувач');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (1000, 'user_info', 1000, '');
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (1001, 1, UPPER('Фамилия')), (1001, 2, UPPER('Прізвище'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (1000, 1000, 1, 1001, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (1000, 1000, 'last_name');
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (1002, 1, UPPER('Имя')), (1002, 2, UPPER('Ім\'я'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (1001, 1000, 1, 1002, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (1001, 1001, 'first_name');
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (1003, 1, UPPER('Отчество')), (1003, 2, UPPER('По батькові'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (1002, 1000, 1, 1003, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (1002, 1002, 'middle_name');

-- admin user --
INSERT INTO `user_info` (`object_id`) VALUES (1);
INSERT INTO `first_name`(`id`, `name`) VALUES (1,'admin');
INSERT INTO `middle_name`(`id`, `name`) VALUES (1,'admin');
INSERT INTO `last_name`(`id`, `name`) VALUES (1,'admin');
INSERT INTO `user_info_attribute` (`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`)
    VALUES (1,1,1000,1,1000), (1,1,1001,1,1001), (1,1,1002,1,1002);
INSERT INTO `user` (`id`, `login`, `password`, `user_info_object_id`) VALUES (1, 'admin', '21232f297a57a5a743894a0e4a801fc3', 1);
INSERT INTO `usergroup` (`id`, `login`, `group_name`) VALUES (1, 'admin', 'ADMINISTRATORS');
INSERT INTO `usergroup` (`id`, `login`, `group_name`) VALUES (2, 'admin', 'EMPLOYEES');
INSERT INTO `usergroup` (`id`, `login`, `group_name`) VALUES (3, 'admin', 'EMPLOYEES_CHILD_VIEW');

-- anonymous user --
INSERT INTO `user_info` (`object_id`) VALUES (2);
INSERT INTO `first_name`(`id`, `name`) VALUES (2,'ANONYMOUS');
INSERT INTO `middle_name`(`id`, `name`) VALUES (2,'ANONYMOUS');
INSERT INTO `last_name`(`id`, `name`) VALUES (2,'ANONYMOUS');
INSERT INTO `user_info_attribute` (`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`)
    VALUES (1,2,1000,2,1000), (1,2,1001,2,1001), (1,2,1002,2,1002);
INSERT INTO `user` (`id`, `login`, `password`, `user_info_object_id`)  VALUES (2, 'ANONYMOUS', 'ANONYMOUS', 2);

-- --------------------------------
-- Organization type
-- --------------------------------

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2300, 1, 'Тип организации'), (2300, 2, 'Тип организации');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (2300, 'organization_type', 2300, '');
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2301, 1, UPPER('Тип организации')), (2301, 2, UPPER('Тип организации'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2300, 2300, 1, 2301, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2300, 2300, UPPER('string_culture'));

INSERT INTO `organization_type`(`object_id`) VALUES (1);
INSERT INTO `organization_type_string_culture`(`id`, `locale_id`, `value`)
  VALUES (1, 1, UPPER('Организации пользователей')), (1, 2,UPPER('Организации пользователей'));
INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`)
  VALUES (1,1,2300,1,2300);

INSERT INTO `organization_type`(`object_id`) VALUES (4);
INSERT INTO `organization_type_string_culture`(`id`, `locale_id`, `value`)
  VALUES (4, 1, UPPER('ОБСЛУЖИВАЮЩАЯ ОРГАНИЗАЦИЯ')), (4, 2, UPPER('ОБСЛУЖИВАЮЩАЯ ОРГАНИЗАЦИЯ'));
INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`) 
  VALUES (1, 4, 2300, 4, 2300);

INSERT INTO `organization_type`(`object_id`) VALUES (2);
INSERT INTO `organization_type_string_culture`(`id`, `locale_id`, `value`)
  VALUES (2, 1, UPPER('МОДУЛЬ НАЧИСЛЕНИЙ')), (2, 2, UPPER('МОДУЛЬ НАЧИСЛЕНИЙ'));
INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`)
  VALUES (1, 2, 2300, 2, 2300);

INSERT INTO `organization_type`(`object_id`) VALUES (3);
INSERT INTO `organization_type_string_culture`(`id`, `locale_id`, `value`)
  VALUES (3, 1, UPPER('БАЛАНСОДЕРЖАТЕЛЬ')), (3, 2, UPPER('БАЛАНСОДЕРЖАТЕЛЬ'));
INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`)
  VALUES (1, 3, 2300, 3, 2300);

INSERT INTO `organization_type`(`object_id`) VALUES (5);
INSERT INTO `organization_type_string_culture`(`id`, `locale_id`, `value`)
  VALUES (5, 1, UPPER('ПОСТАВЩИК УСЛУГ')), (5, 2, UPPER('ПОСТАВЩИК УСЛУГ'));
INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`)
  VALUES (1, 5, 2300, 5, 2300);

INSERT INTO `organization_type`(`object_id`) VALUES (6);
INSERT INTO `organization_type_string_culture`(`id`, `locale_id`, `value`)
  VALUES (6, 1, UPPER('ПОДРЯДЧИК')), (6, 2, UPPER('ПОДРЯДЧИК'));
INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`)
  VALUES (1, 6, 2300, 6, 2300);

-- --------------------------------
-- Organization
-- --------------------------------

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (900, 1, 'Организация'), (900, 2, 'Організація');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (900, 'organization', 900, '');
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (901, 1, UPPER('Наименование организации')), (901, 2, UPPER('Найменування організації'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (900, 900, 1, 901, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (902, 1, UPPER('Уникальный код организации')), (902, 2, UPPER('Унікальний код організації'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (901, 900, 1, 902, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (903, 1, UPPER('Район')), (903, 2, UPPER('Район'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (902, 900, 0, 903, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (904, 1, UPPER('Родительская организация')), (904, 2, UPPER('Родительская организация'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (903, 900, 0, 904, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (905, 1, UPPER('Тип организации')), (905, 2, UPPER('Тип организации'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (904, 900, 0, 905, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (900, 900, UPPER('string_culture'));
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (901, 901, UPPER('string'));
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (902, 902, 'district');
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (903, 903, 'organization');
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (904, 904, 'organization_type');
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (906, 1, UPPER('Короткое наименование')), (906, 2, UPPER('Короткое наименование'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (906, 900, 0, 906, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (906, 906, UPPER('string_culture'));

-- Reference to jdbc data source. It is calculation center only attribute. --
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (913, 1, UPPER('Ресурс доступа к МН')), (913, 2, UPPER('Ресурс доступа к МН'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (913, 900, 1, 913, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (913, 913, UPPER('string'));

-- Service --
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (914, 1, UPPER('Услуга')), (914, 2, UPPER('Услуга'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (914, 900, 1, 914, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (914, 914, UPPER('service'));

-- Billing --
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (915, 1, UPPER('Модуль начислений')), (915, 2, UPPER('Модуль начислений'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (915, 900, 1, 915, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (915, 915, UPPER('organization'));

-- ------------------------------
-- Service
-- ------------------------------
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (1600, 1, 'Услуга'), (1600, 2, 'Услуга');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (1600, 'service', 1600, '');

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (1601, 1, UPPER('Название')), (1601, 2, UPPER('Название'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (1601, 1600, 1, 1601, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (1601, 1601, 'STRING_CULTURE');

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (1602, 1, UPPER('Короткое название')), (1602, 2, UPPER('Короткое название'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (1602, 1600, 1, 1602, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (1602, 1602, 'STRING_CULTURE');

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (1603, 1, UPPER('Код')), (1603, 2, UPPER('Код'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (1603, 1600, 1, 1603, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (1603, 1603, 'STRING_CULTURE');

INSERT INTO `service`(`object_id`) VALUES (1),(2),(3),(4),(5),(6),(7),(8);
INSERT INTO `service_string_culture`(`id`, `locale_id`, `value`) VALUES
    (1, 1, UPPER('квартплата')), (1, 2,UPPER('квартплата')),
    (2, 1, UPPER('отопление')), (2, 2, UPPER('опалення')),
    (3, 1, UPPER('горячая вода')), (3, 2, UPPER('гаряча вода')),
    (4, 1, UPPER('холодная вода')), (4, 2, UPPER('холодна вода')),
    (5, 1, UPPER('газ')), (5, 2, UPPER('газ')),
    (6, 1, UPPER('электроэнергия')), (6, 2, UPPER('електроенергія')),
    (7, 1, UPPER('вывоз мусора')), (7, 2, UPPER('вивіз сміття')),
    (8, 1, UPPER('водоотведение')), (8, 2, UPPER('вивіз нечистот'));

INSERT INTO `service_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`)
  VALUES (1,1,1601,1,1601),(1,2,1601,2,1601),(1,3,1601,3,1601),(1,4,1601,4,1601),(1,5,1601,5,1601),(1,6,1601,6,1601),
    (1,7,1601,7,1601),(1,8,1601,8,1601);