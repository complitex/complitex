CREATE TABLE "country" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта
  "parent_id" BIGINT, -- Идентификатор родительского объекта: не используется
  "parent_entity_id" BIGINT REFERENCES "entity", -- Идентификатор сущности родительского объекта: не используется
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Дата начала периода действия объекта
  "end_date" TIMESTAMP NULL DEFAULT NULL, -- Дата окончания периода действия объекта
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус
  "permission_id" BIGINT NOT NULL DEFAULT 0, -- Ключ прав доступа к объекту
  UNIQUE ("object_id","start_date")
); -- Страна

CREATE SEQUENCE "country_object_id_sequence";

CREATE INDEX "country_object_id_index" ON "country" ("object_id");
CREATE INDEX "country_start_date_index" ON "country" ("start_date");
CREATE INDEX "country_end_date_index" ON "country" ("end_date");
CREATE INDEX "country_status_index" ON "country" ("status");
CREATE INDEX "country_permission_id_index" ON "country" ("permission_id");

CREATE TABLE "country_attribute" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "attribute_id" BIGINT NOT NULL, -- Идентификатор атрибута
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта
  "entity_attribute_id" BIGINT NOT NULL REFERENCES "entity_attribute", -- Идентификатор типа атрибута
  "value_id" BIGINT, -- Идентификатор значения
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Дата начала периода действия атрибута
  "end_date" TIMESTAMP NULL DEFAULT NULL, -- Дата окончания периода действия атрибута
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус
  UNIQUE ("attribute_id", "object_id", "entity_attribute_id", "start_date"),
  CHECK (check_reference('country', "object_id"))
); -- Атрибуты страны

CREATE INDEX "country_attribute_object_id_index" ON "country_attribute" ("object_id");
CREATE INDEX "country_attribute_entity_attribute_id_index" ON "country_attribute" ("entity_attribute_id");
CREATE INDEX "country_attribute_value_id_index" ON "country_attribute" ("value_id");
CREATE INDEX "country_attribute_start_date_index" ON "country_attribute" ("start_date");
CREATE INDEX "country_attribute_end_date_index" ON "country_attribute" ("end_date");
CREATE INDEX "country_attribute_status_index" ON "country_attribute" ("status");

CREATE TABLE "country_string_value" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "id" BIGINT NOT NULL, -- Идентификатор локализации
   "locale_id" BIGINT NOT NULL REFERENCES "locale" REFERENCES "locale", -- Идентификатор локали
  "value" VARCHAR, -- Текстовое значение
  UNIQUE ("id","locale_id")
); -- Локализация атрибутов страны

CREATE SEQUENCE "country_string_value_id_sequence";

CREATE INDEX "country_string_value_value_index" ON "country_string_value" ("id", "value");

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (800, 1, 'Страна'), (800, 2, 'Країна');
INSERT INTO "entity"("id", "entity", "name_id") VALUES (800, 'country', 800);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (801, 1, UPPER('Наименование страны')), (801, 2, UPPER('Найменування країни'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (800, 800, true, 801, true, 0);

CREATE TABLE "country_correction" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор коррекции
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта страны
  "external_id" BIGINT, -- Внешний идентификатор объекта
  "correction" VARCHAR NOT NULL, -- Название страны
  "start_date" TIMESTAMP NOT NULL DEFAULT NOW(), -- Дата начала актуальности соответствия
  "end_date" TIMESTAMP, -- Дата окончания актуальности соответствия
  "organization_id" BIGINT NOT NULL, -- Идентификатор организации
  "user_organization_id" BIGINT, -- Идентификатор организации пользователя
  "module_id" BIGINT, -- Идентификатор внутренней организации
  UNIQUE (external_id, organization_id, user_organization_id),
  CHECK (check_reference('country', "object_id")),
  CHECK (check_reference('organization', "organization_id")),
  CHECK (check_reference('organization', "user_organization_id")),
  CHECK (check_reference('organization', "module_id"))
); -- Коррекция страны

CREATE INDEX "country_correction_object_id_index" ON "country_correction" ("object_id");
CREATE INDEX "country_correction_correction_index" ON "country_correction" ("correction");
CREATE INDEX "country_correction_start_date_index" ON "country_correction" ("start_date");
CREATE INDEX "country_correction_end_date_index" ON "country_correction" ("end_date");
CREATE INDEX "country_correction_organization_id_index" ON "country_correction" ("organization_id");
CREATE INDEX "country_correction_user_organization_id_index" ON "country_correction" ("user_organization_id");

CREATE TABLE "region" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта
  "parent_id" BIGINT, -- Идентификатор родительского объекта
  "parent_entity_id" BIGINT REFERENCES "entity", -- Идентификатор сущности родительского объекта: 800 - country
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, --Дата начала периода действия объекта
  "end_date" TIMESTAMP NULL DEFAULT NULL, -- Дата окончания периода действия объекта
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус
  "permission_id" BIGINT NOT NULL DEFAULT 0, -- Ключ прав доступа к объекту
  UNIQUE ("object_id","start_date"),
  CHECK (check_reference('country', "parent_id"))
); -- Регион

CREATE SEQUENCE "region_object_id_sequence";

CREATE INDEX "region_object_id_index" ON "region" ("object_id");
CREATE INDEX "region_parent_id_index" ON "region" ("parent_id");
CREATE INDEX "region_start_date_index" ON "region" ("start_date");
CREATE INDEX "region_end_date_index" ON "region" ("end_date");
CREATE INDEX "region_status_index" ON "region" ("status");
CREATE INDEX "region_permission_id_index" ON "region" ("permission_id");

