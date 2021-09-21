CREATE TABLE "request_file_group" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор группы файлов
  "group_type" INTEGER, -- Тип группы
  "created" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Дата создания
  "status" INTEGER -- Статус
); -- Группа файлов

CREATE TABLE "request_file" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор файла запроса
  "group_id" BIGINT, -- Идентификатор группы
  "loaded" TIMESTAMP NOT NULL, -- Дата загрузки
  "name" VARCHAR NOT NULL, -- Имя файла
  "directory" VARCHAR, -- Директория файла
  "type" INTEGER, -- Тип файла
  "sub_type" INTEGER, -- Подтип файла
  "begin_date" DATE NOT NULL, -- Дата начала
  "end_date" DATE NULL, -- Дата окончания
  "user_organization_id" BIGINT NOT NULL, -- Идентификатор организации пользователя
  "organization_id" BIGINT NOT NULL, -- Идентификатор организации
  "user_id" BIGINT REFERENCES "user", -- Идентификатор пользователя
  "dbf_record_count" BIGINT NOT NULL DEFAULT 0, -- Количество записей в исходном файле
  "length" BIGINT, -- Размер файла
  "check_sum" VARCHAR, -- Контрольная сумма
  "status" INTEGER, -- Статус
  UNIQUE ("name", "organization_id", "user_organization_id", "begin_date", "end_date"),
  CHECK (check_reference('organization', "organization_id")),
  CHECK (check_reference('organization', "user_organization_id"))
); -- Файл запросов

CREATE INDEX "request_file_group_id_index" ON "request_file" ("group_id");
CREATE INDEX "request_file_loaded_index" ON "request_file" ("loaded");
CREATE INDEX "request_file_name_index" ON "request_file" ("name");
CREATE INDEX "request_file_type_index" ON "request_file" ("type");
CREATE INDEX "request_file_sub_type_index" ON "request_file" ("sub_type");
CREATE INDEX "request_file_organization_id_index" ON "request_file" ("organization_id");
CREATE INDEX "request_file_user_organization_id_index" ON "request_file" ("user_organization_id");
CREATE INDEX "request_file_begin_date_index" ON "request_file" ("begin_date");
CREATE INDEX "request_file_end_date_index" ON "request_file" ("end_date");

CREATE TABLE "status_description" (
  "id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "code" INTEGER NOT NULL, -- Код описания статуса
  "name" VARCHAR NOT NULL, -- Описание статуса
  UNIQUE ("code")
); -- Описание статутов

CREATE TABLE "type_description" (
  "id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "code" INTEGER NOT NULL, -- Код описания типа
  "name" VARCHAR NOT NULL, -- Описание типа
  UNIQUE ("code")
); -- Описание типов

CREATE TABLE "request_warning" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор предупреждения
  "request_id" BIGINT NOT NULL REFERENCES "request_file" ON DELETE CASCADE, -- Идентификатор файла запроса
  "request_file_type" INTEGER NOT NULL, -- Типа файла запроса
  "status" BIGINT NOT NULL -- Код статуса
); -- Расширенные сообщения предупреждений обработки файлов запросов

CREATE TABLE "request_warning_parameter" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор параметра предупреждений
  "request_warning_id" BIGINT NOT NULL REFERENCES "request_warning" ON DELETE CASCADE, -- Идентификатор предупреждения
  "order" INTEGER NOT NULL, -- Порядок
  "type" VARCHAR NULL, -- Тип
  "value" VARCHAR NOT NULL, -- Значение
  UNIQUE ("request_warning_id", "order")
); -- Параметры предупреждений

CREATE TABLE "request_file_description" (
  "id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "request_file_type" VARCHAR NOT NULL, -- Тип файла запроса
  "date_pattern" VARCHAR NOT NULL -- Шаблон даты
); -- Описание структуры файлов запросов

CREATE TABLE "request_file_field_description" (
  "id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "request_file_description_id" BIGINT NOT NULL REFERENCES "request_file_description" ON DELETE CASCADE, -- Ссылка на описание структуры файла запроса
  "name" VARCHAR NOT NULL, -- Имя поля
  "type" VARCHAR NOT NULL, -- Java тип значений поля
  "length" INTEGER NOT NULL, -- Длина поля
  "scale" INTEGER -- Количество знаков после запятой, если тип поля дробный
); -- Описание структуры поля файла запросов

CREATE TABLE "request_file_history" (
  "id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "request_file_id" BIGINT NOT NULL REFERENCES "request_file" ON DELETE CASCADE, -- Идентификатор файла запроса
  "status" INTEGER NOT NULL, -- Статус файла запроса
  "date" TIMESTAMP NOT NULL -- Дата
); -- История файла запроса

CREATE TABLE "ownership" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта
  "parent_id" BIGINT, -- Не используется
  "parent_entity_id" BIGINT REFERENCES "entity", -- Не используется
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Дата начала периода действия параметров объекта
  "end_date" TIMESTAMP NULL DEFAULT NULL, -- Дата завершения периода действия параметров объекта
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус объекта: INACTIVE(0), ACTIVE(1), или ARCHIVE(2)
  "permission_id" BIGINT NOT NULL DEFAULT 0, -- Ключ прав доступа к объекту
  "external_id" BIGINT, -- Внешний идентификатор импорта записи
  UNIQUE ("object_id","start_date"),
  UNIQUE ("external_id")
); -- Форма собственности

CREATE SEQUENCE "ownership_object_id_sequence";

CREATE INDEX "ownership_object_id_index" ON "ownership" ("object_id");
CREATE INDEX "ownership_start_date_index" ON "ownership" ("start_date");
CREATE INDEX "ownership_end_date_index" ON "ownership" ("end_date");
CREATE INDEX "ownership_status_index" ON "ownership" ("status");
CREATE INDEX "ownership_permission_id_index" ON "ownership" ("permission_id");

CREATE TABLE "ownership_attribute" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "attribute_id" BIGINT NOT NULL, -- Идентификатор атрибута
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта
  "entity_attribute_id" BIGINT NOT NULL, -- Идентификатор типа атрибута. Возможные значения: 1100 - НАЗВАНИЕ
  "value_id" BIGINT, -- Идентификатор значения
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Дата начала периода действия параметров атрибута
  "end_date" TIMESTAMP NULL DEFAULT NULL, -- Дата окончания периода действия параметров атрибута
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус атрибута: INACTIVE(0), ACTIVE(1), или ARCHIVE(2)
  UNIQUE ("attribute_id","object_id","entity_attribute_id", "start_date"),
  CHECK (check_reference('ownership', "object_id"))
); -- Атрибуты объекта формы собственности

CREATE INDEX "ownership_attribute_object_id_index" ON "ownership_attribute" ("object_id");
CREATE INDEX "ownership_attribute_entity_attribute_id_index" ON "ownership_attribute" ("entity_attribute_id");
CREATE INDEX "ownership_attribute_value_id_index" ON "ownership_attribute" ("value_id");
CREATE INDEX "ownership_attribute_start_date_index" ON "ownership_attribute" ("start_date");
CREATE INDEX "ownership_attribute_end_date_index" ON "ownership_attribute" ("end_date");
CREATE INDEX "ownership_attribute_status_index" ON "ownership_attribute" ("status");

CREATE TABLE "ownership_string_value" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "id" BIGINT NOT NULL, -- Идентификатор значения
  "locale_id" BIGINT NOT NULL, -- Идентификатор локали
  "value" VARCHAR, -- Текстовое значение
  UNIQUE ("id","locale_id")
); -- Локализация атрибутов формы собственности

CREATE SEQUENCE "ownership_string_value_id_sequence";

CREATE INDEX "ownership_string_value_value_index" ON "ownership_string_value" ("id", "value");

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (1100, 1, 'Форма собственности'), (1100, 2, 'Форма власності');
INSERT INTO "entity"("id", "entity", "name_id") VALUES (1100, 'ownership', 1100);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (1101, 1, UPPER('Название')), (1101, 2, UPPER('Назва'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (1100, 1100, true, 1101, true, 0);

CREATE TABLE "ownership_correction" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор коррекции
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта формы собственности
  "external_id" VARCHAR, -- Код организации
  "correction" VARCHAR NOT NULL, -- Название формы собственности
  "start_date" TIMESTAMP NOT NULL DEFAULT NOW(), -- Дата начала актуальности соответствия
  "end_date" TIMESTAMP,  -- Дата окончания актуальности соответствия
  "organization_id" BIGINT NOT NULL, -- Идентификатор организации
  "user_organization_id" BIGINT,
  "module_id" BIGINT NOT NULL, -- Идентификатор внутренней организации
  "status" INTEGER, -- Статус
  UNIQUE (external_id, organization_id, user_organization_id),
  CHECK (check_reference('country', "object_id")),
  CHECK (check_reference('organization', "organization_id")),
  CHECK (check_reference('organization', "user_organization_id")),
  CHECK (check_reference('organization', "module_id"))
); -- Коррекция формы собственности

CREATE INDEX "ownership_correction_object_id_index" ON "ownership_correction" ("object_id");
CREATE INDEX "ownership_correction_correction_index" ON "ownership_correction" ("correction");
CREATE INDEX "ownership_correction_start_date_index" ON "ownership_correction" ("start_date");
CREATE INDEX "ownership_correction_end_date_index" ON "ownership_correction" ("end_date");
CREATE INDEX "ownership_correction_organization_id_index" ON "ownership_correction" ("organization_id");
CREATE INDEX "ownership_correction_user_organization_id_index" ON "ownership_correction" ("user_organization_id");

CREATE TABLE "privilege" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта
  "parent_id" BIGINT, -- Не используется
  "parent_entity_id" BIGINT, -- Не используется
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Дата начала периода действия параметров объекта
  "end_date" TIMESTAMP NULL DEFAULT NULL, -- Дата окончания периода действия параметров объекта
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус объекта: INACTIVE(0), ACTIVE(1), или ARCHIVE(2)
  "permission_id" BIGINT NOT NULL DEFAULT 0, -- Ключ прав доступа к объекту
  "external_id" BIGINT, -- Внешний идентификатор импорта записи
  UNIQUE ("object_id","start_date"),
  UNIQUE ("external_id")
); -- Привилегия

