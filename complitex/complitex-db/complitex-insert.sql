-- --------------------------------
-- Config
-- --------------------------------

INSERT INTO `config` (`name`,`value`) VALUES ('IMPORT_FILE_STORAGE_DIR','c:\\storage\\import');
INSERT INTO `config` (`name`,`value`) VALUES ('SYNC_DATA_SOURCE','');
INSERT INTO `config` (`name`,`value`) VALUES ('MODULE_ID','0');

-- --------------------------------
-- Locale
-- --------------------------------

INSERT INTO `locale`(`id`, `locale`, `system`, `alternative`) VALUES (1, 'ru', 1, 0);
INSERT INTO `locale`(`id`, `locale`, `system`, `alternative`) VALUES (2, 'uk', 0, 1);

-- --------------------------------
-- Sequence
-- --------------------------------

INSERT INTO `sequence` (`sequence_name`, `sequence_value`) VALUES
('string_value',1),
('apartment',1), ('apartment_string_value',1),
('building',1), ('building_string_value',1),
('building_address',1), ('building_address_string_value',1),
('country',1), ('country_string_value',1),
('district',1), ('district_string_value',1),
('city',1), ('city_string_value',1),
('city_type',1), ('city_type_string_value',1),
('region',1), ('region_string_value',1),
('room',1), ('room_string_value',1),
('street',1), ('street_string_value',1),
('street_type',1), ('street_type_string_value',1),
('organization',1), ('organization_string_value',1),
('organization_type',1), ('organization_type_string_value',1),
('user_info', 3), ('user_info_string_value', 1), ('service', 1), ('service_string_value', 1);


-- --------------------------------
-- Value type
-- --------------------------------

INSERT INTO entity_value_type (id, value_type) VALUE (0, 'string_value');
INSERT INTO entity_value_type (id, value_type) VALUE (1, 'string');
INSERT INTO entity_value_type (id, value_type) VALUE (2, 'boolean');
INSERT INTO entity_value_type (id, value_type) VALUE (3, 'decimal');
INSERT INTO entity_value_type (id, value_type) VALUE (4, 'integer');
INSERT INTO entity_value_type (id, value_type) VALUE (5, 'date');

INSERT INTO entity_value_type (id, value_type) VALUE (10, 'entity');

INSERT INTO entity_value_type (id, value_type) VALUE (20, 'building_code');
INSERT INTO entity_value_type (id, value_type) VALUE (21, 'last_name');
INSERT INTO entity_value_type (id, value_type) VALUE (22, 'first_name');
INSERT INTO entity_value_type (id, value_type) VALUE (23, 'middle_name');

-- Permission
INSERT INTO `permission` (`permission_id`, `table`, `entity`, `object_id`) VALUES (0, 'ALL', 'ALL', 0);
INSERT INTO `sequence` (`sequence_name`, `sequence_value`) VALUES ('permission', 1);