CREATE TABLE "region_attribute" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "attribute_id" BIGINT NOT NULL, -- Идентификатор атрибута
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта
  "entity_attribute_id" BIGINT NOT NULL REFERENCES "entity_attribute", -- Идентификатор типа атрибута
  "value_id" BIGINT, -- Идентификатор значения
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Дата начала периода действия атрибута
  "end_date" TIMESTAMP NULL DEFAULT NULL, -- Дата окончания периода действия атрибута
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус
  UNIQUE ("attribute_id", "object_id", "entity_attribute_id", "start_date"),
  CHECK (check_reference('region', "object_id"))
); -- Атрибуты региона

CREATE INDEX "region_attribute_object_id_index" ON "region_attribute" ("object_id");
CREATE INDEX "region_attribute_entity_attribute_id_index" ON "region_attribute" ("entity_attribute_id");
CREATE INDEX "region_attribute_value_id_index" ON "region_attribute" ("value_id");
CREATE INDEX "region_attribute_start_date_index" ON "region_attribute" ("start_date");
CREATE INDEX "region_attribute_end_date_index" ON "region_attribute" ("end_date");
CREATE INDEX "region_attribute_status_index" ON "region_attribute" ("status");

CREATE TABLE "region_string_value" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "id" BIGINT NOT NULL, -- Идентификатор локализации
   "locale_id" BIGINT NOT NULL REFERENCES "locale", -- Идентификатор локали
  "value" VARCHAR, -- Текстовое значение
  UNIQUE ("id","locale_id")
); -- Локализация атрибутов региона

CREATE SEQUENCE "region_string_value_id_sequence";

CREATE INDEX "region_string_value_value_index" ON "region_string_value" ("id", "value");

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (700, 1, 'Регион'), (700, 2, 'Регіон');
INSERT INTO "entity"("id", "entity", "name_id") VALUES (700, 'region', 700);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (701, 1, UPPER('Наименование региона')), (701, 2, UPPER('Найменування регіону'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (700, 700, true, 701, true, 0);

CREATE TABLE "region_correction" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор коррекции
  "parent_id" BIGINT, -- Идентификатор страны
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта региона
  "external_id" BIGINT, -- Внешний идентификатор объекта
  "correction" VARCHAR NOT NULL, -- Название страны
  "start_date" TIMESTAMP NOT NULL DEFAULT NOW(), -- Дата начала актуальности соответствия
  "end_date" TIMESTAMP, -- Дата окончания актуальности соответствия
  "organization_id" BIGINT NOT NULL, -- Идентификатор организации
  "user_organization_id" BIGINT, -- Идентификатор организации пользователя
  "module_id" BIGINT, -- Идентификатор внутренней организации
  UNIQUE (external_id, organization_id, user_organization_id),
  CHECK (check_reference('country', "parent_id")),
  CHECK (check_reference('region', "object_id")),
  CHECK (check_reference('organization', "organization_id")),
  CHECK (check_reference('organization', "user_organization_id")),
  CHECK (check_reference('organization', "module_id"))
); -- Коррекция региона

CREATE INDEX "region_correction_object_id_index" ON "region_correction" ("object_id");
CREATE INDEX "region_correction_correction_index" ON "region_correction" ("correction");
CREATE INDEX "region_correction_start_date_index" ON "region_correction" ("start_date");
CREATE INDEX "region_correction_end_date_index" ON "region_correction" ("end_date");
CREATE INDEX "region_correction_organization_id_index" ON "region_correction" ("organization_id");
CREATE INDEX "region_correction_user_organization_id_index" ON "region_correction" ("user_organization_id");

CREATE TABLE "city_type" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта
  "parent_id" BIGINT, -- Идентификатор родительского объекта: не используется
  "parent_entity_id" BIGINT REFERENCES "entity", -- Идентификатор сущности родительского объекта: не используется
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Дата начала периода действия объекта
  "end_date" TIMESTAMP NULL DEFAULT NULL, -- Дата окончания периода действия объекта
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус
  "permission_id" BIGINT NOT NULL DEFAULT 0, -- Ключ прав доступа к объекту
  UNIQUE ("object_id","start_date")
); -- Тип населенного пункта

CREATE SEQUENCE "city_type_object_id_sequence";

CREATE INDEX "city_type_object_id_index" ON "city_type" ("object_id");
CREATE INDEX "city_type_start_date_index" ON "city_type" ("start_date");
CREATE INDEX "city_type_end_date_index" ON "city_type" ("end_date");
CREATE INDEX "city_type_status_index" ON "city_type" ("status");
CREATE INDEX "city_type_permission_id_index" ON "city_type" ("permission_id");

CREATE TABLE "city_type_attribute" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "attribute_id" BIGINT NOT NULL, -- Идентификатор атрибута
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта
  "entity_attribute_id" BIGINT NOT NULL REFERENCES "entity_attribute", -- Идентификатор типа атрибута
  "value_id" BIGINT, -- Идентификатор значения
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Дата начала периода действия атрибута
  "end_date" TIMESTAMP NULL DEFAULT NULL, -- Дата окончания периода действия атрибута
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус
  UNIQUE ("attribute_id", "object_id", "entity_attribute_id", "start_date"),
  CHECK (check_reference('city_type', "object_id"))
); -- Атрибуты типа населенного пункта