CREATE SEQUENCE "privilege_object_id_sequence";

CREATE INDEX "privilege_object_id_index" ON "privilege" ("object_id");
CREATE INDEX "privilege_start_date_index" ON "privilege" ("start_date");
CREATE INDEX "privilege_end_date_index" ON "privilege" ("end_date");
CREATE INDEX "privilege_status_index" ON "privilege" ("status");
CREATE INDEX "privilege_permission_id_index" ON "privilege" ("permission_id");

CREATE TABLE "privilege_attribute" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "attribute_id" BIGINT NOT NULL, -- Идентификатор атрибута
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта
  "entity_attribute_id" BIGINT NOT NULL, -- Идентификатор типа атрибута. Возможные значения: 1200 - НАЗВАНИЕ, 1201 - КОД
  "value_id" BIGINT, -- Идентификатор значения
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Дата начала периода действия значений атрибута
  "end_date" TIMESTAMP NULL DEFAULT NULL, -- Дата завершения периода действия значений атрибута
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус атрибута: INACTIVE(0), ACTIVE(1), или ARCHIVE(2)
  UNIQUE ("attribute_id","object_id","entity_attribute_id", "start_date"),
  CHECK (check_reference('country', "object_id"))
); -- Атрибуты объекта привилегии

CREATE INDEX "privilege_attribute_object_id_index" ON "privilege_attribute" ("object_id");
CREATE INDEX "privilege_attribute_entity_attribute_id_index" ON "privilege_attribute" ("entity_attribute_id");
CREATE INDEX "privilege_attribute_value_id_index" ON "privilege_attribute" ("value_id");
CREATE INDEX "privilege_attribute_start_date_index" ON "privilege_attribute" ("start_date");
CREATE INDEX "privilege_attribute_end_date_index" ON "privilege_attribute" ("end_date");
CREATE INDEX "privilege_attribute_status_index" ON "privilege_attribute" ("status");

CREATE TABLE "privilege_string_value" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "id" BIGINT NOT NULL, -- Идентификатор значения
  "locale_id" BIGINT NOT NULL REFERENCES "locale", -- Идентификатор локали
  "value" VARCHAR, -- Текстовое значение
  UNIQUE ("id","locale_id")
); -- Локализация атрибутов привилегий

CREATE SEQUENCE "privilege_string_value_id_sequence";

