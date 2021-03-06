-- --------------------------------
-- Config
-- --------------------------------

-- INSERT INTO `config` (`name`,`value`) VALUES ('MODULE_ID','-1');
-- INSERT INTO `config` (`name`,`value`) VALUES ('IMPORT_FILE_STORAGE_DIR','/var/tmp/data/import"');
-- INSERT INTO `config` (`name`,`value`) VALUES ('SYNC_DATA_SOURCE','organization_id_here');
INSERT INTO `config` (`name`,`value`) VALUES ('TMP_DIR','/tmp');
INSERT INTO `config` (`name`,`value`) VALUES ('NUMBER_FLUSH_REGISTRY_RECORDS','10000');
INSERT INTO `config` (`name`,`value`) VALUES ('NUMBER_READ_CHARS','32000');
-- INSERT INTO `config` (`name`,`value`) VALUES ('EIRC_ORGANIZATION_ID','');
INSERT INTO `config` (`name`,`value`) VALUES ('EIRC_ORGANIZATION_ID','-1');
INSERT INTO `config` (`name`,`value`) VALUES ('MB_ORGANIZATION_ID','-1');
INSERT INTO `config` (`name`,`value`) VALUES ('WORK_DIR','/var/tmp/data');
-- INSERT INTO `config` (`name`,`value`) VALUES ('TMP_DIR','/tmp');
INSERT INTO `config` (`name`,`value`) VALUES ('EIRC_DATA_SOURCE','jdbc/eircResource');

INSERT INTO `sequence` (`sequence_name`, `sequence_value`) VALUES
('eirc_account',1), ('service_provider_account', 1), ('service_provider_account_string_value', 1), ('module_instance', 1), ('module_instance_string_value', 1);

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
-- Organization type
-- --------------------------------
INSERT INTO `organization_type`(`object_id`) VALUES (2), (3);
INSERT INTO `organization_type_string_value`(`id`, `locale_id`, `value`) VALUES (2, 1, UPPER('Поставщик услуг')), (2, 2,UPPER('Постачальник послуг'));
INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`) VALUES (1,2,2300,2,2300);
INSERT INTO `organization_type_string_value`(`id`, `locale_id`, `value`) VALUES (3, 1, UPPER('Сборщик платежей')), (3, 2,UPPER('Збирач платежів'));
INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`) VALUES (2,3,2300,3,2300);

-- --------------------------------
-- Organization
-- --------------------------------
-- Itself organization
INSERT INTO `organization`(`object_id`) VALUES (0);
INSERT INTO `organization_string_value` (`id`, `locale_id`, `value`) VALUES
  (1, 1, UPPER('Модуль ЕИРЦ №1')), (1, 2,UPPER('Модуль ЕIРЦ №1'));
INSERT INTO `organization_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`) VALUES
  (1,0,900,1,900);