CREATE INDEX "city_type_attribute_object_id_index" ON "city_type_attribute" ("object_id");
CREATE INDEX "city_type_attribute_entity_attribute_id_index" ON "city_type_attribute" ("entity_attribute_id");
CREATE INDEX "city_type_attribute_value_id_index" ON "city_type_attribute" ("value_id");
CREATE INDEX "city_type_attribute_start_date_index" ON "city_type_attribute" ("start_date");
CREATE INDEX "city_type_attribute_end_date_index" ON "city_type_attribute" ("end_date");
CREATE INDEX "city_type_attribute_status_index" ON "city_type_attribute" ("status");

CREATE TABLE "city_type_string_value" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "id" BIGINT NOT NULL, -- Идентификатор локализации
   "locale_id" BIGINT NOT NULL REFERENCES "locale", -- Идентификатор локали
  "value" VARCHAR, -- Текстовое значение
  UNIQUE ("id","locale_id")
); -- Локализация атрибутов типа населенного пункта

CREATE SEQUENCE "city_type_string_value_id_sequence";

CREATE INDEX "city_type_string_value_value_index" ON "city_type_string_value" ("id", "value");

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (1300, 1, 'Тип нас. пункта'), (1300, 2, 'Тип населенного пункта');
INSERT INTO "entity"("id", "entity", "name_id") VALUES (1300, 'city_type', 1300);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (1301, 1, UPPER('Краткое название')), (1301, 2, UPPER('Краткое название'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (1300, 1300, true, 1301, true, 0);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (1302, 1, UPPER('Название')), (1302, 2, UPPER('Название'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (1301, 1300, true, 1302, true, 0);

CREATE TABLE "city_type_correction" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор коррекции
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта типа населенного пункта
  "external_id" BIGINT, -- Внешний идентификатор объекта
  "correction" VARCHAR NOT NULL, -- Название типа населенного пункта
  "start_date" TIMESTAMP NOT NULL DEFAULT NOW(), -- Дата начала актуальности соответствия
  "end_date" TIMESTAMP, -- Дата окончания актуальности соответствия
  "organization_id" BIGINT NOT NULL, -- Идентификатор организации
  "user_organization_id" BIGINT, -- Идентификатор организации пользователя
  "module_id" BIGINT, -- Идентификатор внутренней организации
  UNIQUE (external_id, organization_id, user_organization_id),
  CHECK (check_reference('city_type', "object_id")),
  CHECK (check_reference('organization', "organization_id")),
  CHECK (check_reference('organization', "user_organization_id")),
  CHECK (check_reference('organization', "module_id"))
); -- Коррекция типа населенного пункта

CREATE INDEX "city_type_correction_object_id_index" ON "city_type_correction" ("object_id");
CREATE INDEX "city_type_correction_correction_index" ON "city_type_correction" ("correction");
CREATE INDEX "city_type_correction_start_date_index" ON "city_type_correction" ("start_date");
CREATE INDEX "city_type_correction_end_date_index" ON "city_type_correction" ("end_date");
CREATE INDEX "city_type_correction_organization_id_index" ON "city_type_correction" ("organization_id");
CREATE INDEX "city_type_correction_user_organization_id_index" ON "city_type_correction" ("user_organization_id");

CREATE TABLE "city" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта
  "parent_id" BIGINT, -- Идентификатор родительского объекта
  "parent_entity_id" BIGINT REFERENCES "entity", -- Идентификатор сущности родительского объекта: 700 - region
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Дата начала периода действия объекта
  "end_date" TIMESTAMP NULL DEFAULT NULL, -- Дата окончания периода действия объекта
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус
  "permission_id" BIGINT NOT NULL DEFAULT 0, -- Ключ прав доступа к объекту
  UNIQUE ("object_id","start_date"),
  CHECK (check_reference('region', "parent_id"))
); -- Населенный пункт

CREATE SEQUENCE "city_object_id_sequence";

CREATE INDEX "city_object_id_index" ON "city" ("object_id");
CREATE INDEX "city_parent_id_index" ON "city" ("parent_id");
CREATE INDEX "city_start_date_index" ON "city" ("start_date");
CREATE INDEX "city_end_date_index" ON "city" ("end_date");
CREATE INDEX "city_status_index" ON "city" ("status");
CREATE INDEX "city_permission_id_index" ON "city" ("permission_id");

CREATE TABLE "city_attribute" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "attribute_id" BIGINT NOT NULL, -- Идентификатор атрибута
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта
  "entity_attribute_id" BIGINT NOT NULL REFERENCES "entity_attribute", -- Идентификатор типа атрибута
  "value_id" BIGINT, -- Идентификатор значения
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Дата начала периода действия атрибута
  "end_date" TIMESTAMP NULL DEFAULT NULL, -- Дата окончания периода действия атрибута
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус
  UNIQUE ("attribute_id", "object_id", "entity_attribute_id", "start_date"),
  CHECK (check_reference('city', "object_id"))
); -- Атрибуты населенного пункта

CREATE INDEX "city_attribute_object_id_index" ON "city_attribute" ("object_id");
CREATE INDEX "city_attribute_entity_attribute_id_index" ON "city_attribute" ("entity_attribute_id");
CREATE INDEX "city_attribute_value_id_index" ON "city_attribute" ("value_id");
CREATE INDEX "city_attribute_start_date_index" ON "city_attribute" ("start_date");
CREATE INDEX "city_attribute_end_date_index" ON "city_attribute" ("end_date");
CREATE INDEX "city_attribute_status_index" ON "city_attribute" ("status");

CREATE TABLE "city_string_value" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "id" BIGINT NOT NULL, -- Идентификатор локализации
   "locale_id" BIGINT NOT NULL REFERENCES "locale", -- Идентификатор локали
  "value" VARCHAR, -- Текстовое значение
  UNIQUE ("id","locale_id")
); -- Локализация атрибутов населенного пункта

