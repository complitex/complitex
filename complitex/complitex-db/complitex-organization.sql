CREATE TABLE "organization_type" (
  "pk_id"  BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта
  "parent_id" BIGINT, -- Идентификатор родительского объекта: не используется
  "parent_entity_id" BIGINT REFERENCES "entity", -- Идентификатор сущности родительского объекта: не используется
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Дата начала периода действия объекта
  "end_date" TIMESTAMP NULL DEFAULT NULL, -- Дата окончания периода действия объекта
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус
  "permission_id" BIGINT NOT NULL DEFAULT 0, -- Ключ прав доступа к объекту
  UNIQUE ("object_id","start_date")
); -- Тип организации

CREATE SEQUENCE "organization_type_object_id_sequence";

CREATE INDEX "organization_type_object_id_index" ON "organization_type" ("object_id");
CREATE INDEX "organization_type_start_date_index" ON "organization_type" ("start_date");
CREATE INDEX "organization_type_end_date_index" ON "organization_type" ("end_date");
CREATE INDEX "organization_type_status_index" ON "organization_type" ("status");
CREATE INDEX "organization_type_permission_id_index" ON "organization_type" ("permission_id");

CREATE TABLE "organization_type_attribute" (
  "pk_id"  BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "attribute_id" BIGINT NOT NULL, -- Идентификатор атрибута
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта
  "entity_attribute_id" BIGINT NOT NULL REFERENCES "entity_attribute", -- Идентификатор типа атрибута
  "value_id" BIGINT, -- Идентификатор значения
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Дата начала периода действия атрибута
  "end_date" TIMESTAMP NULL DEFAULT NULL, -- Дата окончания периода действия атрибута
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус
  UNIQUE ("attribute_id","object_id","entity_attribute_id", "start_date"),
  CHECK (check_reference('organization_type', "object_id"))
); -- Атрибуты типа организации

CREATE INDEX "organization_type_attribute_object_id_index" ON "organization_type_attribute" ("object_id");
CREATE INDEX "organization_type_attribute_entity_attribute_id_index" ON "organization_type_attribute" ("entity_attribute_id");
CREATE INDEX "organization_type_attribute_value_id_index" ON "organization_type_attribute" ("value_id");
CREATE INDEX "organization_type_attribute_start_date_index" ON "organization_type_attribute" ("start_date");
CREATE INDEX "organization_type_attribute_end_date_index" ON "organization_type_attribute" ("end_date");
CREATE INDEX "organization_type_attribute_status_index" ON "organization_type_attribute" ("status");

CREATE TABLE "organization_type_string_value" (
  "pk_id"  BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "id" BIGINT NOT NULL, -- Идентификатор локализации
  "locale_id" BIGINT NOT NULL REFERENCES "locale", -- Идентификатор локали
  "value" VARCHAR, -- Текстовое значение
  UNIQUE ("id","locale_id")
); -- Локализация атрибутов типа организации

CREATE SEQUENCE "organization_type_string_value_id_sequence";

