CREATE TABLE "last_name" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор фамилии
  "name" VARCHAR NOT NULL, -- Фамилия
  UNIQUE ("name")
); -- Фамилия

CREATE TABLE "first_name" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор имени
  "name" VARCHAR NOT NULL, -- Имя
  UNIQUE  ("name")
); -- Имя

CREATE TABLE "middle_name" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор отчества
  "name" VARCHAR NOT NULL, -- Отчество
  UNIQUE ("name")
); -- Отчество

CREATE TABLE  "user" (
  "id" BIGSERIAL NOT NULL PRIMARY KEY, -- Идентификатор пользователя
  "login" VARCHAR NOT NULL, -- Имя пользователя
  "password" VARCHAR NOT NULL, -- Пароль
  "user_info_object_id" BIGINT, -- Идентификатор объекта информации о пользователе
  UNIQUE ("login")
); -- Пользователь

CREATE VIEW user_view AS SELECT * FROM "user";

CREATE TABLE  "usergroup" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор группы пользователей
  "login" VARCHAR NOT NULL REFERENCES "user" ("login"), -- Имя пользователя
  "group_name" VARCHAR NOT NULL -- Название группы
); -- Группа пользователей

CREATE TABLE "user_info" (
  "pk_id" BIGSERIAL NOT NULL PRIMARY KEY, -- Суррогатный ключ
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта
  "parent_id" BIGINT, -- Идентификатор родительского объекта: не используется
  "parent_entity_id" BIGINT REFERENCES "entity", -- Идентификатор сущности родительского объекта: не используется
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Дата начала периода действия объекта
  "end_date" TIMESTAMP NULL DEFAULT NULL, -- Дата окончания периода действия объекта
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус
  "permission_id" BIGINT NOT NULL DEFAULT 0,  -- Ключ прав доступа к объекту
  UNIQUE ("object_id","start_date")
); -- Информация о пользователе

CREATE SEQUENCE "user_info_object_id_sequence";

CREATE INDEX "user_info_object_id_index" ON "user_info" ("object_id");
CREATE INDEX "user_info_parent_id_index" ON "user_info" ("parent_id");
CREATE INDEX "user_info_start_date_index" ON "user_info" ("start_date");
CREATE INDEX "user_info_status_index" ON "user_info" ("status");

CREATE TABLE "user_info_attribute" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "attribute_id" BIGINT NOT NULL, -- Идентификатор атрибута
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта
  "entity_attribute_id" BIGINT NOT NULL REFERENCES "entity_attribute", -- Идентификатор типа атрибута
  "value_id" BIGINT, -- Идентификатор значения
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Дата начала периода действия атрибута
  "end_date" TIMESTAMP NULL DEFAULT NULL, -- Дата окончания периода действия атрибута
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус
  CHECK (check_reference('user_info', "object_id"))
); -- Атрибуты информации о пользователе

CREATE INDEX "user_info_attribute_object_id_index" ON "user_info_attribute" ("object_id");
CREATE INDEX "user_info_attribute_entity_attribute_id_index" ON "user_info_attribute" ("entity_attribute_id");
CREATE INDEX "user_info_attribute_value_id_index" ON "user_info_attribute" ("value_id");
CREATE INDEX "user_info_attribute_start_date_index" ON "user_info_attribute" ("start_date");
CREATE INDEX "user_info_attribute_end_date_index" ON "user_info_attribute" ("end_date");
CREATE INDEX "user_info_attribute_status_index" ON "user_info_attribute" ("status");

CREATE TABLE "user_info_string_value" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "id" BIGINT NOT NULL, -- Идентификатор локализации
  "locale_id" BIGINT NOT NULL REFERENCES "locale", -- Идентификатор локали
  "value" VARCHAR,  -- Текстовое значение
  UNIQUE ("id", "locale_id")
); -- Локализация атрибутов информации о пользователе

CREATE SEQUENCE "user_info_string_value_id_sequence";