CREATE SEQUENCE "city_string_value_id_sequence";

CREATE INDEX "city_string_value_value_index" ON "city_string_value" ("id", "value");

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (400, 1, 'Населенный пункт'), (400, 2, 'Населений пункт');
INSERT INTO "entity"("id", "entity", "name_id") VALUES (400, 'city', 400);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (401, 1, UPPER('Наименование населенного пункта')), (401, 2, UPPER('Найменування населеного пункту'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (400, 400, true, 401, true, 0);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (402, 1, UPPER('Тип населенного пункта')), (402, 2, UPPER('Тип населенного пункта'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id", "reference_id") VALUES (401, 400, true, 402, true, 10, 1300);

CREATE TABLE "city_correction" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор коррекции
  "parent_id" BIGINT, -- Идентификатор региона
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта населенного пункта
  "external_id" BIGINT, -- Внешний идентификатор объекта
  "correction" VARCHAR NOT NULL, -- Название населенного пункта
  "start_date" TIMESTAMP NOT NULL DEFAULT NOW(), -- Дата начала актуальности соответствия
  "end_date" TIMESTAMP, -- Дата окончания актуальности соответствия
  "organization_id" BIGINT NOT NULL, -- Идентификатор организации
  "user_organization_id" BIGINT, -- Идентификатор организации пользователя
  "module_id" BIGINT, -- Идентификатор внутренней организации
  UNIQUE (external_id, organization_id, user_organization_id),
  CHECK (check_reference('region', "parent_id")),
  CHECK (check_reference('city', "object_id")),
  CHECK (check_reference('organization', "organization_id")),
  CHECK (check_reference('organization', "user_organization_id")),
  CHECK (check_reference('organization', "module_id"))
); -- Коррекция населенного пункта

CREATE INDEX "city_correction_object_id_index" ON "city_correction" ("object_id");
CREATE INDEX "city_correction_correction_index" ON "city_correction" ("correction");
CREATE INDEX "city_correction_start_date_index" ON "city_correction" ("start_date");
CREATE INDEX "city_correction_end_date_index" ON "city_correction" ("end_date");
CREATE INDEX "city_correction_organization_id_index" ON "city_correction" ("organization_id");
CREATE INDEX "city_correction_user_organization_id_index" ON "city_correction" ("user_organization_id");

CREATE TABLE "district" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта
  "parent_id" BIGINT, -- Идентификатор родительского объекта
   "parent_entity_id" BIGINT REFERENCES "entity", -- Идентификатор сущности родительского объекта: 400 - city
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Дата начала периода действия объекта
  "end_date" TIMESTAMP NULL DEFAULT NULL, -- Дата окончания периода действия объекта
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус
  "permission_id" BIGINT NOT NULL DEFAULT 0, -- Ключ прав доступа к объекту
  UNIQUE ("object_id","start_date"),
  CHECK (check_reference('city', "parent_id"))
); -- Район

CREATE SEQUENCE "district_object_id_sequence";

CREATE INDEX "district_object_id_index" ON "district" ("object_id");
CREATE INDEX "district_parent_id_index" ON "district" ("parent_id");
CREATE INDEX "district_start_date_index" ON "district" ("start_date");
CREATE INDEX "district_end_date_index" ON "district" ("end_date");
CREATE INDEX "district_status_index" ON "district" ("status");
CREATE INDEX "district_permission_id_index" ON "district" ("permission_id");

CREATE TABLE "district_attribute" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "attribute_id" BIGINT NOT NULL, -- Идентификатор атрибута
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта
  "entity_attribute_id" BIGINT NOT NULL REFERENCES "entity_attribute", -- Идентификатор типа атрибута
  "value_id" BIGINT, -- Идентификатор значения
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Дата начала периода действия атрибута
  "end_date" TIMESTAMP NULL DEFAULT NULL, -- Дата окончания периода действия атрибута
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус
  UNIQUE ("attribute_id", "object_id", "entity_attribute_id", "start_date"),
  CHECK (check_reference('district', "object_id"))
); -- Атрибуты района

CREATE INDEX "district_attribute_object_id_index" ON "district_attribute" ("object_id");
CREATE INDEX "district_attribute_entity_attribute_id_index" ON "district_attribute" ("entity_attribute_id");
CREATE INDEX "district_attribute_value_id_index" ON "district_attribute" ("value_id");
CREATE INDEX "district_attribute_start_date_index" ON "district_attribute" ("start_date");
CREATE INDEX "district_attribute_end_date_index" ON "district_attribute" ("end_date");
CREATE INDEX "district_attribute_status_index" ON "district_attribute" ("status");

CREATE TABLE "district_string_value" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "id" BIGINT NOT NULL, -- Идентификатор локализации
   "locale_id" BIGINT NOT NULL REFERENCES "locale", -- Идентификатор локали
  "value" VARCHAR, -- Текстовое значение
  UNIQUE ("id","locale_id")
); -- Локализация атрибутов района

CREATE SEQUENCE "district_string_value_id_sequence";