CREATE INDEX "privilege_string_value_value_index" ON "privilege_string_value" ("id", "value");

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (1200, 1, 'Льгота'), (1200, 2, 'Привілей');
INSERT INTO "entity"("id", "entity", "name_id") VALUES (1200, 'privilege', 1200);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (1201, 1, UPPER('Название')), (1201, 2, UPPER('Назва'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (1200, 1200, true, 1201, true, 0);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (1202, 1, UPPER('Код')), (1202, 2, UPPER('Код'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (1201, 1200, true, 1202, true, 1);

CREATE TABLE "privilege_correction" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор коррекции
  "external_id" VARCHAR, -- Внешний идентификатор объекта
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта льготы
  "correction" VARCHAR NOT NULL, -- Название льготы
  "start_date" DATE NOT NULL DEFAULT NOW(), -- Дата начала актуальности соответствия
  "end_date" DATE, -- Дата окончания актуальности соответствия
  "organization_id" BIGINT NOT NULL, -- Идентификатор организации
  "user_organization_id" BIGINT,
  "module_id" BIGINT NOT NULL, -- Идентификатор модуля
  "status" INTEGER, -- Статус
  CHECK (check_reference('privilege', "object_id")),
  CHECK (check_reference('organization', "organization_id")),
  CHECK (check_reference('organization', "user_organization_id")),
  CHECK (check_reference('organization', "module_id"))
); -- Коррекция льгот

CREATE INDEX "privilege_correction_object_id_index" ON "privilege_correction" ("object_id");
CREATE INDEX "privilege_correction_correction_index" ON "privilege_correction" ("correction");
CREATE INDEX "privilege_correction_start_date_index" ON "privilege_correction" ("start_date");
CREATE INDEX "privilege_correction_end_date_index" ON "privilege_correction" ("end_date");
CREATE INDEX "privilege_correction_organization_id_index" ON "privilege_correction" ("organization_id");
CREATE INDEX "privilege_correction_user_organization_id_index" ON "privilege_correction" ("user_organization_id");
CREATE INDEX "privilege_correction_module_id_index" ON "privilege_correction" ("module_id");
CREATE INDEX "privilege_correction_status_index" ON "privilege_correction" ("status");

CREATE TABLE "privilege_prolongation" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор
  "request_file_id" BIGINT NULL REFERENCES "request_file" ON DELETE CASCADE, -- Идентификатор файла запросов
  "account_number" VARCHAR NULL, -- Номер счета
  "status" INTEGER NOT NULL DEFAULT 240, -- Код статуса
  "internal_city_id" BIGINT, -- Идентификатор населенного пункта
  "internal_street_id" BIGINT, -- Идентификатор улицы
  "internal_street_type_id" BIGINT, -- Идентификатор типа улицы
  "internal_building_id" BIGINT, -- Идентификатор дома
  "outgoing_city" VARCHAR, -- Название населенного пункта используемое центром начисления
  "outgoing_district" VARCHAR, -- Название района используемое центром начисления
  "outgoing_street" VARCHAR, -- Название улицы используемое центром начисления
  "outgoing_street_type" VARCHAR, -- Название типа улицы используемое центром начисления
  "outgoing_building_number" VARCHAR, -- Номер дома используемый центром начисления
  "outgoing_building_corp" VARCHAR, -- Корпус используемый центром начисления
  "outgoing_apartment" VARCHAR, -- Номер квартиры
  "date" DATE NOT NULL, -- Дата
  "first_name" VARCHAR, -- Имя
  "last_name" VARCHAR, -- Фамилия
  "middle_name" VARCHAR, -- Отчество
  "city" VARCHAR NOT NULL, -- Населенный пункт
  "COD" INTEGER, -- Код ОСЗН
  "CDPR" BIGINT, -- ЕДРПОУ код предприятия
  "NCARD" BIGINT, -- Номер дела в ОСЗН
  "IDPIL" VARCHAR, -- Иден.код льготника
  "PASPPIL" VARCHAR, -- Паспорт льготника
  "FIOPIL" VARCHAR, -- ФИО льготника
  "INDEX" INTEGER, -- Почтовый индекс
  "CDUL" INTEGER, -- Код улицы
  "HOUSE" VARCHAR, -- Номер дома
  "BUILD" VARCHAR, -- Номер корпуса
  "APT" VARCHAR, -- Номер квартиры
  "KAT" INTEGER, -- Код льготы ЕДАРП
  "LGCODE" INTEGER, -- Код услуги
  "DATEIN" VARCHAR, -- Дата начала действия льготы
  "DATEOUT" VARCHAR, -- Дата окончания действия льготы
  "RAH" VARCHAR, -- Номер л/с ПУ
  "MONEY" INTEGER,
  "EBK" VARCHAR
); -- Файлы продления льгот

CREATE INDEX "privilege_prolongation_account_number_index" ON "privilege_prolongation" ("account_number");

CREATE TABLE "payment" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор начисления
  "request_file_id" BIGINT NOT NULL REFERENCES "request_file" ON DELETE CASCADE, -- Идентификатор файла
  "account_number" VARCHAR, -- Номер счета
  "internal_city_id" BIGINT, -- Идентификатор населенного пункта
  "internal_street_id" BIGINT, -- Идентификатор улицы
  "internal_street_type_id" BIGINT, -- Идентификатор типа улицы
  "internal_building_id" BIGINT, -- Идентификатор дома
  "internal_apartment_id" BIGINT, -- Идентификатор квартиры
  "outgoing_city" VARCHAR, -- Название населенного пункта используемое центром начисления
  "outgoing_district" VARCHAR, -- Название района используемое центром начисления
  "outgoing_street" VARCHAR, -- Название улицы используемое центром начисления
  "outgoing_street_type" VARCHAR, -- Название типа улицы используемое центром начисления
  "outgoing_building_number" VARCHAR, -- Номер дома используемый центром начисления
  "outgoing_building_corp" VARCHAR, -- Корпус используемый центром начисления
  "outgoing_apartment" VARCHAR, -- Номер квартиры
  "status" INTEGER NOT NULL DEFAULT 240, -- Статус
  "OWN_NUM" BIGINT, -- Номер дела
  "REE_NUM" INTEGER, -- Номер реестра
  "OPP" VARCHAR, -- Признаки наличия услуг
  "NUMB" INTEGER, -- Общее число зарегистрированных
  "MARK" INTEGER, -- К-во людей, которые пользуются льготами
  "CODE" INTEGER, -- Код ЖЭО
  "ENT_COD" BIGINT, -- Код ЖЭО ОКПО
  "FROG"  DECIMAL(5,1), -- Процент льгот
  "FL_PAY" DECIMAL(9,2), -- Общая плата
  "NM_PAY" DECIMAL(9,2), -- Плата в пределах норм потребления
  "DEBT" DECIMAL(9,2), -- Сумма долга
  "CODE2_1" INTEGER, -- Оплата жилья
  "CODE2_2" INTEGER, -- система
  "CODE2_3" INTEGER, -- Горячее водоснабжение
  "CODE2_4" INTEGER, -- Холодное водоснабжение
  "CODE2_5" INTEGER, -- Газоснабжение
  "CODE2_6" INTEGER, -- Электроэнергия
  "CODE2_7" INTEGER, -- Вывоз мусора
  "CODE2_8" INTEGER, -- Водоотведение
  "CODE2_17" INTEGER,
  "CODE2_18" INTEGER,
  "CODE2_19" INTEGER,
  "CODE2_20" INTEGER,
  "CODE2_21" INTEGER,
  "CODE2_22" INTEGER,
  "CODE2_23" INTEGER,
  "CODE2_24" INTEGER,
  "CODE2_25" INTEGER,
  "CODE2_26" INTEGER,
  "CODE2_50" INTEGER,
  "CODE2_60" INTEGER,
  "CODE2_70" INTEGER,
  "NORM_F_1" DECIMAL(10,4), -- Общая площадь (оплата жилья)
  "NORM_F_2" DECIMAL(10,4), -- Объемы потребления (отопление)
  "NORM_F_3" DECIMAL(10,4), -- Объемы потребления (горячего водо.)
  "NORM_F_4" DECIMAL(10,4), -- Объемы потребления (холодное водо.)
  "NORM_F_5" DECIMAL(10,4), -- Объемы потребления (газоснабжение)
  "NORM_F_6" DECIMAL(10,4), -- Объемы потребления (электроэнергия)
  "NORM_F_7" DECIMAL(10,4), -- Объемы потребления (вывоз мусора)
  "NORM_F_8" DECIMAL(10,4), -- Объемы потребления (водоотведение)
  "OWN_NUM_SR" VARCHAR, -- Лицевой счет в обслуж. организации
  "DAT1" DATE, -- Дата начала действия субсидии
  "DAT2" DATE, -- Дата формирования запроса
  "OZN_PRZ" INTEGER,
  "DAT_F_1" DATE, -- Дата начала для факта
  "DAT_F_2" DATE, -- Дата конца для факта
  "DAT_FOP_1" DATE, -- Дата начала для факта отопления
  "DAT_FOP_2" DATE, -- Дата конца для факта отопления
  "ID_RAJ" VARCHAR, -- Код района
  "SUR_NAM" VARCHAR, -- Фамилия
  "F_NAM" VARCHAR, -- Имя
  "M_NAM" VARCHAR, -- Отчество
  "IND_COD" VARCHAR, -- Идентификационный номер
  "INDX" VARCHAR, -- Индекс почтового отделения
  "N_NAME" VARCHAR, -- Название населенного пункта
  "VUL_NAME" VARCHAR, -- Название улицы
  "BLD_NUM" VARCHAR, -- Номер дома
  "CORP_NUM" VARCHAR, -- Номер корпуса
  "FLAT" VARCHAR, -- Номер квартиры
  "CODE3_1" INTEGER, -- Код тарифа оплаты жилья
  "CODE3_2" INTEGER, -- Код тарифа отопления
  "CODE3_3" INTEGER, -- Код тарифа горячего водоснабжения
  "CODE3_4" INTEGER, -- Код тарифа холодного водоснабжения
  "CODE3_5" INTEGER, -- Код тарифа - газоснабжение
  "CODE3_6" INTEGER, -- Код тарифа-электроэнергии
  "CODE3_7" INTEGER, -- Код тарифа - вывоз мусора
  "CODE3_8" INTEGER, -- Код тарифа - водоотведение
  "CODE3_17" INTEGER,
  "CODE3_18" INTEGER,
  "CODE3_19" INTEGER,
  "CODE3_20" INTEGER,
  "CODE3_21" INTEGER,
  "CODE3_22" INTEGER,
  "CODE3_23" INTEGER,
  "CODE3_24" INTEGER,
  "CODE3_25" INTEGER,
  "CODE3_26" INTEGER,
  "CODE3_50" INTEGER,
  "CODE3_60" INTEGER,
  "CODE3_70" INTEGER,
  "OPP_SERV" VARCHAR, -- Резерв
  "RESERV1" BIGINT, -- Резерв
  "RESERV2" VARCHAR, -- Резерв
  CHECK (check_reference('city', internal_city_id)),
  CHECK (check_reference('street', internal_street_id)),
  CHECK (check_reference('street_type', internal_street_type_id)),
  CHECK (check_reference('building', internal_building_id)),
  CHECK (check_reference('apartment', internal_apartment_id))
); -- Начисления

CREATE INDEX "payment_account_number_index" ON "payment" ("account_number");
CREATE INDEX "payment_status_index" ON "payment" ("status");
CREATE INDEX "payment_internal_city_id_index" ON "payment" ("internal_city_id");
CREATE INDEX "payment_internal_street_id_index" ON "payment" ("internal_street_id");
CREATE INDEX "payment_internal_street_type_id_index" ON "payment" ("internal_street_type_id");
CREATE INDEX "payment_internal_building_id_index" ON "payment" ("internal_building_id");
CREATE INDEX "payment_internal_apartment_id_index" ON "payment" ("internal_apartment_id");
CREATE INDEX "payment_F_NAM_index" ON "payment" ("F_NAM");
CREATE INDEX "payment_M_NAM_index" ON "payment" ("M_NAM");
CREATE INDEX "payment_SUR_NAM_index" ON "payment" ("SUR_NAM");
CREATE INDEX "payment_N_NAME_index" ON "payment" ("N_NAME");
CREATE INDEX "payment_VUL_NAME_index" ON "payment" ("VUL_NAME");
CREATE INDEX "payment_BLD_NUM_index" ON "payment" ("BLD_NUM");
CREATE INDEX "payment_FLAT_index" ON "payment" ("FLAT");
CREATE INDEX "payment_OWN_NUM_SR_index" ON "payment" ("OWN_NUM_SR");

CREATE TABLE "benefit" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор льготы
  "request_file_id" BIGINT NULL REFERENCES "request_file" ON DELETE CASCADE, -- Идентификатор файла запросов
  "account_number" VARCHAR NULL, -- Номер счета
  "status" INTEGER NOT NULL DEFAULT 240, -- Статус
  "OWN_NUM" BIGINT, -- Номер дела
  "REE_NUM" INTEGER, -- Номер реестра
  "OWN_NUM_SR" VARCHAR, -- Лицевой счет в обслуж. организации
  "FAM_NUM" INTEGER, -- Номер члена семьи
  "SUR_NAM" VARCHAR, -- Фамилия
  "F_NAM" VARCHAR, -- Имя
  "M_NAM" VARCHAR, -- Отчество
  "IND_COD" VARCHAR, -- Идентификационный номер
  "PSP_SER" VARCHAR, -- Серия паспорта
  "PSP_NUM" VARCHAR, -- Номер паспорта
  "OZN" INTEGER, -- Признак владельца
  "CM_AREA" DECIMAL(10,2), -- Общая площадь
  "HEAT_AREA" DECIMAL(10,2), -- Обогреваемая площадь
  "OWN_FRM" INTEGER, -- Форма собственности
  "HOSTEL" INTEGER, -- Количество комнат
  "PRIV_CAT" INTEGER, -- Категория льготы на платежи
  "ORD_FAM" INTEGER, -- Порядок семьи льготников для расчета платежей
  "OZN_SQ_ADD" INTEGER, -- Признак учета дополнительной площади
  "OZN_ABS" INTEGER, -- Признак отсутствия данных в базе ЖЭО
  "RESERV1" DECIMAL(10,2), -- Резерв
  "RESERV2" VARCHAR -- Резерв
); -- Льготы

CREATE INDEX "benefit_account_number_index" ON "benefit" ("account_number");
CREATE INDEX "benefit_status_index" ON "benefit" ("status");
CREATE INDEX "benefit_F_NAM_index" ON "benefit" ("F_NAM");
CREATE INDEX "benefit_M_NAM_index" ON "benefit" ("M_NAM");
CREATE INDEX "benefit_SUR_NAM_index" ON "benefit" ("SUR_NAM");
CREATE INDEX "benefit_OWN_NUM_SR_index" ON "benefit" ("OWN_NUM_SR");

CREATE TABLE "subsidy_tarif" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор тарифа
  "request_file_id" BIGINT NOT NULL REFERENCES "request_file" ON DELETE CASCADE, -- Идентификатор файла тарифов
  "status" INTEGER NULL, -- Статус
  "T11_DATA_T" DATE,
  "T11_DATA_E" DATE,
  "T11_DATA_R" DATE,
  "T11_MARK" INTEGER,
  "T11_TARN" INTEGER,
  "T11_CODE1" INTEGER,
  "T11_CODE2" INTEGER,
  "T11_COD_NA" VARCHAR,
  "T11_CODE3" INTEGER,
  "T11_NORM_U" DECIMAL(15, 4),
  "T11_NOR_US" DECIMAL(15, 4),
  "T11_CODE_N" INTEGER,
  "T11_COD_ND" INTEGER,
  "T11_CD_UNI" INTEGER,
  "T11_CS_UNI" DECIMAL (15, 4),
  "T11_NORM" DECIMAL (15, 4),
  "T11_NRM_DO" DECIMAL (15, 4),
  "T11_NRM_MA" DECIMAL (15, 4),
  "T11_K_NADL" DECIMAL (15, 4)
); -- Тарифы