CREATE INDEX "user_info_string_value_value_index" ON "user_info_string_value" ("id", "value");

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (1000, 1, 'Пользователь'), (1000, 2, 'Користувач');
INSERT INTO "entity"("id", "entity", "name_id") VALUES (1000, 'user_info', 1000);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (1001, 1, UPPER('Фамилия')), (1001, 2, UPPER('Прізвище'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", value_type_id) VALUES (1000, 1000, true, 1001, true, 21);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (1002, 1, UPPER('Имя')), (1002, 2, UPPER('Ім''я'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (1001, 1000, true, 1002, true, 22);
INSERT INTO "entity_string_value" ("id", "locale_id", "value") VALUES (1003, 1, UPPER('Отчество')), (1003, 2, UPPER('По батькові'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (1002, 1000, true, 1003, true, 23);

INSERT INTO "user" ("login", "password", "user_info_object_id") VALUES ('admin', encode(sha256('admin'::bytea), 'hex'), 1);
INSERT INTO "usergroup" ("login", "group_name") VALUES ('admin', 'ADMINISTRATORS');
INSERT INTO "usergroup" ("login", "group_name") VALUES ('admin', 'EMPLOYEES');
INSERT INTO "usergroup" ("login", "group_name") VALUES ('admin', 'EMPLOYEES_CHILD_VIEW');
INSERT INTO "user_info" ("object_id") VALUES (1);
INSERT INTO "first_name"("name") VALUES ('admin');
INSERT INTO "middle_name"("name") VALUES ('admin');
INSERT INTO "last_name"("name") VALUES ('admin');
INSERT INTO "user_info_attribute" ("attribute_id", "object_id", "entity_attribute_id", "value_id") VALUES (1, 1,1000, 1), (1, 1,1001, 1), (1, 1, 1002, 1);

CREATE TABLE  "log" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор записи журнала событий
  "date" TIMESTAMP, -- Дата
  "login" VARCHAR REFERENCES "user" ("login"), -- Имя пользователя
  "module" VARCHAR,  -- Название модуля системы
  "object_id" BIGINT, -- Идентификатор объекта
  "controller" VARCHAR,  -- Название класса обработчика
  "model" VARCHAR,  -- Название класса модели данных
  "event" VARCHAR,  -- Название события
  "status" VARCHAR,  -- Статус
  "description" VARCHAR -- Описание
); -- Журнал событий

CREATE TABLE "log_change" (
    "id" BIGSERIAL PRIMARY KEY, -- Идентификатор изменения
    "log_id" BIGINT NOT NULL REFERENCES "log", -- Идентификатор журнала событий
    "attribute_id" BIGINT, -- Идентификатор атрибута
    "collection" VARCHAR,  -- Название группы параметров
    "property" VARCHAR,  -- Свойство
    "old_value" VARCHAR,  -- Предыдущее значение
    "new_value" VARCHAR,  -- Новое значение
    "locale" VARCHAR -- Код локали
); -- Изменения модели данных

CREATE TABLE "preference" (
    "id" BIGSERIAL PRIMARY KEY, -- Идентификатор предпочтения
    "user_id" BIGINT REFERENCES "user", -- Идентификатор пользователя
    "page" VARCHAR NOT NULL, -- Класс страницы
    "key" VARCHAR NOT NULL, -- Ключ
    "value" VARCHAR NOT NULL, -- Значение
    UNIQUE ("user_id", "page", "key")
); -- Предпочтения пользователя

CREATE TABLE "config" (
    "id" BIGSERIAL PRIMARY KEY, -- Идентификатор настройки
    "name" VARCHAR NOT NULL, -- Имя
    "value" VARCHAR NOT NULL, -- Значение
    UNIQUE ("name")
); -- Настройки

CREATE TABLE "update" (
    "id" BIGSERIAL PRIMARY KEY, -- Идентификатор обновления
    "version" VARCHAR NOT NULL, -- Версия
    "date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP --Дата обновления
); -- Обновление базы данных