CREATE INDEX "district_string_value_value_index" ON "district_string_value" ("id", "value");

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (600, 1, 'Район'), (600, 2, 'Район');
INSERT INTO "entity"("id", "entity", "name_id") VALUES (600, 'district', 600);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (601, 1, UPPER('Наименование района')), (601, 2, UPPER('Найменування району'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (600, 600, true, 601, true, 0);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (602, 1, UPPER('Код района')), (602, 2, UPPER('Код району'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (601, 600, true, 602, true, 1);

CREATE TABLE "district_correction" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор коррекции
  "parent_id" BIGINT, -- Идентификатор объекта населенного пункта
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта района
  "external_id" BIGINT, -- Внешний идентификатор объекта
  "correction" VARCHAR NOT NULL, -- Название типа населенного пункта
  "start_date" TIMESTAMP NOT NULL DEFAULT NOW(), -- Дата начала актуальности соответствия
  "end_date" TIMESTAMP, -- Дата окончания актуальности соответствия
  "organization_id" BIGINT NOT NULL, -- Идентификатор организации
  "user_organization_id" BIGINT, -- Идентификатор организации пользователя
  "module_id" BIGINT, -- Идентификатор внутренней организации
  UNIQUE (external_id, organization_id, user_organization_id),
  CHECK (check_reference('city', "parent_id")),
  CHECK (check_reference('district', "object_id")),
  CHECK (check_reference('organization', "organization_id")),
  CHECK (check_reference('organization', "user_organization_id")),
  CHECK (check_reference('organization', "module_id"))
); -- Коррекция района

CREATE INDEX "district_correction_object_id_index" ON "district_correction" ("object_id");
CREATE INDEX "district_correction_correction_index" ON "district_correction" ("correction");
CREATE INDEX "district_correction_start_date_index" ON "district_correction" ("start_date");
CREATE INDEX "district_correction_end_date_index" ON "district_correction" ("end_date");
CREATE INDEX "district_correction_organization_id_index" ON "district_correction" ("organization_id");
CREATE INDEX "district_correction_user_organization_id_index" ON "district_correction" ("user_organization_id");

CREATE TABLE "street_type" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта
  "parent_id" BIGINT, -- Идентификатор родительского объекта: не используется
   "parent_entity_id" BIGINT REFERENCES "entity", -- Идентификатор сущности родительского объекта: не используется
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Дата начала периода действия объекта
  "end_date" TIMESTAMP NULL DEFAULT NULL, -- Дата окончания периода действия объекта
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус
  "permission_id" BIGINT NOT NULL DEFAULT 0, -- Ключ прав доступа к объекту
  UNIQUE ("object_id","start_date")
); -- Тип улицы

CREATE SEQUENCE "street_type_object_id_sequence";

CREATE INDEX "street_type_object_id_index" ON "street_type" ("object_id");
CREATE INDEX "street_type_start_date_index" ON "street_type" ("start_date");
CREATE INDEX "street_type_end_date_index" ON "street_type" ("end_date");
CREATE INDEX "street_type_status_index" ON "street_type" ("status");
CREATE INDEX "street_type_permission_id_index" ON "street_type" ("permission_id");

CREATE TABLE "street_type_attribute" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "attribute_id" BIGINT NOT NULL, -- Идентификатор атрибута
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта
  "entity_attribute_id" BIGINT NOT NULL REFERENCES "entity_attribute", -- Идентификатор типа атрибута
  "value_id" BIGINT, -- Идентификатор значения
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Дата начала периода действия атрибута
  "end_date" TIMESTAMP NULL DEFAULT NULL, -- Дата окончания периода действия атрибута
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус
  UNIQUE ("attribute_id", "object_id", "entity_attribute_id", "start_date"),
  CHECK (check_reference('street_type', "object_id"))
); -- Атрибуты типа улицы

CREATE INDEX "street_type_attribute_object_id_index" ON "street_type_attribute" ("object_id");
CREATE INDEX "street_type_attribute_entity_attribute_id_index" ON "street_type_attribute" ("entity_attribute_id");
CREATE INDEX "street_type_attribute_value_id_index" ON "street_type_attribute" ("value_id");
CREATE INDEX "street_type_attribute_start_date_index" ON "street_type_attribute" ("start_date");
CREATE INDEX "street_type_attribute_end_date_index" ON "street_type_attribute" ("end_date");
CREATE INDEX "street_type_attribute_status_index" ON "street_type_attribute" ("status");

CREATE TABLE "street_type_string_value" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "id" BIGINT NOT NULL, -- Идентификатор локализации
   "locale_id" BIGINT NOT NULL REFERENCES "locale", -- Идентификатор локали
  "value" VARCHAR, -- Текстовое значение
  UNIQUE ("id","locale_id")
); -- Локализация атрибутов типа улицы

CREATE SEQUENCE "street_type_string_value_id_sequence";