-- --------------------------------
-- Apartment
-- --------------------------------

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (100, 1, 'Квартира'), (100, 2, 'Квартира');
INSERT INTO `entity`(`id`, `entity`, `name_id`, `strategy_factory`) VALUES (100, 'apartment', 100, '');
INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (101, 1, UPPER('Номер квартиры')), (101, 2, UPPER('Номер квартири'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (100, 100, 1, 101, 1, 0);

-- --------------------------------
-- Room
-- --------------------------------

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (200, 1, 'Комната'), (200, 2, 'Кімната');
INSERT INTO `entity`(`id`, `entity`, `name_id`, `strategy_factory`) VALUES (200, 'room', 200, '');
INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (201, 1, UPPER('Номер комнаты')), (201, 2, UPPER('Номер кімнати'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (200, 200, 1, 201, 1, 0);

-- --------------------------------
-- Street
-- --------------------------------

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (300, 1, 'Улица'), (300, 2, 'Вулиця');
INSERT INTO `entity`(`id`, `entity`, `name_id`, `strategy_factory`) VALUES (300, 'street', 300, '');

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (301, 1, UPPER('Наименование улицы')), (301, 2, UPPER('Найменування вулиці'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (300, 300, 1, 301, 1, 0);

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (302, 1, UPPER('Тип улицы')),(302, 2, UPPER('Тип улицы'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`, `reference_id`) VALUES (301, 300, 1, 302, 1, 10, 1400);

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (303, 1, UPPER('Код улицы')),(303, 2, UPPER('Код улицы'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (303, 300, 0, 303, 1, 1);

-- --------------------------------
-- Street Type
-- --------------------------------

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (1400, 1, 'Тип улицы'), (1400, 2, 'Тип улицы');
INSERT INTO `entity`(`id`, `entity`, `name_id`, `strategy_factory`) VALUES (1400, 'street_type', 1400, '');

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (1401, 1, UPPER('Краткое название')), (1401, 2, UPPER('Краткое название'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (1400, 1400, 1, 1401, 1, 0);

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (1402, 1, UPPER('Название')), (1402, 2, UPPER('Название'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (1401, 1400, 1, 1402, 1, 0);

-- --------------------------------
-- City
-- --------------------------------

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (400, 1, 'Населенный пункт'), (400, 2, 'Населений пункт');
INSERT INTO `entity`(`id`, `entity`, `name_id`, `strategy_factory`) VALUES (400, 'city', 400, '');

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (401, 1, UPPER('Наименование населенного пункта')), (401, 2, UPPER('Найменування населеного пункту'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (400, 400, 1, 401, 1, 0);

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (402, 1, UPPER('Тип населенного пункта')), (402, 2, UPPER('Тип населенного пункта'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`, `reference_id`) VALUES (401, 400, 1, 402, 1, 10, 1300);

-- --------------------------------
-- City Type
-- --------------------------------

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (1300, 1, 'Тип нас. пункта'), (1300, 2, 'Тип населенного пункта');
INSERT INTO `entity`(`id`, `entity`, `name_id`, `strategy_factory`) VALUES (1300, 'city_type', 1300, '');

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (1301, 1, UPPER('Краткое название')), (1301, 2, UPPER('Краткое название'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (1300, 1300, 1, 1301, 1, 0);

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (1302, 1, UPPER('Название')), (1302, 2, UPPER('Название'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (1301, 1300, 1, 1302, 1, 0);

-- --------------------------------
-- Building
-- --------------------------------

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (500, 1, 'Дом'), (500, 2, 'Будинок');
INSERT INTO `entity`(`id`, `entity`, `name_id`, `strategy_factory`) VALUES (500, 'building', 500, '');

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (501, 1, UPPER('Номер дома')), (501, 2, UPPER('Номер будинку'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (500, 500, 1, 501, 1, 0);

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (502, 1, UPPER('Корпус')), (502, 2, UPPER('Корпус'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (501, 500, 0, 502, 1, 0);

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (503, 1, UPPER('Строение')), (503, 2, UPPER('Будова'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (502, 500, 0, 503, 1, 0);


INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (504, 1, UPPER('Район')), (504, 2, UPPER('Район'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`, `reference_id`) VALUES (503, 500, 0, 504, 1, 10, 600);

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (505, 1, UPPER('Список кодов дома')), (505, 2, UPPER('Список кодов дома'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (504, 500, 0, 505, 1, 20);

-- --------------------------------
-- District
-- --------------------------------

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (600, 1, 'Район'), (600, 2, 'Район');
INSERT INTO `entity`(`id`, `entity`, `name_id`, `strategy_factory`) VALUES (600, 'district', 600, '');

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (601, 1, UPPER('Наименование района')), (601, 2, UPPER('Найменування району'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (600, 600, 1, 601, 1, 0);

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (602, 1, UPPER('Код района')), (602, 2, UPPER('Код району'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (601, 600, 1, 602, 1, 1);

-- --------------------------------
-- Region
-- --------------------------------

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (700, 1, 'Регион'), (700, 2, 'Регіон');
INSERT INTO `entity`(`id`, `entity`, `name_id`, `strategy_factory`) VALUES (700, 'region', 700, '');

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (701, 1, UPPER('Наименование региона')), (701, 2, UPPER('Найменування регіону'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (700, 700, 1, 701, 1, 0);

-- --------------------------------
-- Country
-- --------------------------------

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (800, 1, 'Страна'), (800, 2, 'Країна');
INSERT INTO `entity`(`id`, `entity`, `name_id`, `strategy_factory`) VALUES (800, 'country', 800, '');

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (801, 1, UPPER('Наименование страны')), (801, 2, UPPER('Найменування країни'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (800, 800, 1, 801, 1, 0);


-- --------------------------------
-- User Info
-- --------------------------------

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (1000, 1, 'Пользователь'), (1000, 2, 'Користувач');
INSERT INTO `entity`(`id`, `entity`, `name_id`, `strategy_factory`) VALUES (1000, 'user_info', 1000, '');

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (1001, 1, UPPER('Фамилия')), (1001, 2, UPPER('Прізвище'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, value_type_id) VALUES (1000, 1000, 1, 1001, 1, 21);

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (1002, 1, UPPER('Имя')), (1002, 2, UPPER('Ім\'я'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (1001, 1000, 1, 1002, 1, 22);

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (1003, 1, UPPER('Отчество')), (1003, 2, UPPER('По батькові'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (1002, 1000, 1, 1003, 1, 23);

-- anonymous user --
INSERT INTO `user_info` (`object_id`) VALUES (2);
INSERT INTO `first_name`(`id`, `name`) VALUES (2,'ANONYMOUS');
INSERT INTO `middle_name`(`id`, `name`) VALUES (2,'ANONYMOUS');
INSERT INTO `last_name`(`id`, `name`) VALUES (2,'ANONYMOUS');

INSERT INTO `user_info_attribute` (`attribute_id`, `object_id`, `entity_attribute_id`, `value_id`)
    VALUES (1,2,1000,2), (1,2,1001,2), (1,2,1002,2);
INSERT INTO `user` (`id`, `login`, `password`, `user_info_object_id`)  VALUES (2, 'ANONYMOUS', 'ANONYMOUS', 2);

-- --------------------------------
-- Organization type
-- --------------------------------

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (2300, 1, 'Тип организации'), (2300, 2, 'Тип организации');
INSERT INTO `entity`(`id`, `entity`, `name_id`, `strategy_factory`) VALUES (2300, 'organization_type', 2300, '');

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (2301, 1, UPPER('Тип организации')), (2301, 2, UPPER('Тип организации'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (2300, 2300, 1, 2301, 1, 0);

INSERT INTO `organization_type`(`object_id`) VALUES (1);
INSERT INTO `organization_type_string_value`(`id`, `locale_id`, `value`) VALUES (1, 1, UPPER('Организации пользователей')),
  (1, 2,UPPER('Организации пользователей'));
INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `entity_attribute_id`, `value_id`) VALUES (1,1,2300,1);

INSERT INTO `organization_type`(`object_id`) VALUES (4);
INSERT INTO `organization_type_string_value`(`id`, `locale_id`, `value`) VALUES (4, 1, UPPER('ОБСЛУЖИВАЮЩАЯ ОРГАНИЗАЦИЯ')),
  (4, 2, UPPER('ОБСЛУЖИВАЮЩАЯ ОРГАНИЗАЦИЯ'));
INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `entity_attribute_id`, `value_id`) VALUES (1, 4, 2300, 4);

INSERT INTO `organization_type`(`object_id`) VALUES (2);
INSERT INTO `organization_type_string_value`(`id`, `locale_id`, `value`) VALUES (2, 1, UPPER('МОДУЛЬ НАЧИСЛЕНИЙ')),
  (2, 2, UPPER('МОДУЛЬ НАЧИСЛЕНИЙ'));
INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `entity_attribute_id`, `value_id`) VALUES (1, 2, 2300, 2);

INSERT INTO `organization_type`(`object_id`) VALUES (3);
INSERT INTO `organization_type_string_value`(`id`, `locale_id`, `value`) VALUES (3, 1, UPPER('БАЛАНСОДЕРЖАТЕЛЬ')),
  (3, 2, UPPER('БАЛАНСОДЕРЖАТЕЛЬ'));
INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `entity_attribute_id`, `value_id`) VALUES (1, 3, 2300, 3);

INSERT INTO `organization_type`(`object_id`) VALUES (5);
INSERT INTO `organization_type_string_value`(`id`, `locale_id`, `value`) VALUES (5, 1, UPPER('ПОСТАВЩИК УСЛУГ')),
  (5, 2, UPPER('ПОСТАВЩИК УСЛУГ'));
INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `entity_attribute_id`, `value_id`) VALUES (1, 5, 2300, 5);

INSERT INTO `organization_type`(`object_id`) VALUES (6);
INSERT INTO `organization_type_string_value`(`id`, `locale_id`, `value`)
  VALUES (6, 1, UPPER('ПОДРЯДЧИК')), (6, 2, UPPER('ПОДРЯДЧИК'));
INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `entity_attribute_id`, `value_id`)
  VALUES (1, 6, 2300, 6);

-- --------------------------------
-- Organization
-- --------------------------------

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (900, 1, 'Организация'), (900, 2, 'Організація');
INSERT INTO `entity`(`id`, `entity`, `name_id`, `strategy_factory`) VALUES (900, 'organization', 900, '');

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (901, 1, UPPER('Наименование организации')), (901, 2, UPPER('Найменування організації'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (900, 900, 1, 901, 1, 0);

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (902, 1, UPPER('Уникальный код организации')), (902, 2, UPPER('Унікальний код організації'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (901, 900, 1, 902, 1, 1);

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (903, 1, UPPER('Район')), (903, 2, UPPER('Район'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`, `reference_id`) VALUES (902, 900, 0, 903, 1, 10, 600);

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (904, 1, UPPER('Родительская организация')), (904, 2, UPPER('Родительская организация'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`, `reference_id`) VALUES (903, 900, 0, 904, 1, 10, 900);

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (905, 1, UPPER('Тип организации')), (905, 2, UPPER('Тип организации'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`, `reference_id`) VALUES (904, 900, 0, 905, 1, 10, 2300);

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (906, 1, UPPER('Короткое наименование')), (906, 2, UPPER('Короткое наименование'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (906, 900, 0, 906, 1, 0);

-- Reference to jdbc data source. It is calculation center only attribute. --
INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (913, 1, UPPER('Ресурс доступа к МН')), (913, 2, UPPER('Ресурс доступа к МН'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (913, 900, 0, 913, 1, 1);

-- Service --
INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (4914, 1, UPPER('Услуга')), (4914, 2, UPPER('Услуга'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`, `reference_id`) VALUES (4914, 900, 1, 4914, 1, 10, 1600);

-- Billing --
INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (4915, 1, UPPER('Модуль начислений')), (4915, 2, UPPER('Модуль начислений'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`, `reference_id`) VALUES (4915, 900, 1, 4915, 1, 10, 900);

-- ------------------------------
-- Service
-- ------------------------------
INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (1600, 1, 'Услуга'), (1600, 2, 'Услуга');
INSERT INTO `entity`(`id`, `entity`, `name_id`, `strategy_factory`) VALUES (1600, 'service', 1600, '');

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (1601, 1, UPPER('Название')), (1601, 2, UPPER('Название'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (1601, 1600, 1, 1601, 1, 0);

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (1602, 1, UPPER('Короткое название')), (1602, 2, UPPER('Короткое название'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (1602, 1600, 1, 1602, 1, 0);

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (1603, 1, UPPER('Код')), (1603, 2, UPPER('Код'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (1603, 1600, 0, 1603, 1, 0);

INSERT INTO `service`(`object_id`) VALUES (1),(2),(3),(4),(5),(6),(7),(8);
INSERT INTO `service_string_value`(`id`, `locale_id`, `value`) VALUES
    (1, 1, UPPER('квартплата')), (1, 2,UPPER('квартплата')),
    (2, 1, UPPER('отопление')), (2, 2, UPPER('опалення')),
    (3, 1, UPPER('горячая вода')), (3, 2, UPPER('гаряча вода')),
    (4, 1, UPPER('холодная вода')), (4, 2, UPPER('холодна вода')),
    (5, 1, UPPER('газ')), (5, 2, UPPER('газ')),
    (6, 1, UPPER('электроэнергия')), (6, 2, UPPER('електроенергія')),
    (7, 1, UPPER('вывоз мусора')), (7, 2, UPPER('вивіз сміття')),
    (8, 1, UPPER('водоотведение')), (8, 2, UPPER('вивіз нечистот'));

INSERT INTO `service_attribute`(`attribute_id`, `object_id`, `entity_attribute_id`, `value_id`)
  VALUES (1,1,1601,1),(1,2,1601,2),(1,3,1601,3),(1,4,1601,4),(1,5,1601,5),(1,6,1601,6), (1,7,1601,7),(1,8,1601,8);