CREATE TABLE "actual_payment" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор фактического начисления
  "request_file_id" BIGINT NOT NULL REFERENCES "request_file" ON DELETE CASCADE, -- Идентификатор файла запросов
  "account_number" VARCHAR, -- Номер счета
  "internal_city_id" BIGINT, -- Идентификатор населенного пункта
  "internal_street_id" BIGINT, -- Идентификатор улицы
  "internal_street_type_id" BIGINT, -- Идентификатор типа улицы
  "internal_building_id" BIGINT, -- Идентификатор дома
  "outgoing_city" VARCHAR, -- Название населенного пункта используемое центром начисления
  "outgoing_district" VARCHAR, -- Название района используемое центром начисления
  "outgoing_street" VARCHAR, -- Название улицы используемое центром начисления
  "outgoing_street_type" VARCHAR, -- Название типа улицы используемое центром начисления
  "outgoing_building_number" VARCHAR, -- Номер дома используемый центром начисления
  "outgoing_building_corp" VARCHAR, -- Корпус используемый центром начисления
  "outgoing_apartment" VARCHAR, -- Номер квартиры
  "status" INTEGER NOT NULL DEFAULT 240, -- Статус
  "SUR_NAM" VARCHAR, -- Фамилия
  "F_NAM" VARCHAR, -- Имя
  "M_NAM" VARCHAR, -- Отчество
  "INDX" VARCHAR, -- Индекс почтового отделения
  "N_NAME" VARCHAR, -- Название населенного пункта
  "N_CODE" VARCHAR, --
  "VUL_CAT" VARCHAR, -- Тип улицы
  "VUL_NAME" VARCHAR, -- Название улицы
  "VUL_CODE" VARCHAR, -- Код улицы
  "BLD_NUM" VARCHAR, -- Номер дома
  "CORP_NUM" VARCHAR, -- Номер корпуса
  "FLAT" VARCHAR, -- Номер квартиры
  "OWN_NUM" VARCHAR (15), -- Номер дела
  "APP_NUM" VARCHAR,
  "DAT_BEG" DATE,
  "DAT_END" DATE,
  "CM_AREA" DECIMAL(7,2),
  "NM_AREA" DECIMAL(7,2),
  "BLC_AREA" DECIMAL(5,2),
  "FROG" DECIMAL(5,1),
  "DEBT" DECIMAL(10,2),
  "NUMB" INTEGER,
  "P1" DECIMAL(10,4),
  "N1" DECIMAL(10,4),
  "P2" DECIMAL(10,4),
  "N2" DECIMAL(10,4),
  "P3" DECIMAL(10,4),
  "N3" DECIMAL(10,4),
  "P4" DECIMAL(10,4),
  "N4" DECIMAL(10,4),
  "P5" DECIMAL(10,4),
  "N5" DECIMAL(10,4),
  "P6" DECIMAL(10,4),
  "N6" DECIMAL(10,4),
  "P7" DECIMAL(10,4),
  "N7" DECIMAL(10,4),
  "P8" DECIMAL(10,4),
  "N8" DECIMAL(10,4),
  CHECK (check_reference('city',"internal_city_id")),
  CHECK (check_reference('street',"internal_street_id")),
  CHECK (check_reference('street_type',"internal_street_type_id")),
  CHECK (check_reference('building',"internal_building_id"))
); -- Фактические начисления

CREATE INDEX "actual_payment_account_number_index" ON "actual_payment" ("account_number");
CREATE INDEX "actual_payment_status_index" ON "actual_payment" ("status");
CREATE INDEX "actual_payment_internal_city_id_index" ON "actual_payment" ("internal_city_id");
CREATE INDEX "actual_payment_internal_street_id_index" ON "actual_payment" ("internal_street_id");
CREATE INDEX "actual_payment_internal_street_type_id_index" ON "actual_payment" ("internal_street_type_id");
CREATE INDEX "actual_payment_internal_building_id_index" ON "actual_payment" ("internal_building_id");
CREATE INDEX "actual_payment_F_NAM_index" ON "actual_payment" ("F_NAM");
CREATE INDEX "actual_payment_M_NAM_index" ON "actual_payment" ("M_NAM");
CREATE INDEX "actual_payment_SUR_NAM_index" ON "actual_payment" ("SUR_NAM");
CREATE INDEX "actual_payment_N_NAME_index" ON "actual_payment" ("N_NAME");
CREATE INDEX "actual_payment_VUL_NAME_index" ON "actual_payment" ("VUL_NAME");
CREATE INDEX "actual_payment_BLD_NUM_index" ON "actual_payment" ("BLD_NUM");
CREATE INDEX "actual_payment_FLAT_index" ON "actual_payment" ("FLAT");

CREATE TABLE "subsidy" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор субсидии
  "request_file_id" BIGINT NOT NULL REFERENCES "request_file" ON DELETE CASCADE, -- Идентификатор файла запросов
  "account_number" VARCHAR, -- Номер счета
  "internal_city_id" BIGINT, -- Идентификатор населенного пункта
  "internal_street_id" BIGINT, -- Идентификатор улицы
  "internal_street_type_id" BIGINT, -- Идентификатор типа улицы
  "internal_building_id" BIGINT, -- Идентификатор дома
  "outgoing_city" VARCHAR, -- Название населенного пункта используемое центром начисления
  "outgoing_district" VARCHAR, -- Название района используемое центром начисления
  "outgoing_street" VARCHAR, -- Название улицы используемое центром начисления
  "outgoing_street_type" VARCHAR, -- Название типа улицы используемое центром начисления
  "outgoing_building_number" VARCHAR, -- Номер дома используемый центром начисления
  "outgoing_building_corp" VARCHAR, -- Корпус используемый центром начисления
  "outgoing_apartment" VARCHAR, -- Номер квартиры
  "status" INTEGER NOT NULL DEFAULT 240, -- Статус
  "first_name" VARCHAR, -- Имя
  "last_name" VARCHAR, -- Фамилия
  "middle_name" VARCHAR, -- Отчество
  "FIO" VARCHAR, -- ФИО
  "ID_RAJ" VARCHAR, -- Код района
  "NP_CODE" VARCHAR, -- Код населенного пункта
  "NP_NAME"VARCHAR, -- Название населенного пункта
  "CAT_V" VARCHAR, -- Тип улицы
  "VULCOD" VARCHAR, -- Код улицы
  "NAME_V" VARCHAR, -- Название улицы
  "BLD" VARCHAR, -- Номер дома
  "CORP" VARCHAR, -- Номер корпуса
  "FLAT" VARCHAR, -- Номер квартиры
  "RASH" VARCHAR, -- Номер л/с ПУ
  "NUMB" VARCHAR,
  "DAT1" DATE, -- Дата начала периода, на который предоставляется субсидия
  "DAT2" DATE, -- Дата конца периода, на который предоставляется субсидия
  "NM_PAY" DECIMAL(9,2), -- Начисление в пределах нормы
  "P1" DECIMAL(9,4),
  "P2" DECIMAL(9,4),
  "P3" DECIMAL(9,4),
  "P4" DECIMAL(9,4),
  "P5" DECIMAL(9,4),
  "P6" DECIMAL(9,4),
  "P7" DECIMAL(9,4),
  "P8" DECIMAL(9,4),
  "SM1" DECIMAL(9,2),
  "SM2" DECIMAL(9,2),
  "SM3" DECIMAL(9,2),
  "SM4" DECIMAL(9,2),
  "SM5" DECIMAL(9,2),
  "SM6" DECIMAL(9,2),
  "SM7" DECIMAL(9,2),
  "SM8" DECIMAL(9,2),
  "SB1" DECIMAL(9,2),
  "SB2" DECIMAL(9,2),
  "SB3" DECIMAL(9,2),
  "SB4" DECIMAL(9,2),
  "SB5" DECIMAL(9,2),
  "SB6" DECIMAL(9,2),
  "SB7" DECIMAL(9,2),
  "SB8" DECIMAL(9,2),
  "OB1" DECIMAL(9,2),
  "OB2" DECIMAL(9,2),
  "OB3" DECIMAL(9,2),
  "OB4" DECIMAL(9,2),
  "OB5" DECIMAL(9,2),
  "OB6" DECIMAL(9,2),
  "OB7" DECIMAL(9,2),
  "OB8" DECIMAL(9,2),
  "SUMMA" DECIMAL(13,2),
  "NUMM" INTEGER,
  "SUBS" DECIMAL(13,2),
  "KVT" INTEGER,
  "MON" INTEGER,
  "EBK" VARCHAR,
  CHECK (check_reference('city',"internal_city_id")),
  CHECK (check_reference('street',"internal_street_id")),
  CHECK (check_reference('street_type',"internal_street_type_id")),
  CHECK (check_reference('building',"internal_building_id"))
); -- Файлы субсидий

CREATE INDEX "subsidy_account_number_index" ON "subsidy" ("account_number");
CREATE INDEX "subsidy_status_index" ON "subsidy" ("status");
CREATE INDEX "subsidy_internal_city_id_index" ON "subsidy" ("internal_city_id");
CREATE INDEX "subsidy_internal_street_id_index" ON "subsidy" ("internal_street_id");
CREATE INDEX "subsidy_internal_street_type_id_index" ON "subsidy" ("internal_street_type_id");
CREATE INDEX "subsidy_internal_building_id_index" ON "subsidy" ("internal_building_id");
CREATE INDEX "subsidy_FIO_index" ON "subsidy" ("FIO");
CREATE INDEX "subsidy_NP_NAME_index" ON "subsidy" ("NP_NAME");
CREATE INDEX "subsidy_NAME_V_index" ON "subsidy" ("NAME_V");
CREATE INDEX "subsidy_BLD_index" ON "subsidy" ("BLD");
CREATE INDEX "subsidy_CORP_index" ON "subsidy" ("CORP");
CREATE INDEX "subsidy_FLAT_index" ON "subsidy" ("FLAT");