CREATE INDEX "street_type_string_value_value_index" ON "street_type_string_value" ("id", "value");

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (1400, 1, 'Тип улицы'), (1400, 2, 'Тип улицы');
INSERT INTO "entity"("id", "entity", "name_id") VALUES (1400, 'street_type', 1400);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (1401, 1, UPPER('Краткое название')), (1401, 2, UPPER('Краткое название'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (1400, 1400, true, 1401, true, 0);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (1402, 1, UPPER('Название')), (1402, 2, UPPER('Название'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (1401, 1400, true, 1402, true, 0);

CREATE TABLE "street_type_correction" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор коррекции
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта типа населенного пункта
  "external_id" BIGINT, -- Внешний идентификатор объекта
  "correction" VARCHAR NOT NULL, -- Название типа населенного пункта
  "start_date" TIMESTAMP NOT NULL DEFAULT NOW(), -- Дата начала актуальности соответствия
  "end_date" TIMESTAMP, -- Дата окончания актуальности соответствия
  "organization_id" BIGINT NOT NULL, -- Идентификатор организации
  "user_organization_id" BIGINT, -- Идентификатор организации пользователя
  "module_id" BIGINT, -- Идентификатор внутренней организации
  UNIQUE (external_id, organization_id, user_organization_id),
  CHECK (check_reference('street_type', "object_id")),
  CHECK (check_reference('organization', "organization_id")),
  CHECK (check_reference('organization', "user_organization_id")),
  CHECK (check_reference('organization', "module_id"))
); -- Коррекция типа улицы

CREATE INDEX "street_type_correction_object_id_index" ON "street_type_correction" ("object_id");
CREATE INDEX "street_type_correction_correction_index" ON "street_type_correction" ("correction");
CREATE INDEX "street_type_correction_start_date_index" ON "street_type_correction" ("start_date");
CREATE INDEX "street_type_correction_end_date_index" ON "street_type_correction" ("end_date");
CREATE INDEX "street_type_correction_organization_id_index" ON "street_type_correction" ("organization_id");
CREATE INDEX "street_type_correction_user_organization_id_index" ON "street_type_correction" ("user_organization_id");

CREATE TABLE "street" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта
  "parent_id" BIGINT, -- Идентификатор родительского объекта
   "parent_entity_id" BIGINT REFERENCES "entity", -- Идентификатор сущности родительского объекта: 400 - city
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Дата начала периода действия объекта
  "end_date" TIMESTAMP NULL DEFAULT NULL, -- Дата окончания периода действия объекта
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус
  "permission_id" BIGINT NOT NULL DEFAULT 0, -- Ключ прав доступа к объекту
  UNIQUE ("object_id","start_date"),
  CHECK (check_reference('city', "parent_id"))
); -- Улица

CREATE SEQUENCE "street_object_id_sequence";

CREATE INDEX "street_object_id_index" ON "street" ("object_id");
CREATE INDEX "street_parent_id_index" ON "street" ("parent_id");
CREATE INDEX "street_start_date_index" ON "street" ("start_date");
CREATE INDEX "street_end_date_index" ON "street" ("end_date");
CREATE INDEX "street_status_index" ON "street" ("status");
CREATE INDEX "street_permission_id_index" ON "street" ("permission_id");

CREATE TABLE "street_attribute" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "attribute_id" BIGINT NOT NULL, -- Идентификатор атрибута
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта
  "entity_attribute_id" BIGINT NOT NULL REFERENCES "entity_attribute", -- Идентификатор типа атрибута
  "value_id" BIGINT, -- Идентификатор значения
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Дата начала периода действия атрибута
  "end_date" TIMESTAMP NULL DEFAULT NULL, -- Дата окончания периода действия атрибута
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус
  UNIQUE ("attribute_id", "object_id", "entity_attribute_id", "start_date"),
  CHECK (check_reference('street', "object_id"))
); -- Атрибуты улицы

CREATE INDEX "street_attribute_object_id_index" ON "street_attribute" ("object_id");
CREATE INDEX "street_attribute_entity_attribute_id_index" ON "street_attribute" ("entity_attribute_id");
CREATE INDEX "street_attribute_value_id_index" ON "street_attribute" ("value_id");
CREATE INDEX "street_attribute_start_date_index" ON "street_attribute" ("start_date");
CREATE INDEX "street_attribute_end_date_index" ON "street_attribute" ("end_date");
CREATE INDEX "street_attribute_status_index" ON "street_attribute" ("status");

CREATE TABLE "street_string_value" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "id" BIGINT NOT NULL, -- Идентификатор локализации
   "locale_id" BIGINT NOT NULL REFERENCES "locale", -- Идентификатор локали
  "value" VARCHAR, -- Текстовое значение
  UNIQUE ("id","locale_id")
); -- Локализация атрибутов улицы

CREATE SEQUENCE "street_string_value_id_sequence";

