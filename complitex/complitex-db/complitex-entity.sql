CREATE TABLE "locale" (
    "id" BIGSERIAL PRIMARY KEY, -- Идентификатор локали
    "locale" VARCHAR NOT NULL, -- Код локали
    "system" BOOLEAN NOT NULL, -- Является ли локаль системной
    "alternative" BOOLEAN NOT NULL, -- Является ли локаль альтернативной
    UNIQUE ("locale")
    ); -- Локаль

INSERT INTO "locale" ("id", "locale", "system", "alternative") VALUES (1, 'RU', true, false);
INSERT INTO "locale" ("id", "locale", "system", "alternative") VALUES (2, 'UA', false, true);

CREATE FUNCTION "to_cyrillic" (str VARCHAR) RETURNS VARCHAR
LANGUAGE plpgsql
AS $$
  BEGIN IF str IS NULL THEN RETURN NULL; END IF;
    RETURN REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(
      REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE( str, 'a', 'а'), 'A', 'А'), 'T', 'Т'),
      'x', 'х'), 'X', 'Х'), 'k', 'к'), 'K', 'К'), 'M', 'М'), 'e', 'е'), 'E', 'Е'), 'o', 'о'), 'O', 'О'), 'p', 'р'), 'P', 'Р'),
      'c', 'с'), 'C', 'С'), 'B', 'В'), 'i', 'і'), 'I', 'І'), 'Ї', 'Є'), 'Ў', 'І'), 'ў', 'і'), '∙', 'ї'), '°', 'Ї');
  END
$$;

CREATE FUNCTION check_reference(table_name VARCHAR, object_id BIGINT) RETURNS BOOLEAN
LANGUAGE plpgsql
AS $$
DECLARE checked BOOLEAN;
BEGIN
    IF (object_id IS NULL)
    THEN
        RETURN true;
    END IF;

    EXECUTE concat('SELECT count(*) > 0 FROM "', table_name, '" WHERE object_id = ', object_id, '') INTO checked;

    RETURN checked;
END
$$;

CREATE TABLE "entity_string_value" (
  "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
  "id" BIGINT NOT NULL, -- Идентификатор локализации
  "locale_id" BIGINT NOT NULL REFERENCES "locale", -- Идентификатор локали
  "value" VARCHAR, -- Текстовое значение
  UNIQUE ("id", "locale_id")
); -- Локализация

CREATE SEQUENCE "entity_string_value_id_sequence";

CREATE TABLE "entity" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор сущности
  "entity" VARCHAR NOT NULL, -- Название сущности
  "name_id" BIGINT NOT NULL, -- Идентификатор локализации названия сущности
  UNIQUE (entity)
); --Сущность;

CREATE TABLE "entity_attribute" (
  "id" BIGSERIAL PRIMARY KEY, -- Идентификатор типа атрибута
  "entity_id" BIGINT NOT NULL REFERENCES "entity", -- Идентификатор сущности
  "name_id" BIGINT NOT NULL, -- Идентификатор локализации названия атрибута
  "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Дата начала периода действия типа атрибута
  "end_date" TIMESTAMP NULL DEFAULT NULL, -- Дата окончания периода действия типа атрибута
  "value_type_id" BIGINT, -- Тип значения атрибута
  "reference_id" BIGINT, -- Внешний ключ
  "system" BOOLEAN DEFAULT FALSE NOT NULL, -- Является ли тип атрибута системным
  "required" BOOLEAN DEFAULT FALSE NOT NULL -- Является ли атрибут обязательным
); -- Тип атрибута сущности

CREATE TABLE "entity_value_type" (
  "id" BIGINT NOT NULL PRIMARY KEY, -- Идентификатор типа значения атрибута
  "value_type" VARCHAR NOT NULL -- Тип значения атрибута
); --Тип значения атрибута

INSERT INTO entity_value_type ("id", "value_type") VALUES (0, 'string_value');
INSERT INTO entity_value_type ("id", "value_type") VALUES (1, 'string');
INSERT INTO entity_value_type ("id", "value_type") VALUES (2, 'boolean');
INSERT INTO entity_value_type ("id", "value_type") VALUES (3, 'decimal');
INSERT INTO entity_value_type ("id", "value_type") VALUES (4, 'integer');
INSERT INTO entity_value_type ("id", "value_type") VALUES (5, 'date');
INSERT INTO entity_value_type ("id", "value_type") VALUES (10, 'entity');
INSERT INTO entity_value_type ("id", "value_type") VALUES (20, 'building_code');
INSERT INTO entity_value_type ("id", "value_type") VALUES (21, 'last_name');
INSERT INTO entity_value_type ("id", "value_type") VALUES (22, 'first_name');
INSERT INTO entity_value_type ("id", "value_type") VALUES (23, 'middle_name');

CREATE TABLE "permission" (
    "pk_id" BIGSERIAL PRIMARY KEY, -- Суррогатный ключ
    "permission_id" BIGINT NOT NULL, -- Идентификатор права доступа
    "table" VARCHAR NOT NULL, -- Таблица
    "entity" VARCHAR NOT NULL, -- Сущность
    "object_id" BIGINT NOT NULL, -- Идентификатор объекта
    UNIQUE ("permission_id", "entity", "object_id")
); -- Права доступа

CREATE INDEX "permission_permission_id_index" ON "permission" ("permission_id");
CREATE INDEX "permission_table_index" ON "permission" ("table");
CREATE INDEX "permission_entity_index" ON "permission" ("entity");
CREATE INDEX "permission_object_id_index" ON "permission" ("object_id");

INSERT INTO "permission" ("permission_id", "table", "entity", "object_id") VALUES (0, 'ALL', 'ALL', 0);