CREATE TABLE "subsidy_split" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор разбивки субсидии помесячно
  "subsidy_id" BIGINT NOT NULL REFERENCES "subsidy" ON DELETE CASCADE, -- Идентификатор субсидии
  "DAT1" DATE, -- Дата начала периода
  "DAT2" DATE, -- Дата конца периода
  "SM1" DECIMAL(9,2),
  "SM2" DECIMAL(9,2),
  "SM3" DECIMAL(9,2),
  "SM4" DECIMAL(9,2),
  "SM5" DECIMAL(9,2),
  "SM6" DECIMAL(9,2),
  "SM7" DECIMAL(9,2),
  "SM8" DECIMAL(9,2),
  "SUMMA" DECIMAL(13,2),
  "NUMM" INTEGER,
  "SUBS" DECIMAL(13,2)
); -- Разбивка субсидий помесячно

CREATE TABLE "dwelling_characteristics" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор объекта характеристик жилья
  "request_file_id" BIGINT NOT NULL REFERENCES "request_file" ON DELETE CASCADE, -- Идентификатор файла запросов
  "account_number" VARCHAR, -- Номер счета
  "internal_city_id" BIGINT, -- Идентификатор населенного пункта
  "internal_street_id" BIGINT, -- Идентификатор улицы
  "internal_street_type_id" BIGINT, -- Идентификатор типа улицы
  "internal_building_id" BIGINT, -- Идентификатор дома
  "outgoing_city" VARCHAR, -- Название населенного пункта используемое центром начисления
  "outgoing_district" VARCHAR, -- Название района используемое центром начисления
  "outgoing_street" VARCHAR, -- Название улицы используемое центром начисления
  "outgoing_street_type" VARCHAR, -- Название типа улицы используемое центром начисления
  "outgoing_building_number" VARCHAR, -- Номер дома используемый центром начисления
  "outgoing_building_corp" VARCHAR, -- Корпус используемый центром начисления
  "outgoing_apartment" VARCHAR, -- Номер квартиры
  "date" DATE NOT NULL, -- Дата
  "status" INTEGER NOT NULL DEFAULT 240, -- Статус
  "first_name" VARCHAR, -- Имя
  "last_name" VARCHAR, -- Фамилия
  "middle_name" VARCHAR, -- Отчество
  "city" VARCHAR NOT NULL, -- Населенный пункт
  "COD" INTEGER, -- Код района
  "CDPR" BIGINT, -- Код ЄДРПОУ (ОГРН) организации
  "NCARD" BIGINT, -- Идентификатор льготника
  "IDCODE" VARCHAR, -- ИНН собственника жилья/льготника (ставят ИНН льготника)
  "PASP" VARCHAR, -- Серия и номер паспорта собственника жилья/льготника (ставят паспорт льготника)
  "FIO" VARCHAR, -- ФИО собственника жилья/льготника (ставят ФИО льготника)
  "IDPIL" VARCHAR, -- ИНН льготника
  "PASPPIL" VARCHAR, -- Серия и номер паспорта льготника
  "FIOPIL" VARCHAR, -- ФИО льготника
  "INDEX" INTEGER, -- Почтовый индекс
  "CDUL" INTEGER, -- Код улицы
  "HOUSE" VARCHAR, -- Номер дома
  "BUILD" VARCHAR, -- Корпус
  "APT" VARCHAR, -- Номер квартиры
  "VL" INTEGER, -- Тип собственности
  "PLZAG" DECIMAL(8,2), -- Общая площадь помещения
  "PLOPAL" DECIMAL(8,2), -- Отапливаемая площадь помещения
  CHECK (check_reference('city',"internal_city_id")),
  CHECK (check_reference('street',"internal_street_id")),
  CHECK (check_reference('street_type',"internal_street_type_id")),
  CHECK (check_reference('building',"internal_building_id"))
); -- Файлы-запросы характеристик жилья

CREATE INDEX "dwelling_characteristics_account_number_index" ON "dwelling_characteristics" ("account_number");
CREATE INDEX "dwelling_characteristics_status_index" ON "dwelling_characteristics" ("status");
CREATE INDEX "dwelling_characteristics_internal_city_id_index" ON "dwelling_characteristics" ("internal_city_id");
CREATE INDEX "dwelling_characteristics_internal_street_id_index" ON "dwelling_characteristics" ("internal_street_id");
CREATE INDEX "dwelling_characteristics_internal_street_type_id_index" ON "dwelling_characteristics" ("internal_street_type_id");
CREATE INDEX "dwelling_characteristics_internal_building_id_index" ON "dwelling_characteristics" ("internal_building_id");
CREATE INDEX "dwelling_characteristics_FIO_index" ON "dwelling_characteristics" ("FIO");
CREATE INDEX "dwelling_characteristics_CDUL_index" ON "dwelling_characteristics" ("CDUL");
CREATE INDEX "dwelling_characteristics_HOUSE_index" ON "dwelling_characteristics" ("HOUSE");
CREATE INDEX "dwelling_characteristics_BUILD_index" ON "dwelling_characteristics" ("BUILD");
CREATE INDEX "dwelling_characteristics_APT_index" ON "dwelling_characteristics" ("APT");

CREATE TABLE "facility_service_type" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор объекта вид услуги
  "request_file_id" BIGINT NOT NULL REFERENCES "request_file" ON DELETE CASCADE, -- Идентификатор файла запросов
  "account_number" VARCHAR, -- Номер счета
  "internal_city_id" BIGINT, -- Идентификатор населенного пункта
  "internal_street_id" BIGINT, -- Идентификатор улицы
  "internal_street_type_id" BIGINT, -- Идентификатор типа улицы
  "internal_building_id" BIGINT, -- Идентификатор дома
  "outgoing_city" VARCHAR, -- Название населенного пункта используемое центром начисления
  "outgoing_district" VARCHAR, -- Название района используемое центром начисления
  "outgoing_street" VARCHAR, -- Название улицы используемое центром начисления
  "outgoing_street_type" VARCHAR, -- Название типа улицы используемое центром начисления
  "outgoing_building_number" VARCHAR, -- Номер дома используемый центром начисления
  "outgoing_building_corp" VARCHAR, -- Корпус используемый центром начисления
  "outgoing_apartment" VARCHAR, -- Номер квартиры
  "date" DATE NOT NULL, -- Дата
  "status" INTEGER NOT NULL DEFAULT 240, -- Статус
  "first_name" VARCHAR, -- Имя
  "last_name" VARCHAR, -- Фамилия
  "middle_name" VARCHAR, -- Отчество
  "city" VARCHAR NOT NULL, -- Населенный пункт
  "COD" INTEGER, -- Код района
  "CDPR" BIGINT, -- Код ЄДРПОУ (ОГРН) организации
  "NCARD" BIGINT, -- Идентификатор льготника
  "IDCODE" VARCHAR, -- ИНН собственника жилья/льготника (ставят ИНН льготника)
  "PASP" VARCHAR, -- Серия и номер паспорта собственника жилья/льготника (ставят паспорт льготника)
  "FIO" VARCHAR, -- ФИО собственника жилья/льготника (ставят ФИО льготника)
  "IDPIL" VARCHAR, -- ИНН льготника
  "PASPPIL" VARCHAR, -- Серия и номер паспорта льготника
  "FIOPIL" VARCHAR, -- ФИО льготника
  "INDEX" INTEGER, -- Почтовый индекс
  "CDUL" INTEGER, -- Код улицы
  "HOUSE" VARCHAR, -- Номер дома
  "BUILD" VARCHAR, -- Корпус
  "APT" VARCHAR, -- Номер квартиры
  "KAT" INTEGER, -- Категория льготы ЕДАРП
  "LGCODE" INTEGER, -- Код возмещения
  "YEARIN" INTEGER, -- Год начала действия льготы
  "MONTHIN" INTEGER, -- Месяц начала действия льготы
  "YEAROUT" INTEGER, -- Год окончания действия льготы
  "MONTHOUT" INTEGER, -- Месяц окончания действия льготы
  "RAH" VARCHAR, -- Номер л/с ПУ
  "RIZN" INTEGER, -- Тип услуги
  "TARIF" BIGINT, -- Код тарифа услуги
  CHECK (check_reference('city',"internal_city_id")),
  CHECK (check_reference('street',"internal_street_id")),
  CHECK (check_reference('street_type',"internal_street_type_id")),
  CHECK (check_reference('building',"internal_building_id"))
); -- Файлы-запросы видов услуг

CREATE INDEX "facility_service_type_account_number_index" ON "facility_service_type" ("account_number");
CREATE INDEX "facility_service_type_status_index" ON "facility_service_type" ("status");
CREATE INDEX "facility_service_type_internal_city_id_index" ON "facility_service_type" ("internal_city_id");
CREATE INDEX "facility_service_type_internal_street_id_index" ON "facility_service_type" ("internal_street_id");
CREATE INDEX "facility_service_type_internal_street_type_id_index" ON "facility_service_type" ("internal_street_type_id");
CREATE INDEX "facility_service_type_internal_building_id_index" ON "facility_service_type" ("internal_building_id");
CREATE INDEX "facility_service_type_FIO_index" ON "facility_service_type" ("FIO");
CREATE INDEX "facility_service_type_CDUL_index" ON "facility_service_type" ("CDUL");
CREATE INDEX "facility_service_type_HOUSE_index" ON "facility_service_type" ("HOUSE");
CREATE INDEX "facility_service_type_BUILD_index" ON "facility_service_type" ("BUILD");
CREATE INDEX "facility_service_type_APT_index" ON "facility_service_type" ("APT");