CREATE INDEX "street_string_value_value_index" ON "street_string_value" ("id", "value");

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (300, 1, 'Улица'), (300, 2, 'Вулиця');
INSERT INTO "entity"("id", "entity", "name_id") VALUES (300, 'street', 300);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (301, 1, UPPER('Наименование улицы')), (301, 2, UPPER('Найменування вулиці'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (300, 300, true, 301, true, 0);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (302, 1, UPPER('Тип улицы')),(302, 2, UPPER('Тип улицы'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id", "reference_id") VALUES (301, 300, true, 302, true, 10, 1400);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (303, 1, UPPER('Код улицы')),(303, 2, UPPER('Код улицы'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (303, 300, false, 303, true, 1);

CREATE TABLE "street_correction" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор коррекции
  "parent_id" BIGINT, -- Идентификатор объекта населенного пункта
  "additional_parent_id" BIGINT, -- Идентификатор объекта типа улицы
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта улицы
  "external_id" BIGINT, -- Внешний идентификатор объекта
  "correction" VARCHAR NOT NULL, -- Название типа населенного пункта
  "start_date" TIMESTAMP NOT NULL DEFAULT NOW(), -- Дата начала актуальности соответствия
  "end_date" TIMESTAMP, -- Дата окончания актуальности соответствия
  "organization_id" BIGINT NOT NULL, -- Идентификатор организации
  "user_organization_id" BIGINT, -- Идентификатор организации пользователя
  "module_id" BIGINT, -- Идентификатор внутренней организации
  UNIQUE (external_id, organization_id, user_organization_id),
  CHECK (check_reference('city', "parent_id")),
  CHECK (check_reference('street', "object_id")),
  CHECK (check_reference('organization', "organization_id")),
  CHECK (check_reference('organization', "user_organization_id")),
  CHECK (check_reference('organization', "module_id"))
); -- Коррекция улицы

CREATE INDEX "street_correction_object_id_index" ON "street_correction" ("object_id");
CREATE INDEX "street_correction_correction_index" ON "street_correction" ("correction");
CREATE INDEX "street_correction_start_date_index" ON "street_correction" ("start_date");
CREATE INDEX "street_correction_end_date_index" ON "street_correction" ("end_date");
CREATE INDEX "street_correction_organization_id_index" ON "street_correction" ("organization_id");
CREATE INDEX "street_correction_user_organization_id_index" ON "street_correction" ("user_organization_id");

CREATE TABLE "building" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта
  "parent_id" BIGINT, -- Идентификатор родительского объекта
   "parent_entity_id" BIGINT REFERENCES "entity", -- Идентификатор сущности родительского объекта
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Дата начала периода действия объекта
  "end_date" TIMESTAMP NULL DEFAULT NULL, -- Дата окончания периода действия объекта
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус
  "permission_id" BIGINT NOT NULL DEFAULT 0, -- Ключ прав доступа к объекту
  UNIQUE ("object_id","start_date"),
  CHECK (check_reference('street', "parent_id"))
); -- Дом

CREATE SEQUENCE "building_object_id_sequence";

CREATE INDEX "building_object_id_index" ON "building" ("object_id");
CREATE INDEX "building_parent_id_index" ON "building" ("parent_id");
CREATE INDEX "building_start_date_index" ON "building" ("start_date");
CREATE INDEX "building_end_date_index" ON "building" ("end_date");
CREATE INDEX "building_status_index" ON "building" ("status");
CREATE INDEX "building_permission_id_index" ON "building" ("permission_id");

CREATE TABLE "building_attribute" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "attribute_id" BIGINT NOT NULL, -- Идентификатор атрибута
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта
  "entity_attribute_id" BIGINT NOT NULL REFERENCES "entity_attribute", -- Идентификатор типа атрибута
  "value_id" BIGINT, -- Идентификатор значения
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Дата начала периода действия атрибута
  "end_date" TIMESTAMP NULL DEFAULT NULL, -- Дата окончания периода действия атрибута
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус
  UNIQUE ("attribute_id", "object_id", "entity_attribute_id", "start_date"),
  CHECK (check_reference('building', "object_id"))
  ); -- Атрибуты дома

CREATE INDEX "building_attribute_object_id_index" ON "building_attribute" ("object_id");
CREATE INDEX "building_attribute_entity_attribute_id_index" ON "building_attribute" ("entity_attribute_id");
CREATE INDEX "building_attribute_value_id_index" ON "building_attribute" ("value_id");
CREATE INDEX "building_attribute_start_date_index" ON "building_attribute" ("start_date");
CREATE INDEX "building_attribute_end_date_index" ON "building_attribute" ("end_date");
CREATE INDEX "building_attribute_status_index" ON "building_attribute" ("status");

CREATE TABLE "building_string_value" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "id" BIGINT NOT NULL, -- Идентификатор локализации
   "locale_id" BIGINT NOT NULL REFERENCES "locale", -- Идентификатор локали
  "value" VARCHAR, -- Текстовое значение
  UNIQUE ("id","locale_id")
); -- Локализация атрибутов дома

CREATE SEQUENCE "building_string_value_id_sequence";