-- Reference to jdbc data source. It is calculation center only attribute. --
INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (914, 1, UPPER('КПП')), (914, 2, UPPER('КПП'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`) VALUES (913, 900, 1, 914, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (913, 913, UPPER('string'));

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (915, 1, UPPER('ИНН')), (915, 2, UPPER('ІПН'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`) VALUES (914, 900, 1, 915, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (914, 914, UPPER('string'));

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (916, 1, UPPER('Примечание')), (916, 2, UPPER('Примітка'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`) VALUES (915, 900, 0, 916, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (915, 915, UPPER('string'));

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (917, 1, UPPER('Юридический адрес')), (917, 2, UPPER('Юридична адреса'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`) VALUES (916, 900, 1, 917, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (916, 916, UPPER('string'));

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (918, 1, UPPER('Почтовый адрес')), (918, 2, UPPER('Поштова адреса'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`) VALUES (917, 900, 1, 918, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (917, 917, UPPER('string'));

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (919, 1, UPPER('E-mail')), (919, 2, UPPER('E-mail'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`) VALUES (918, 900, 1, 919, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (918, 918, UPPER('string'));

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (921, 1, UPPER('Допустимые услуги')), (921, 2, UPPER('Допустимi послуги'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`) VALUES (919, 900, 0, 921, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (919, 919, 'service');

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (403, 1, UPPER('Префикс л/с ЕИРЦ')), (403, 2, UPPER('Префікс л/р ЄIРЦ'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`) VALUES (402, 400, 0, 403, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (402, 402, UPPER('string'));

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (6000, 1, 'Л/c ПУ'), (6000, 2, 'Л/п ПП');
INSERT INTO `entity`(`id`, `entity`, `name_id`, `strategy_factory`) VALUES (6000, 'service_provider_account', 6000, '');

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (6002, 1, UPPER('Кол-во проживающих')), (6002, 2, UPPER('Кількість проживаючих'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`) VALUES (6001, 6000, 0, 6002, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (6001, 6001, UPPER('string'));

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (6003, 1, UPPER('Площадь общая')), (6003, 2, UPPER('Площа загальна'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`) VALUES (6002, 6000, 0, 6003, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (6002, 6002, UPPER('string'));

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (6004, 1, UPPER('Площадь жилая')), (6004, 2, UPPER('Площа житлова'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`) VALUES (6003, 6000, 0, 6004, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (6003, 6003, UPPER('string'));

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (6005, 1, UPPER('Площадь отапливаемая')), (6005, 2, UPPER('Площа опалювальна'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`) VALUES (6004, 6000, 0, 6005, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (6004, 6004, UPPER('string'));

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (6006, 1, UPPER('ФИО основного квартиросъемщика')), (6006, 2, UPPER('ПIБ основного квартиронаймача'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`) VALUES (6005, 6000, 0, 6006, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (6005, 6005, UPPER('string'));

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (6007, 1, UPPER('Л/с на закрытие')), (6007, 2, UPPER('Л/п на закриття'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`) VALUES (6006, 6000, 0, 6007, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (6006, 6006, UPPER('other'));

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (1010, 1, 'Модуль'), (1010, 2, 'Модуль');
INSERT INTO `entity`(`id`, `entity`, `name_id`, `strategy_factory`) VALUES (1010, 'module_instance', 1010, '');

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (1110, 1, 'Тип модуля'), (1110, 2, 'Тип модуля');
INSERT INTO `entity`(`id`, `entity`, `name_id`, `strategy_factory`) VALUES (1110, 'module_instance_type', 1110, '');
INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (1111, 1, UPPER('Тип модуля')), (1111, 2, UPPER('Тип модуля'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`) VALUES (1110, 1110, 1, 1111, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (1110, 1110, UPPER('string_value'));

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (1011, 1, UPPER('Название')), (1011, 2, UPPER('Назва'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`) VALUES (1010, 1010, 0, 1011, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (1010, 1010, UPPER('string'));

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (1012, 1, UPPER('Секретный ключ')), (1012, 2, UPPER('Секретний ключ'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`) VALUES (1011, 1010, 0, 1012, 0);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (1011, 1011, UPPER('string'));

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (1013, 1, UPPER('Идентификатор')), (1013, 2, UPPER('Ідентифікатор'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`) VALUES (1012, 1010, 1, 1013, 0);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (1012, 1012, UPPER('string'));

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (1014, 1, UPPER('Организация')), (1014, 2, UPPER('Организація'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`) VALUES (1013, 1010, 1, 1014, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (1013, 1013, UPPER('string'));

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (1015, 1, UPPER('Тип модуля')),(1015, 2, UPPER('Тип модуля'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`) VALUES (1014, 1010, 1, 1015, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (1014, 1014, 'module_instance_type');

-- ----------------------------------
-- Module instance type
-- ----------------------------------
INSERT INTO `module_instance_type`(`object_id`) VALUES (1), (2);
INSERT INTO `module_instance_type_string_value`(`id`, `locale_id`, `value`) VALUES (1, 1, UPPER('Модуль ЕИРЦ')), (1, 2,UPPER('Модуль ЕIРЦ'));
INSERT INTO `module_instance_type_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`) VALUES (1,1,1110,1,1110);
INSERT INTO `module_instance_type_string_value`(`id`, `locale_id`, `value`) VALUES (2, 1, UPPER('Модуль платежей')), (2, 2,UPPER('Модуль платежів'));
INSERT INTO `module_instance_type_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`) VALUES (2,2,1110,2,1110);

INSERT INTO `registry_status` (`code`, `name`) values
    (0, 'Загружается'),
    (1, 'Загружается с ошибкой'),
    (2, 'Загружен'),
    (3, 'Загрузка отменена'),
    (4, 'Загружен с ошибками'),
    (5, 'Обрабатывается'),
    (6, 'Обрабатывается с ошибками'),
    (7, 'Обработан'),
    (8, 'Обработан с ошибками'),
    (9, 'Обработка отменена'),
    (10, 'Откатывается'),
    (11, 'Откачен'),
    (12, 'Частично обработан'),
    (19, 'Связывается'),
    (20, 'Связывается с ошибками'),
    (21, 'Связан'),
    (22, 'Ошибка связывания'),
    (23, 'Связывание отменено')
;

INSERT INTO `registry_type` (`code`, `name`) values
    (0, 'Неизвестный'),
    (1, 'Сальдо'),
    (2, 'Начисление'),
    (3, 'Извещение'),
    (4, 'Счета на закрытие'),
    (5, 'Информационный'),
    (6, 'Корректировки'),
    (7, 'Наличные оплаты'),
    (8, 'Безналичные оплаты'),
    (9, 'Возвраты платежей'),
    (10, 'Ошибки'),
    (11, 'Квитанции'),
    (12, 'Оплаты банка')
;

INSERT INTO `registry_record_status` (`code`, `name`) values
    (1, 'Неверный формат данных'),
    (2, 'Загружена'),
    (3, 'Ошибка связывания'),
    (4, 'Связана'),
    (5, 'Ошибка обработки'),
    (6, 'Обработана')
;

INSERT INTO `import_error_type` (`code`, `name`) values
    (1, 'Не найден нас.пункт'),
    (2, 'Не найден тип улицы'),
    (3, 'Не найдена улица'),
    (4, 'Не найдена улица и дом'),
    (5, 'Не найден дом'),
    (6, 'Не найдена квартира'),
    (7, 'Соответствие для нас.пункта не может быть установлено'),
    (8, 'Соответствие для типа улицы не может быть установлено'),
    (9, 'Соответствие для улицы не может быть установлено'),
    (10, 'Соответствие для дома не может быть установлено'),
    (11, 'Соответствие для квартиры не может быть установлено'),
    (12, 'Более одного соответствия для нас.пункта'),
    (13, 'Более одного соответствие для типа улицы'),
    (14, 'Более одного соответствие для улицы'),
    (15, 'Более одного соответствие для дома'),
    (16, 'Более одного соответствие для квартиры'),
    (17, 'Несоответствие л/с'),
    (18, 'Более одного л/с'),
    (19, 'Не найдена комната'),
    (20, 'Соответствие для комнаты не может быть установлено'),
    (21, 'Более одного соответствие для комнаты')
;

INSERT INTO `container_type` (`code`, `name`) values
    (0,''),
    (1, 'Открытие лицевого счета'),
    (2, 'Закрытие лицевого счета'),
    (3, 'ФИО основного квартиросъемщика'),
    (4, 'Кол-во проживающих'),
    (5, 'Площадь общая'),
    (6, 'Площадь жилая'),
    (7, 'Площадь отапливаемая'),
    (8, 'Тип льготы'),
    (9, 'ФИО носителя льготы'),
    (10, 'ИНН носителя льготы'),
    (11, 'Документ подтверждающий право на льготу'),
    (12, 'Кол-во пользующихся льготой'),
    (13, 'Изменение номера лицевого счета'),
    (14, 'Добавление подуслуги на лицевой счет'),
    (15, 'Номер лицевого счета сторонней организации'),

    (50, 'Наличная оплата'),
    (51, 'Безналичная оплата'),
    (52, 'Оплата банка'),

    (100,'Базовый'),
    (101, 'Начисление опер.месяца'),
    (102, 'Исх.сальдо опер.месяца '),

    (150,'Принятие здания на обслуживание'),
    (151,'Снятие здания с обслуживания'),


    (500,'Идентификатор ППП'),
    (501,'Аннотация к реестру'),
    (502,'Синхронизация идентификаторов'),
    (503,'Номер экземпляра приложения'),
    (504,'Реквизиты платежного документа'),

    (600,'Кол-во проживающих'),
    (601,'Кол-во зарегистрированных'),
    (602,'Общая площадь (приведенная)'),
    (603,'Жилая площадь'),
    (604,'Отапливаемая площадь')
;


-- Current database version
 INSERT INTO `update` (`version`) VALUE ('eirc_20140831_0.0.9');