CREATE TABLE "facility_form2" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор объекта форма-2 льгота
  "request_file_id" BIGINT NOT NULL REFERENCES "request_file" ON DELETE CASCADE, -- Идентификатор файла запросов
  "account_number" VARCHAR, -- Номер счета
  "status" INTEGER NOT NULL DEFAULT 240, -- Статус
  "CDPR" BIGINT, -- Код ЄДРПОУ (ОГРН) организации
  "IDCODE" VARCHAR, -- ИНН льготника
  "FIO" VARCHAR, -- ФИО льготника
  "PPOS" VARCHAR, -- 0
  "RS" VARCHAR, -- Номер л/с ПУ
  "YEARIN" INTEGER, -- Год выгрузки данных
  "MONTHIN" INTEGER, -- Месяц выгрузки данных
  "LGCODE" INTEGER, -- Код льготы
  "DATA1" DATE, -- Дата начала периода
  "DATA2" DATE, -- Дата окончания периода
  "LGKOL" INTEGER, -- Кол-во пользующихся льготой
  "LGKAT" VARCHAR, -- Категория льготы ЕДАРП
  "LGPRC" INTEGER, -- Процент льготы
  "SUMM" DECIMAL(8,2), -- Сумма возмещения
  "FACT" DECIMAL(19,6), -- Объем фактического потребления (для услуг со счетчиком)
  "TARIF" DECIMAL(14,7), -- Ставка тарифа
  "FLAG" INTEGER,
  "DEPART" INTEGER -- Код участка
); -- Файлы форма-2 льгота

CREATE INDEX "facility_form2_account_number_index" ON "facility_form2" ("account_number");
CREATE INDEX "facility_form2_status_index" ON "facility_form2" ("status");

CREATE TABLE "facility_local" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор объекта местной льготы
  "request_file_id" BIGINT NOT NULL REFERENCES "request_file" ON DELETE CASCADE, -- Идентификатор файла запросов
  "account_number" VARCHAR, -- Номер счета
  "status" INTEGER NOT NULL DEFAULT 240, -- Статус
  "COD" INTEGER, -- Код района
  "CDPR" BIGINT, -- Код ОКПО балансодержателя
  "NCARD" BIGINT,
  "IDCODE" VARCHAR, -- ИНН ответственного по л/с
  "PASP" VARCHAR, -- Серия и номер паспорта ответственного
  "FIO" VARCHAR, -- ФИО ответственного
  "IDPIL" VARCHAR, -- ИНН носителя льготы
  "PASPPIL" VARCHAR, -- Серия и номер паспорта носителя льготы
  "FIOPIL" VARCHAR, -- ФИО носителя льготы
  "INDEX" INTEGER,
  "NAMUL" VARCHAR, -- Название и тип улицы
  "CDUL" INTEGER, -- Код улицы в СЗ
  "HOUSE" VARCHAR, -- Номер дома
  "BUILD" VARCHAR, -- Часть дома
  "APT" VARCHAR, -- Номер квартиры
  "LGCODE" INTEGER,
  "KAT" INTEGER, -- Код льготной категории
  "YEARIN" INTEGER, -- Год формирования
  "MONTHIN" INTEGER, -- Месяц формирования
  "YEAR" INTEGER, -- Расчетный год
  "MONTH" INTEGER, -- Расчетный месяц
  "RAH" BIGINT, -- Номер л/с
  "RIZN" INTEGER,
  "TARIF" BIGINT,
  "VL" INTEGER,
  "PLZAG" DECIMAL(6,2), -- Общая площадь
  "PLPIL" DECIMAL(6,2), -- Льготная площадь
  "TARIFS" DECIMAL(10,2),
  "TARIFN" DECIMAL(10,3), -- Тариф
  "SUM" DECIMAL(10,2), -- Возмещение текущего месяца
  "LGKOL" INTEGER, -- Количество пользующихся льготой
  "SUMPER" DECIMAL(10,2), -- Сумма перерасчетов возмещения
  "DATN" DATE, -- Начало расчетного периода (текущего)
  "DATK" DATE, -- Окончание расчетного периода (текущего)
  "PSN" INTEGER, -- Причина снятия льготы
  "SUMM" DECIMAL(10,2), -- Итоговая сумма (текущее возмещение и перерасчеты)
  "DEPART" INTEGER -- Код участка
); -- Записи местной льготы

CREATE INDEX "facility_local_account_number_index" ON "facility_local" ("account_number");
CREATE INDEX "facility_local_status_index" ON "facility_local" ("status");

CREATE TABLE "facility_street_type_reference" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор объекта тип улицы
  "request_file_id" BIGINT NOT NULL REFERENCES "request_file" ON DELETE CASCADE, -- Идентификатор файла типов улиц
  "status" INTEGER NULL, -- Код статуса
  "KLKUL_CODE" VARCHAR, -- Код типа улицы
  "KLKUL_NAME" VARCHAR -- Наименование типа улицы
); -- Файлы-справочники типов улиц

CREATE INDEX "facility_street_type_reference_status_index" ON "facility_street_type_reference" ("status");
CREATE INDEX "facility_street_type_reference_KLKUL_CODE_index" ON "facility_street_type_reference" ("KLKUL_CODE");
CREATE INDEX "facility_street_type_reference_KLKUL_NAME_index" ON "facility_street_type_reference" ("KLKUL_NAME");

CREATE TABLE "facility_street_reference" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор объекта улица
  "request_file_id" BIGINT NOT NULL REFERENCES "request_file" ON DELETE CASCADE, -- Идентификатор файла улиц
  "status" INTEGER NULL, -- Статус
  "KL_CODERN" VARCHAR, -- Код района
  "KL_CODEUL" VARCHAR, -- Код улицы
  "KL_NAME" VARCHAR, -- Наименование улицы
  "KL_CODEKUL" VARCHAR -- Код типа улицы
); -- Файлы-справочники улиц

CREATE INDEX "facility_street_reference_status_index" ON "facility_street_reference" ("status");
CREATE INDEX "facility_street_reference_KL_CODERN_index" ON "facility_street_reference" ("KL_CODERN");
CREATE INDEX "facility_street_reference_KL_CODEUL_index" ON "facility_street_reference" ("KL_CODEUL");
CREATE INDEX "facility_street_reference_KL_NAME_index" ON "facility_street_reference" ("KL_NAME");
CREATE INDEX "facility_street_reference_KL_CODEKUL_index" ON "facility_street_reference" ("KL_CODEKUL");

CREATE TABLE "facility_tarif_reference" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор объекта тариф
  "request_file_id" BIGINT NOT NULL REFERENCES "request_file" ON DELETE CASCADE, -- Идентификатор файла тарифов
  "status" INTEGER NULL, -- Статус
  "TAR_CODE" BIGINT, -- Код тарифа
  "TAR_CDPLG" BIGINT, -- Код услуги
  "TAR_SERV" BIGINT, -- Номер тарифа в услуге
  "TAR_DATEB" DATE, -- Дата начала действия тарифа
  "TAR_DATEE" DATE, -- Дата окончания действия тарифа
  "TAR_COEF" DECIMAL(11,2),
  "TAR_COST" DECIMAL(14,7), -- Ставка тарифа
  "TAR_UNIT" BIGINT,
  "TAR_METER" INTEGER,
  "TAR_NMBAS" DECIMAL(11,2),
  "TAR_NMSUP" DECIMAL(11,2),
  "TAR_NMUBS" DECIMAL(22,6),
  "TAR_NMUSP" DECIMAL(22,6),
  "TAR_NMUMX" DECIMAL(11,4),
  "TAR_TPNMB" BIGINT,
  "TAR_TPNMS" BIGINT,
  "TAR_NMUPL" INTEGER,
  "TAR_PRIV" BIGINT
); -- Файлы-справочники тарифов для запросов по льготам

CREATE INDEX "facility_tarif_reference_status_index" ON "facility_tarif_reference" ("status");

CREATE TABLE "person_account" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор счета абонента
  "first_name"VARCHAR NOT NULL, -- Имя
  "middle_name" VARCHAR NOT NULL, -- Отчество
  "last_name" VARCHAR NOT NULL, -- Фамилия
  "city" VARCHAR, -- Населенный пункт
  "street_type" VARCHAR, -- Тип улицы
  "street" VARCHAR, -- Улица
  "building_number" VARCHAR, -- Номер дома
  "building_corp" VARCHAR, -- Корпус
  "apartment" VARCHAR, -- Номер квартиры
  "city_object_id" BIGINT, -- Идентификатор населенного пункта
  "street_object_id" BIGINT, -- Идентификатор улицы
  "street_type_object_id" BIGINT, -- Идентификатор типа улицы
  "building_object_id" BIGINT, -- Идентификатор дома
  "apartment_object_id" BIGINT, -- Идентификатор квартиры
  "account_number" VARCHAR NOT NULL, -- Счет абонента
  "pu_account_number" VARCHAR NOT NULL, -- Личный счет в обслуживающей организации
  "organization_id" BIGINT NOT NULL, -- Идентификатор отдела соц. защиты населения
  "user_organization_id" BIGINT, -- Идентификатор организации пользователя
  "calc_center_id" BIGINT NOT NULL, -- Идентификатор центра начислений
  "created" TIMESTAMP DEFAULT current_timestamp, -- Время создания
  CHECK (check_reference('city', "city_object_id")),
  CHECK (check_reference('street', "street_object_id")),
  CHECK (check_reference('street_type', "street_type_object_id")),
  CHECK (check_reference('building', "building_object_id")),
  CHECK (check_reference('apartment', "apartment_object_id")),
  CHECK (check_reference('organization', "user_organization_id")),
  CHECK (check_reference('organization', "organization_id")),
  CHECK (check_reference('organization', "calc_center_id"))
); -- Соответствие лицевого счета