CREATE INDEX "building_string_value_value_index" ON "building_string_value" ("id", "value");

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (500, 1, 'Дом'), (500, 2, 'Будинок');
INSERT INTO "entity"("id", "entity", "name_id") VALUES (500, 'building', 500);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (501, 1, UPPER('Номер дома')), (501, 2, UPPER('Номер будинку'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (500, 500, true, 501, true, 0);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (502, 1, UPPER('Корпус')), (502, 2, UPPER('Корпус'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (501, 500, false, 502, true, 0);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (503, 1, UPPER('Строение')), (503, 2, UPPER('Будова'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (502, 500, false, 503, true, 0);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (504, 1, UPPER('Район')), (504, 2, UPPER('Район'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id", "reference_id") VALUES (503, 500, false, 504, true, 10, 600);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (505, 1, UPPER('Список кодов дома')), (505, 2, UPPER('Список кодов дома'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (504, 500, false, 505, true, 20);

CREATE TABLE "building_correction" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор коррекции
  "parent_id" BIGINT, -- Идентификатор объекта улица
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта дом
  "external_id" BIGINT, -- Внешний идентификатор объекта
  "correction" VARCHAR NOT NULL, -- Номер дома
  "additional_correction" VARCHAR NOT NULL DEFAULT '', -- Корпус дома
  "start_date" TIMESTAMP NOT NULL DEFAULT NOW(), -- Дата начала актуальности соответствия
  "end_date" TIMESTAMP, -- Дата окончания актуальности соответствия
  "organization_id" BIGINT NOT NULL, -- Идентификатор организации
  "user_organization_id" BIGINT, -- Идентификатор организации пользователя
  "module_id" BIGINT, -- Идентификатор внутренней организации
  UNIQUE (external_id, organization_id, user_organization_id),
  CHECK (check_reference('street', "parent_id")),
  CHECK (check_reference('building', "object_id")),
  CHECK (check_reference('organization', "organization_id")),
  CHECK (check_reference('organization', "user_organization_id")),
  CHECK (check_reference('organization', "module_id"))
); -- Коррекция дома

CREATE INDEX "building_correction_object_id_index" ON "building_correction" ("object_id");
CREATE INDEX "building_correction_correction_index" ON "building_correction" ("correction");
CREATE INDEX "building_correction_start_date_index" ON "building_correction" ("start_date");
CREATE INDEX "building_correction_end_date_index" ON "building_correction" ("end_date");
CREATE INDEX "building_correction_organization_id_index" ON "building_correction" ("organization_id");
CREATE INDEX "building_correction_user_organization_id_index" ON "building_correction" ("user_organization_id");

CREATE TABLE "building_code" (
  "id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "organization_id" BIGINT NOT NULL, -- ID обслуживающей организации
  "code" BIGINT NOT NULL, -- Код дома для данной обслуживающей организации
  "building_id" BIGINT NOT NULL, -- ID дома
  CHECK (check_reference('organization', "organization_id")),
  CHECK (check_reference('building', "building_id"))
); -- Код дома;

CREATE INDEX "building_code_attribute_organization_id_index" ON "building_code" ("organization_id");
CREATE INDEX "building_code_attribute_code_index" ON "building_code" ("code");
CREATE INDEX "building_code_attribute_building_id_index" ON "building_code" ("building_id");

CREATE TABLE "apartment" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта
  "parent_id" BIGINT, -- Идентификатор родительского объекта
   "parent_entity_id" BIGINT REFERENCES "entity", -- Идентификатор сущности родительского объекта: 500 - building
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Дата начала периода действия объекта
  "end_date" TIMESTAMP NULL DEFAULT NULL, -- Дата окончания периода действия объекта
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус
  "permission_id" BIGINT NOT NULL DEFAULT 0, -- Ключ прав доступа к объекту
  UNIQUE ("object_id","start_date"),
  CHECK (check_reference('building', "parent_id"))
); -- Квартира

CREATE SEQUENCE "apartment_object_id_sequence";

CREATE INDEX "apartment_object_id_index" ON "apartment" ("object_id");
CREATE INDEX "apartment_parent_id_index" ON "apartment" ("parent_id");
CREATE INDEX "apartment_start_date_index" ON "apartment" ("start_date");
CREATE INDEX "apartment_end_date_index" ON "apartment" ("end_date");
CREATE INDEX "apartment_status_index" ON "apartment" ("status");
CREATE INDEX "apartment_permission_id_index" ON "apartment" ("permission_id");

CREATE TABLE "apartment_attribute" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "attribute_id" BIGINT NOT NULL, -- Идентификатор атрибута
  "object_id" BIGINT NOT NULL, -- Идентификатор объекта
  "entity_attribute_id" BIGINT NOT NULL REFERENCES "entity_attribute", -- Идентификатор типа атрибута
  "value_id" BIGINT, -- Идентификатор значения
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Дата начала периода действия атрибута
  "end_date" TIMESTAMP NULL DEFAULT NULL, -- Дата окончания периода действия атрибута
  "status" INTEGER NOT NULL DEFAULT 1, -- Статус
  UNIQUE ("attribute_id", "object_id", "entity_attribute_id", "start_date"),
  CHECK (check_reference('apartment', "object_id"))
); -- Атрибуты квартиры

CREATE INDEX "apartment_attribute_object_id_index" ON "apartment_attribute" ("object_id");
CREATE INDEX "apartment_attribute_entity_attribute_id_index" ON "apartment_attribute" ("entity_attribute_id");
CREATE INDEX "apartment_attribute_value_id_index" ON "apartment_attribute" ("value_id");
CREATE INDEX "apartment_attribute_start_date_index" ON "apartment_attribute" ("start_date");
CREATE INDEX "apartment_attribute_end_date_index" ON "apartment_attribute" ("end_date");
CREATE INDEX "apartment_attribute_status_index" ON "apartment_attribute" ("status");

CREATE TABLE "apartment_string_value" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "id" BIGINT NOT NULL, -- Идентификатор локализации
   "locale_id" BIGINT NOT NULL REFERENCES "locale", -- Идентификатор локали
  "value" VARCHAR, -- Текстовое значение
  UNIQUE ("id","locale_id")
); -- Локализация атрибутов квартиры;

CREATE SEQUENCE "apartment_string_value_id_sequence";

CREATE INDEX "apartment_string_value_value_index" ON "apartment_string_value" ("id", "value");

INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (100, 1, 'Квартира'), (100, 2, 'Квартира');
INSERT INTO "entity"("id", "entity", "name_id") VALUES (100, 'apartment', 100);
INSERT INTO "entity_string_value"("id", "locale_id", "value") VALUES (101, 1, UPPER('Номер квартиры')), (101, 2, UPPER('Номер квартири'));
INSERT INTO "entity_attribute"("id", "entity_id", "required", "name_id", "system", "value_type_id") VALUES (100, 100, true, 101, true, 0);