CREATE INDEX "organization_type_string_value_value_index" ON "organization_type_string_value" ("id", "value");

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (2300, 1, 'Тип организации'), (2300, 2, 'Тип организации');
INSERT INTO "entity"("id", "entity", "name_id") VALUES (2300, 'organization_type', 2300);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (2301, 1, UPPER('Тип организации')), (2301, 2, UPPER('Тип организации'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (2300, 2300, true, 2301, true, 0);
INSERT INTO "organization_type"("object_id") VALUES (1);
INSERT INTO "organization_type_string_value"("id", "locale_id", "value") VALUES (1, 1, UPPER('Организации пользователей')), (1, 2,UPPER('Организации пользователей'));
INSERT INTO "organization_type_attribute"("attribute_id", "object_id", "entity_attribute_id", "value_id") VALUES (1,1,2300,1);
INSERT INTO "organization_type"("object_id") VALUES (4);
INSERT INTO "organization_type_string_value"("id", "locale_id", "value") VALUES (4, 1, UPPER('ОБСЛУЖИВАЮЩАЯ ОРГАНИЗАЦИЯ')), (4, 2, UPPER('ОБСЛУЖИВАЮЩАЯ ОРГАНИЗАЦИЯ'));
INSERT INTO "organization_type_attribute"("attribute_id", "object_id", "entity_attribute_id", "value_id") VALUES (1, 4, 2300, 4);
INSERT INTO "organization_type"("object_id") VALUES (2);
INSERT INTO "organization_type_string_value"("id", "locale_id", "value") VALUES (2, 1, UPPER('МОДУЛЬ НАЧИСЛЕНИЙ')), (2, 2, UPPER('МОДУЛЬ НАЧИСЛЕНИЙ'));
INSERT INTO "organization_type_attribute"("attribute_id", "object_id", "entity_attribute_id", "value_id") VALUES (1, 2, 2300, 2);
INSERT INTO "organization_type"("object_id") VALUES (3);
INSERT INTO "organization_type_string_value"("id", "locale_id", "value") VALUES (3, 1, UPPER('БАЛАНСОДЕРЖАТЕЛЬ')), (3, 2, UPPER('БАЛАНСОДЕРЖАТЕЛЬ'));
INSERT INTO "organization_type_attribute"("attribute_id", "object_id", "entity_attribute_id", "value_id") VALUES (1, 3, 2300, 3);
INSERT INTO "organization_type"("object_id") VALUES (5);
INSERT INTO "organization_type_string_value"("id", "locale_id", "value") VALUES (5, 1, UPPER('ПОСТАВЩИК УСЛУГ')), (5, 2, UPPER('ПОСТАВЩИК УСЛУГ'));
INSERT INTO "organization_type_attribute"("attribute_id", "object_id", "entity_attribute_id", "value_id") VALUES (1, 5, 2300, 5);
INSERT INTO "organization_type"("object_id") VALUES (6);
INSERT INTO "organization_type_string_value"("id", "locale_id", "value") VALUES (6, 1, UPPER('ПОДРЯДЧИК')), (6, 2, UPPER('ПОДРЯДЧИК'));
INSERT INTO "organization_type_attribute"("attribute_id", "object_id", "entity_attribute_id", "value_id") VALUES (1, 6, 2300, 6);

CREATE TABLE "organization" (
  "pk_id"  BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта
  "parent_id" BIGINT, -- Идентификатор родительского объекта
  "parent_entity_id" BIGINT REFERENCES "entity", -- Идентификатор сущности родительского объекта
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Дата начала периода действия объекта
  "end_date" TIMESTAMP NULL DEFAULT NULL, -- Дата окончания периода действия объекта
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус
  "permission_id" BIGINT NOT NULL DEFAULT 0, -- Ключ прав доступа к объекту
  UNIQUE ("object_id","start_date")
); -- Организация

CREATE SEQUENCE "organization_object_id_sequence";

CREATE INDEX "organization_parent_id_index" ON "organization" ("parent_id");
CREATE INDEX "organization_object_id_index" ON "organization" ("object_id");
CREATE INDEX "organization_start_date_index" ON "organization" ("start_date");
CREATE INDEX "organization_end_date_index" ON "organization" ("end_date");
CREATE INDEX "organization_status_index" ON "organization" ("status");
CREATE INDEX "organization_permission_id_index" ON "organization" ("permission_id");

CREATE TABLE "organization_attribute" (
  "pk_id"  BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "attribute_id" BIGINT NOT NULL, -- Идентификатор атрибута
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта
  "entity_attribute_id" BIGINT NOT NULL REFERENCES "entity_attribute", -- Идентификатор типа атрибута
  "value_id" BIGINT, -- Идентификатор значения
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Дата начала периода действия атрибута
  "end_date" TIMESTAMP NULL DEFAULT NULL, -- Дата окончания периода действия атрибута
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус
  UNIQUE ("attribute_id","object_id","entity_attribute_id", "start_date"),
  CHECK (check_reference('organization', "object_id"))
); -- Атрибуты организации

CREATE INDEX "organization_attribute_object_id_index" ON "organization_attribute" ("object_id");
CREATE INDEX "organization_attribute_entity_attribute_id_index" ON "organization_attribute" ("entity_attribute_id");
CREATE INDEX "organization_attribute_value_id_index" ON "organization_attribute" ("value_id");
CREATE INDEX "organization_attribute_start_date_index" ON "organization_attribute" ("start_date");
CREATE INDEX "organization_attribute_end_date_index" ON "organization_attribute" ("end_date");
CREATE INDEX "organization_attribute_status_index" ON "organization_attribute" ("status");

CREATE TABLE "organization_string_value" (
  "pk_id"  BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "id" BIGINT NOT NULL, -- Идентификатор локализации
  "locale_id" BIGINT NOT NULL REFERENCES "locale", -- Идентификатор локали
  "value" VARCHAR, -- Текстовое значение
  UNIQUE ("id","locale_id")
); -- Локализация атрибутов организации

CREATE SEQUENCE "organization_string_value_id_sequence";

CREATE INDEX "organization_string_value_value_index" ON "organization_string_value" ("id", "value");

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (900, 1, 'Организация'), (900, 2, 'Організація');
INSERT INTO "entity"("id", "entity", "name_id") VALUES (900, 'organization', 900);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (901, 1, UPPER('Наименование организации')), (901, 2, UPPER('Найменування організації'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (900, 900, true, 901, true, 0);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (902, 1, UPPER('Уникальный код организации')), (902, 2, UPPER('Унікальний код організації'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (901, 900, true, 902, true, 1);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (903, 1, UPPER('Район')), (903, 2, UPPER('Район'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id", "reference_id") VALUES (902, 900, false, 903, true, 10, 600);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (904, 1, UPPER('Родительская организация')), (904, 2, UPPER('Родительская организация'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id", "reference_id") VALUES (903, 900, false, 904, true, 10, 900);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (905, 1, UPPER('Тип организации')), (905, 2, UPPER('Тип организации'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id", "reference_id") VALUES (904, 900, false, 905, true, 10, 2300);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (906, 1, UPPER('Короткое наименование')), (906, 2, UPPER('Короткое наименование'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (906, 900, false, 906, true, 0);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (913, 1, UPPER('Ресурс доступа к МН')), (913, 2, UPPER('Ресурс доступа к МН'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (913, 900, false, 913, true, 1);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (4914, 1, UPPER('Услуга')), (4914, 2, UPPER('Услуга'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id", "reference_id") VALUES (4914, 900, false, 4914, true, 10, 1600);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (4915, 1, UPPER('Модуль начислений')), (4915, 2, UPPER('Модуль начислений'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id", "reference_id") VALUES (4915, 900, false, 4915, true, 10, 900);

CREATE TABLE "organization_correction" (
  "id"  BIGSERIAL PRIMARY KEY, -- Идентификатор коррекции
  "parent_id" BIGINT, -- Идентификатор родительской организации
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта организация
  "external_id" BIGINT, -- Внешний идентификатор организации
  "correction" VARCHAR NOT NULL, -- Код организации
  "start_date" TIMESTAMP NOT NULL DEFAULT NOW(), -- Дата начала актуальности соответствия
  "end_date" TIMESTAMP, -- Дата окончания актуальности соответствия
  "organization_id" BIGINT NOT NULL, -- Идентификатор организации
  "user_organization_id" BIGINT,
  "module_id" BIGINT, -- Идентификатор внутренней организации
  UNIQUE (external_id, organization_id, user_organization_id),
  CHECK (check_reference('organization', "object_id")),
  CHECK (check_reference('organization', "organization_id")),
  CHECK (check_reference('organization', "user_organization_id")),
  CHECK (check_reference('organization', "module_id"))
); -- Коррекция организации

CREATE INDEX "organization_correction_parent_id_index" ON "organization_correction" ("parent_id");
CREATE INDEX "organization_correction_object_id_index" ON "organization_correction" ("object_id");
CREATE INDEX "organization_correction_correction_index" ON "organization_correction" ("correction");
CREATE INDEX "organization_correction_start_date_index" ON "organization_correction" ("start_date");
CREATE INDEX "organization_correction_end_date_index" ON "organization_correction" ("end_date");
CREATE INDEX "organization_correction_organization_id_index" ON "organization_correction" ("organization_id");
CREATE INDEX "organization_correction_user_organization_id_index" ON "organization_correction" ("user_organization_id");

CREATE TABLE "user_organization" (
    "id"  BIGSERIAL PRIMARY KEY, -- Идентификатор организации пользователей
    "user_id" BIGINT NOT NULL, -- Идентификатор пользователя
    "organization_object_id" BIGINT NOT NULL, -- Идентификатор организации
    "main" BOOLEAN NOT NULL DEFAULT FALSE, -- Является ли организация основной
    UNIQUE ("user_id", "organization_object_id"),
    CHECK (check_reference('user', "user_id")),
    CHECK (check_reference('organization', "organization_object_id"))
); -- Организация пользователей

CREATE INDEX "user_organization_user_id_index" ON "user_organization" ("user_id");
CREATE INDEX "user_organization_organization_object_id_index" ON "user_organization" ("organization_object_id");

CREATE TABLE "service" (
  "pk_id" BIGSERIAL PRIMARY KEY, --Суррогатный ключ
  "object_id" BIGINT NOT NULL, --Идентификатор объекта
  "parent_id" BIGINT, --Не используется
  "parent_entity_id" BIGINT, --Не используется
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, --Дата начала периода действия параметров объекта
  "end_date" TIMESTAMP NULL DEFAULT NULL, --Дата завершения периода действия параметров объекта
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус
  "permission_id" BIGINT NOT NULL DEFAULT 0, --Ключ прав доступа к объекту
  UNIQUE ("object_id","start_date")
); --Услуга

CREATE SEQUENCE "service_object_id_sequence";

CREATE INDEX "service_object_id_index" ON "service" ("object_id");
CREATE INDEX "service_start_date_index" ON "service" ("start_date");
CREATE INDEX "service_end_date_index" ON "service" ("end_date");
CREATE INDEX "service_status_index" ON "service" ("status");
CREATE INDEX "service_permission_id_index" ON "service" ("permission_id");

CREATE TABLE "service_attribute" (
  "pk_id" BIGSERIAL PRIMARY KEY, --Суррогатный ключ
  "attribute_id" BIGINT NOT NULL, --Идентификатор атрибута
  "object_id" BIGINT NOT NULL, --Идентификатор объекта
  "entity_attribute_id" BIGINT NOT NULL REFERENCES "entity_attribute", --Идентификатор типа атрибута
  "value_id" BIGINT, --Идентификатор значения
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, --Дата начала периода действия параметров атрибута
  "end_date" TIMESTAMP NULL DEFAULT NULL, --Дата окончания периода действия параметров атрибута
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус
  UNIQUE ("attribute_id","object_id","entity_attribute_id", "start_date"),
  CHECK (check_reference('service', "object_id"))
); --Атрибуты услуги

CREATE INDEX "service_attribute_object_id_index" ON "service_attribute" ("object_id");
CREATE INDEX "service_attribute_entity_attribute_id_index" ON "service_attribute" ("entity_attribute_id");
CREATE INDEX "service_attribute_value_id_index" ON "service_attribute" ("value_id");
CREATE INDEX "service_attribute_start_date_index" ON "service_attribute" ("start_date");
CREATE INDEX "service_attribute_end_date_index" ON "service_attribute" ("end_date");
CREATE INDEX "service_attribute_status_index" ON "service_attribute" ("status");

CREATE TABLE "service_string_value" (
  "pk_id" BIGSERIAL PRIMARY KEY, --Суррогатный ключ
  "id" BIGINT NOT NULL, --Идентификатор значения
  "locale_id" BIGINT NOT NULL, --Идентификатор локали
  "value" VARCHAR, --Текстовое значение
  UNIQUE ("id","locale_id")
); --Локализированное значение атрибута услуги

CREATE SEQUENCE "service_string_value_id_sequence";

CREATE INDEX "service_string_value_value_index" ON "service_string_value" ("id", "value");

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (1600, 1, 'Услуга'), (1600, 2, 'Услуга');
INSERT INTO "entity"("id", "entity", "name_id") VALUES (1600, 'service', 1600);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (1601, 1, UPPER('Название')), (1601, 2, UPPER('Название'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (1601, 1600, true, 1601, true, 0);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (1602, 1, UPPER('Короткое название')), (1602, 2, UPPER('Короткое название'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (1602, 1600, true, 1602, true, 0);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (1603, 1, UPPER('Код')), (1603, 2, UPPER('Код'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (1603, 1600, false, 1603, true, 0);

INSERT INTO "service"("object_id") VALUES (1), (2), (3), (4), (5), (6), (7), (8);
INSERT INTO "service_string_value"("id", "locale_id", "value") VALUES
    (1, 1, UPPER('квартплата')), (1, 2,UPPER('квартплата')),
    (2, 1, UPPER('отопление')), (2, 2, UPPER('опалення')),
    (3, 1, UPPER('горячая вода')), (3, 2, UPPER('гаряча вода')),
    (4, 1, UPPER('холодная вода')), (4, 2, UPPER('холодна вода')),
    (5, 1, UPPER('газ')), (5, 2, UPPER('газ')),
    (6, 1, UPPER('электроэнергия')), (6, 2, UPPER('електроенергія')),
    (7, 1, UPPER('вывоз мусора')), (7, 2, UPPER('вивіз сміття')),
    (8, 1, UPPER('водоотведение')), (8, 2, UPPER('вивіз нечистот'));
INSERT INTO "service_attribute"("attribute_id", "object_id", "entity_attribute_id", "value_id")
  VALUES (1, 1, 1601, 1), (1, 2, 1601, 2), (1, 3, 1601, 3), (1, 4, 1601, 4), (1, 5, 1601, 5), (1, 6, 1601, 6), (1, 7, 1601, 7), (1, 8, 1601, 8);

CREATE TABLE "service_correction" (
  "id" BIGSERIAL PRIMARY KEY, --Идентификатор коррекции
  "object_id" BIGINT NOT NULL, --Идентификатор объекта услуги
  "external_id" BIGINT, --Внешний идентификатор услуги
  "correction" VARCHAR NOT NULL, --Код услуги
  "start_date" TIMESTAMP NOT NULL DEFAULT NOW(), --Дата начала актуальности соответствия
  "end_date" TIMESTAMP, --Дата окончания актуальности соответствия
  "organization_id" BIGINT NOT NULL, --Идентификатор услуги
  "user_organization_id" BIGINT,
  "module_id" BIGINT, --Идентификатор внутренней организации
  UNIQUE (external_id, organization_id, user_organization_id),
  CHECK (check_reference('service', "object_id")),
  CHECK (check_reference('organization', "organization_id")),
  CHECK (check_reference('organization', "user_organization_id")),
  CHECK (check_reference('organization', "module_id"))
); --Коррекция услуги

CREATE INDEX "service_correction_object_id_index" ON "service_correction" ("object_id");
CREATE INDEX "service_correction_correction_index" ON "service_correction" ("correction");
CREATE INDEX "service_correction_start_date_index" ON "service_correction" ("start_date");
CREATE INDEX "service_correction_end_date_index" ON "service_correction" ("end_date");
CREATE INDEX "service_correction_organization_id_index" ON "service_correction" ("organization_id");
CREATE INDEX "service_correction_user_organization_id_index" ON "service_correction" ("user_organization_id");