CREATE INDEX "person_account_city_object_id_index" ON "person_account" ("city_object_id");
CREATE INDEX "person_account_street_object_id_index" ON "person_account" ("street_object_id");
CREATE INDEX "person_account_street_type_object_id_index" ON "person_account" ("street_type_object_id");
CREATE INDEX "person_account_building_object_id_index" ON "person_account" ("building_object_id");
CREATE INDEX "person_account_apartment_object_id_index" ON "person_account" ("apartment_object_id");
CREATE INDEX "person_account_organization_id_index" ON "person_account" ("organization_id");
CREATE INDEX "person_account_calc_center_id_index" ON "person_account" ("calc_center_id");
CREATE INDEX "person_account_user_organization_id_index" ON "person_account" ("user_organization_id");
CREATE INDEX "person_account_first_name_index" ON "person_account" ("first_name");
CREATE INDEX "person_account_middle_name_index" ON "person_account" ("middle_name");
CREATE INDEX "person_account_last_name_index" ON "person_account" ("last_name");
CREATE INDEX "person_account_city_index" ON "person_account" ("city");
CREATE INDEX "person_account_street_type_index" ON "person_account" ("street_type");
CREATE INDEX "person_account_street_index" ON "person_account" ("street");
CREATE INDEX "person_account_building_number_index" ON "person_account" ("building_number");
CREATE INDEX "person_account_building_corp_index" ON "person_account" ("building_corp");
CREATE INDEX "person_account_apartment_index" ON "person_account" ("apartment");
CREATE INDEX "person_account_pu_account_number_index" ON "person_account" ("pu_account_number");

CREATE TABLE "oschadbank_request_file" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор файла запросов от ощадбанка
  "request_file_id" BIGINT NOT NULL REFERENCES "request_file" ON DELETE CASCADE, -- Идентификатор файла запросов
  "EDRPOU" VARCHAR, -- ЕДРПОУ
  "PROVIDER_NAME" VARCHAR, -- Название поставщика
  "DOCUMENT_NUMBER" VARCHAR, -- № Анкеты
  "SERVICE_NAME" VARCHAR, -- Название услуги
  "REPORTING_PERIOD" VARCHAR, -- Отчетный период
  "PROVIDER_CODE" VARCHAR, -- Код Банка Поставщика услуги
  "PROVIDER_ACCOUNT" VARCHAR, -- р/с Поставщика услуги
  "PROVIDER_IBAN" VARCHAR -- IBAN Поставщика
); -- Файл запроса от ощадбанка

CREATE TABLE "oschadbank_request" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор запросов от ощадбанка
  "request_file_id" BIGINT NOT NULL, -- Идентификатор файла запросов
  "account_number" VARCHAR NULL, -- Номер счета
  "status" INTEGER NOT NULL DEFAULT 240, -- Статус
  "UTSZN" VARCHAR, -- Номер УТСЗН
  "OSCHADBANK_ACCOUNT" VARCHAR, -- Номер учетной записи получателя жилищной субсидии в АО «Ощадбанк»
  "FIO" VARCHAR, -- ФИО получателя субсидии
  "SERVICE_ACCOUNT" VARCHAR, -- Номер лицевого счета у поставщика
  "MONTH_SUM" DECIMAL(13,2), -- Общая начисленная сумма за потребленные услуги в отчетном месяце (грн.)
  "SUM" DECIMAL(13,2) -- Общая сумма к оплате, включающая задолженность / переплату за предыдущие периоды (грн.)
); -- Запросы от ощадбанка

CREATE TABLE "oschadbank_response_file" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор файла запросов от ощадбанка
  "request_file_id" BIGINT NOT NULL REFERENCES "request_file" ON DELETE CASCADE, -- Идентификатор файла запросов
  "EDRPOU" VARCHAR, -- ЕДРПОУ
  "PROVIDER_NAME" VARCHAR, -- Название поставщика
  "DOCUMENT_NUMBER" VARCHAR, -- № Анкеты
  "SERVICE_NAME" VARCHAR, -- Название услуги
  "REPORTING_PERIOD" VARCHAR, -- Отчетный период
  "PROVIDER_CODE" VARCHAR, -- Код Банка Поставщика услуги
  "PROVIDER_ACCOUNT" VARCHAR, -- р/с Поставщика услуги
  "PROVIDER_IBAN" VARCHAR, -- IBAN Поставщика
  "PAYMENT_NUMBER" VARCHAR, -- Номер платежного документа
  "REFERENCE_DOCUMENT" VARCHAR, -- Референс документа
  "PAYMENT_DATE" VARCHAR, -- Дата формирования платежного документа
  "TOTAL_AMOUNT" DECIMAL(13,2), -- Общая сумма перечисления
  "ANALYTICAL_ACCOUNT" VARCHAR, -- Номер аналитического счета
  "FEE" DECIMAL(13,2), -- Комиссионное вознаграждение Банка
  "FEE_CODE" VARCHAR, -- Код банка для перечисления комиссии
  "REGISTRY_ID" VARCHAR -- ID реестра
); -- Файл ответа от ощадбанка

CREATE TABLE "oschadbank_response" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор запросов от ощадбанка
  "request_file_id" BIGINT NOT NULL REFERENCES "request_file" ON DELETE CASCADE, -- Идентификатор файла запросов
  "account_number" VARCHAR NULL, -- Номер счета
  "status" INTEGER NOT NULL DEFAULT 240, -- Статус
  "UTSZN" VARCHAR, -- Номер УТСЗН
  "OSCHADBANK_ACCOUNT" VARCHAR, -- Номер учетной записи получателя жилищной субсидии в АО «Ощадбанк»
  "FIO" VARCHAR, -- ФИО получателя субсидии
  "SERVICE_ACCOUNT" VARCHAR, -- Номер лицевого счета у поставщика
  "MONTH_SUM" DECIMAL(13,2), -- Общая начисленная сумма за потребленные услуги в отчетном месяце (грн.)
  "SUM" DECIMAL(13,2), -- Общая сумма к оплате, включающая задолженность / переплату за предыдущие периоды (грн.)
  "SUBSIDY_SUM" DECIMAL(13,2), -- Сумма, уплаченная за счет субсидии
  "DESCRIPTION" VARCHAR -- Описание ошибок
); -- Ответы от ощадбанка

CREATE TABLE "debt" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор
  "request_file_id" BIGINT NULL REFERENCES "request_file" ON DELETE CASCADE, -- Идентификатор файла запросов
  "account_number" VARCHAR NULL, -- Номер счета
  "status" INTEGER NOT NULL DEFAULT 240, -- Код статуса
  "internal_city_id" BIGINT, -- Идентификатор населенного пункта
  "internal_street_id" BIGINT, -- Идентификатор улицы
  "internal_street_type_id" BIGINT, -- Идентификатор типа улицы
  "internal_building_id" BIGINT, -- Идентификатор дома
  "outgoing_city" VARCHAR, -- Название населенного пункта используемое центром начисления
  "outgoing_district" VARCHAR, -- Название района используемое центром начисления
  "outgoing_street" VARCHAR, -- Название улицы используемое центром начисления
  "outgoing_street_type" VARCHAR, -- Название типа улицы используемое центром начисления
  "outgoing_building_number" VARCHAR, -- Номер дома используемый центром начисления
  "outgoing_building_corp" VARCHAR, -- Корпус используемый центром начисления
  "outgoing_apartment" VARCHAR, -- Номер квартиры
  "date" DATE NOT NULL, -- Дата
  "first_name" VARCHAR, -- Имя
  "last_name" VARCHAR, -- Фамилия
  "middle_name" VARCHAR, -- Отчество
  "city" VARCHAR NOT NULL, -- Населенный пункт
  "COD" INTEGER,
  "CDPR" BIGINT,
  "NCARD" BIGINT,
  "IDPIL" VARCHAR,
  "PASPPIL" VARCHAR,
  "FIOPIL" VARCHAR,
  "INDEX" INTEGER,
  "CDUL" INTEGER,
  "HOUSE" VARCHAR,
  "BUILD" VARCHAR,
  "APT" VARCHAR,
  "KAT" INTEGER,
  "LGCODE" INTEGER,
  "DATEIN" VARCHAR,
  "DATEOUT" VARCHAR,
  "MONTHZV" INTEGER,
  "YEARZV" INTEGER,
  "RAH" VARCHAR,
  "MONEY" INTEGER,
  "EBK" VARCHAR,
  "SUM_BORG" DECIMAL(10, 2)
); -- Задолженность


INSERT INTO "config" ("name","value") VALUES ('PAYMENT_FILENAME_PREFIX','A');
INSERT INTO "config" ("name","value") VALUES ('PAYMENT_FILENAME_MASK','a(\d{8}|\d{10}){MM}{YY}[0-3][0-9]\.DBF');
INSERT INTO "config" ("name","value") VALUES ('BENEFIT_FILENAME_PREFIX','AF');
INSERT INTO "config" ("name","value") VALUES ('BENEFIT_FILENAME_MASK','af(\d{8}|\d{10}){MM}{YY}[0-3][0-9]\.DBF');
INSERT INTO "config" ("name","value") VALUES ('PAYMENT_BENEFIT_FILENAME_SUFFIX','(\d{8}|\d{10}){MM}{YY}\d\d\.DBF');
INSERT INTO "config" ("name","value") VALUES ('ACTUAL_PAYMENT_FILENAME_MASK','.*{MM}{YY}\.DBF');
INSERT INTO "config" ("name","value") VALUES ('SUBSIDY_FILENAME_MASK','.*{MM}{YY}\.DBF');
INSERT INTO "config" ("name","value") VALUES ('DWELLING_CHARACTERISTICS_INPUT_FILENAME_MASK','(\d{8}\.a\d{2}');
INSERT INTO "config" ("name","value") VALUES ('DWELLING_CHARACTERISTICS_OUTPUT_FILE_EXTENSION_PREFIX','c');
INSERT INTO "config" ("name","value") VALUES ('FACILITY_SERVICE_TYPE_INPUT_FILENAME_MASK','\d{8}\.b\d{2}');
INSERT INTO "config" ("name","value") VALUES ('FACILITY_SERVICE_TYPE_OUTPUT_FILE_EXTENSION_PREFIX','d');
INSERT INTO "config" ("name","value") VALUES ('SUBSIDY_TARIF_FILENAME_MASK','TARIF12\.DBF');
INSERT INTO "config" ("name","value") VALUES ('FACILITY_STREET_TYPE_REFERENCE_FILENAME_MASK','KLKATUL\.DBF');
INSERT INTO "config" ("name","value") VALUES ('FACILITY_STREET_REFERENCE_FILENAME_MASK','KLUL\.DBF');
INSERT INTO "config" ("name","value") VALUES ('FACILITY_TARIF_REFERENCE_FILENAME_MASK','TARIF\.DBF');
INSERT INTO "config" ("name","value") VALUES ('PRIVILEGE_PROLONGATION_S_FILENAME_MASK','(\d{8}|\d{10})\.s\d{2}');
INSERT INTO "config" ("name","value") VALUES ('PRIVILEGE_PROLONGATION_P_FILENAME_MASK','(\d{8}|\d{10})\.p\d{2}');
INSERT INTO "config" ("name","value") VALUES ('OSCHADBANK_REQUEST_FILENAME_MASK','(R|L)K\d{8}_{YYYY}{MM}\d{8}\.xlsx');
INSERT INTO "config" ("name","value") VALUES ('OSCHADBANK_RESPONSE_FILENAME_MASK','(R|L)K\d{17}\.xlsx');
INSERT INTO "config" ("name","value") VALUES ('DEBT_FILENAME_MASK','\d{8}\.m\d{2}');
INSERT INTO "config" ("name","value") VALUES ('DEBT_OUTPUT_FILE_EXTENSION_PREFIX','n');
INSERT INTO "config" ("name","value") VALUES ('LOAD_THREAD_SIZE','2');
INSERT INTO "config" ("name","value") VALUES ('BIND_THREAD_SIZE','4');
INSERT INTO "config" ("name","value") VALUES ('FILL_THREAD_SIZE','4');
INSERT INTO "config" ("name","value") VALUES ('SAVE_THREAD_SIZE','4');
INSERT INTO "config" ("name","value") VALUES ('LOAD_BATCH_SIZE','16');
INSERT INTO "config" ("name","value") VALUES ('BIND_BATCH_SIZE','64');
INSERT INTO "config" ("name","value") VALUES ('FILL_BATCH_SIZE','64');
INSERT INTO "config" ("name","value") VALUES ('LOAD_MAX_ERROR_COUNT','10');
INSERT INTO "config" ("name","value") VALUES ('BIND_MAX_ERROR_COUNT','10');
INSERT INTO "config" ("name","value") VALUES ('FILL_MAX_ERROR_COUNT','10');
INSERT INTO "config" ("name","value") VALUES ('SAVE_MAX_ERROR_COUNT','10');
INSERT INTO "config" ("name","value") VALUES ('DEFAULT_REQUEST_FILE_CITY','');


INSERT INTO "organization_type"("object_id") VALUES (7);
INSERT INTO "organization_type_string_value"("id", "locale_id", "value")
VALUES (7, 1, UPPER('Отдел субсидий')), (7, 2, UPPER('Отдел субсидий'));
INSERT INTO "organization_type_attribute"("attribute_id", "object_id", "entity_attribute_id", "value_id") VALUES (1, 7, 2300, 7);

INSERT INTO "organization_type"("object_id") VALUES (8);
INSERT INTO "organization_type_string_value"("id", "locale_id", "value")
VALUES (8, 1, UPPER('Отдел льгот')), (8, 2, UPPER('Отдел льгот'));
INSERT INTO "organization_type_attribute"("attribute_id", "object_id", "entity_attribute_id", "value_id") VALUES (1, 8, 2300, 8);


INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES
    (915, 1, UPPER('Директория входящих запросов на субсидию')),
    (915, 2, UPPER('Директория входящих запросов на субсидию'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (915, 900, false, 915, false, 1);

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES
    (916, 1, UPPER('Директория исходящих ответов на запросы на субсидию')),
    (916, 2, UPPER('Директория исходящих ответов на запросы на субсидию'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (916, 900, false, 916, false, 1);

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES
    (917, 1, UPPER('Директория входящих запросов фактического начисления')),
    (917, 2, UPPER('Директория входящих запросов фактического начисления'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (917, 900, false, 917, false, 1);

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES
    (918, 1, UPPER('Директория исходящих ответов на запросы фактического начисления')),
    (918, 2, UPPER('Директория исходящих ответов на запросы фактического начисления'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (918, 900, false, 918, false, 1);

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES
    (919, 1, UPPER('Директория входящих файлов субсидий')),
    (919, 2, UPPER('Директория входящих файлов субсидий'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (919, 900, false, 919, false, 1);

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES
    (920, 1, UPPER('Директория исходящих файлов субсидий')),
    (920, 2, UPPER('Директория исходящих файлов субсидий'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (920, 900, false, 920, false, 1);

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES
    (921, 1, UPPER('Директория входящих файлов характеристик жилья')),
    (921, 2, UPPER('Директория входящих файлов характеристик жилья'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (921, 900, false, 921, false, 1);

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES
    (922, 1, UPPER('Директория исходящих файлов характеристик жилья')),
    (922, 2, UPPER('Директория исходящих файлов характеристик жилья'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (922, 900, false, 922, false, 1);

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES
    (923, 1, UPPER('Директория входящих файлов-запросов видов услуг')),
    (923, 2, UPPER('Директория входящих файлов-запросов видов услуг'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (923, 900, false, 923, false, 1);

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES
    (924, 1, UPPER('Директория исходящих файлов-запросов видов услуг')),
    (924, 2, UPPER('Директория исходящих файлов-запросов видов услуг'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (924, 900, false, 924, false, 1);

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES
    (925, 1, UPPER('Директория справочников')),
    (925, 2, UPPER('Директория справочников'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (925, 900, false, 925, false, 1);

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES
    (926, 1, UPPER('ЕДРПОУ')),
    (926, 2, UPPER('ЕДРПОУ'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (926, 900, true, 926, false, 1);

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES
    (927, 1, UPPER('Корневой каталог для файлов запросов')),
    (927, 2, UPPER('Корневой каталог для файлов запросов'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (927, 900, true, 927, false, 1);

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES
    (928, 1, UPPER('Директория исходящих файлов форма-2 льгота')),
    (928, 2, UPPER('Директория исходящих файлов форма-2 льгота'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (928, 900, false, 928, false, 1);

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES
    (929, 1, UPPER('Директория исходящих файлов местной льготы')),
    (929, 2, UPPER('Директория исходящих файлов местной льготы'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (929, 900, false, 929, false, 1);

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES
    (930, 1, UPPER('Корневой каталог для экспорта файлов')),
    (930, 2, UPPER('Корневой каталог для экспорта файлов'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (930, 900, false, 930, false, 1);

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES
    (931, 1, UPPER('Директория входящих файлов продления льгот')),
    (931, 2, UPPER('Директория входящих файлов продления льгот'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (931, 900, false, 931, true, 1);

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES
    (932, 1, UPPER('Директория входящих файлов запросов от ощадбанка')),
    (932, 2, UPPER('Директорія вхідних файлів запитів від ощадбанку'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (932, 900, false, 932, true, 1);

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES
    (933, 1, UPPER('Директория исходящих файлов запросов от ощадбанка')),
    (933, 2, UPPER('Директорія вихідних файлів запитів від ощадбанку'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (933, 900, false, 933, true, 1);

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES
    (934, 1, UPPER('Директория входящих файлов ответов от ощадбанка')),
    (934, 2, UPPER('Директорія вхідних файлів відповідей від ощадбанку'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (934, 900, false, 934, true, 1);

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES
    (935, 1, UPPER('Директория исходящих файлов ответов от ощадбанка')),
    (935, 2, UPPER('Директорія вихідних файлів відповідей від ощадбанку'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (935, 900, false, 935, true, 1);

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES
    (936, 1, UPPER('Директория входящих файлов задолженностей')),
    (936, 2, UPPER('Директорія вхідних файлів заборгованостей'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (936, 900, false, 936, true, 1);

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES
    (937, 1, UPPER('Директория исходящих файлов задолженностей')),
    (937, 2, UPPER('Директорія вихідних файлів заборгованостей'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (937, 900, false, 937, true, 1);

INSERT INTO "status_description"("code", "name") VALUES
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

INSERT INTO "type_description"("code", "name") VALUES
(1, 'Льгота запроса на субсидию'), (2, 'Начисление запроса на субсидию'), (3, 'Тариф запроса на субсидию'),
(4, 'Фактическое начисление'),(5, 'Субсидия'),(6, 'Характеристики жилья'),(7, 'Виды услуг'),(8, 'Форма-2 льгота'),
(9, 'Типы улиц запроса по льготам'),(10, 'Улицы запроса по льготам'),(11, 'Тарифы запроса по льготам');

INSERT INTO "organization"("object_id") VALUES (0);
SELECT nextval('organization_object_id_sequence');
INSERT INTO "organization_string_value"("id", "locale_id", "value") VALUES
    (1, (SELECT "id" FROM "locale" WHERE "system" = true), UPPER('Модуль обработки файлов запросов от отделов социальной защиты населения')),
    (2, (SELECT "id" FROM "locale" WHERE "system" = true), UPPER('OSZN')),
    (3, (SELECT "id" FROM "locale" WHERE "system" = true), UPPER('СЗ'));
SELECT nextval('organization_string_value_id_sequence');
SELECT nextval('organization_string_value_id_sequence');
SELECT nextval('organization_string_value_id_sequence');
INSERT INTO "organization_attribute"("attribute_id", "object_id", "entity_attribute_id", "value_id")
    VALUES (1, 0, 900, 1), (1 ,0, 901, 2), (1 ,0, 906, 3);

INSERT INTO "config" ("name","value") VALUES ('MODULE_ID', '0');
