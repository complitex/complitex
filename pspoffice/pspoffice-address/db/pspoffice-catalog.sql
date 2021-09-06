CREATE TABLE "catalog" (
    "id" BIGSERIAL PRIMARY KEY,  -- Идентификатор
    "key_id" INT NOT NULL, -- Ключ
    "name" VARCHAR NOT NULL,  -- Название
    "begin" TIMESTAMP NOT NULL DEFAULT current_timestamp,  -- Начало
    "end" TIMESTAMP  -- Окончание
);  -- Справочник

CREATE INDEX catalog_index ON "catalog" ("key_id", "name", "begin", "end");
CREATE UNIQUE INDEX catalog_unique ON "catalog" ("key_id", ("end" IS NULL)) WHERE "end" IS NULL;

CREATE VIEW catalog_view AS SELECT "id", "key_id", "name" FROM "catalog" WHERE "end" IS NULL;


CREATE TABLE "type" (
    "id" BIGSERIAL PRIMARY KEY,  -- Идентификатор
    "key_id" INT NOT NULL, -- Ключ
    "name" VARCHAR NOT NULL,  -- Название
    "begin" TIMESTAMP NOT NULL DEFAULT current_timestamp,  -- Начало
    "end" TIMESTAMP  -- Окончание
);  -- Тип

CREATE INDEX type_index ON "type" ("key_id", "name", "begin", "end");
CREATE UNIQUE INDEX type_unique ON "type" ("key_id", ("end" IS NULL)) WHERE "end" IS NULL;

INSERT INTO "type" ("key_id", "name") VALUES (1, 'NUMERIC');
INSERT INTO "type" ("key_id", "name") VALUES (2, 'TEXT');
INSERT INTO "type" ("key_id", "name") VALUES (3, 'TIMESTAMP');
INSERT INTO "type" ("key_id", "name") VALUES (4, 'REFERENCE');


CREATE TABLE "locale" (
  "id" BIGSERIAL PRIMARY KEY,  -- Идентификатор
  "key_id" INT NOT NULL, -- Ключ
  "name" VARCHAR NOT NULL,  -- Название
  "begin" TIMESTAMP NOT NULL DEFAULT current_timestamp,  -- Начало
  "end" TIMESTAMP  -- Окончание
);  -- Локаль

CREATE INDEX locale_index ON "locale" ("key_id", "name", "begin", "end");
CREATE UNIQUE INDEX locale_unique ON "locale" ("key_id", ("end" IS NULL)) WHERE "end" IS NULL;

CREATE VIEW locale_view AS SELECT "id", "key_id", "name" FROM "locale" WHERE "end" IS NULL;

INSERT INTO "locale" ("key_id", "name") VALUES (1, 'RU');
INSERT INTO "locale" ("key_id", "name") VALUES (2, 'UA');


CREATE TABLE "value" (
    "id" BIGSERIAL PRIMARY KEY, -- Идентификатор
    "catalog_id" BIGINT NOT NULL REFERENCES "catalog" ON DELETE CASCADE,  -- Справочник
    "key_id" INT NOT NULL,  -- Ключ
    "type_id" BIGINT NOT NULL REFERENCES "type", -- Тип
    "locale_id" BIGINT REFERENCES "locale", -- Локаль
    "reference_catalog_id" BIGINT REFERENCES "catalog", -- Ссылка
    "name" VARCHAR NOT NULL, -- Название
    "begin" TIMESTAMP NOT NULL DEFAULT current_timestamp,  -- Начало
    "end" TIMESTAMP, -- Окончание
    "start_date" DATE,  -- Начало актуальности
    "end_date" DATE,  -- Окончание актуальности
    CONSTRAINT value_exclude EXCLUDE USING gist (
        int8range("catalog_id", "catalog_id", '[]') WITH =,
        int4range("key_id", "key_id", '[]') WITH =,
        int8range("locale_id", "locale_id", '[]') WITH =,
        daterange("start_date", "end_date" , '[)') WITH &&)
        WHERE ("end" IS NULL)
);  -- Значение

CREATE INDEX value_index ON "value" ("catalog_id", "key_id", "type_id", "locale_id", "name", "begin", "end", "start_date", "end_date");

CREATE FUNCTION value_relevance (actual_date DATE)
    RETURNS TABLE ("id" BIGINT, "catalog_id" BIGINT, "key_id" INT, "type_id" BIGINT, "locale_id" BIGINT, "reference_catalog_id" BIGINT,
        "name" VARCHAR, "start_date" DATE, "end_date" DATE)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
        SELECT v."id", v."catalog_id",  v."key_id", v."type_id", v."locale_id", v."reference_catalog_id", v."name", v."start_date", v."end_date" FROM "value" v
            WHERE (v."start_date" IS NULL OR v."start_date" >= actual_date)
                AND (v."end_date" IS NULL OR v."end_date" <= actual_date)
                AND v."end" IS NULL;
END
$$;


CREATE FUNCTION check_data_reference(value_id BIGINT, reference_id BIGINT) RETURNS BOOLEAN
LANGUAGE plpgsql
AS $$
DECLARE table_name VARCHAR;
DECLARE checked BOOLEAN;
BEGIN
    IF (reference_id IS NULL)
    THEN
        RETURN true;
    END IF;

    SELECT lower(c."name") INTO table_name FROM "catalog" c
        LEFT JOIN "value" v on c.id = v.reference_catalog_id
    WHERE v.id = value_id;

    EXECUTE concat('SELECT count("id") > 0 FROM "', table_name, '" WHERE id = ', reference_id, '') INTO checked;

    RETURN checked;
END
$$;


CREATE FUNCTION check_data_relevance(table_name VARCHAR, data_id BIGINT, value_id BIGINT,
    data_index INT, data_start_date DATE, data_end_date DATE) RETURNS BOOLEAN
LANGUAGE plpgsql
AS $$
DECLARE checked BOOLEAN;
BEGIN
    IF (data_start_date > data_end_date)
    THEN
        RETURN false;
    END IF;

    EXECUTE concat('SELECT count("id") = 1
        FROM "', table_name, '_data"
        WHERE "id" = $1 AND "value_id" = $2 AND ($3 IS NULL OR index = $3)
            AND ($4 IS NULL OR "start_date" >= $4) AND ($5 IS NULL OR "end_date" <= $5)
            AND "end" IS NULL')
    USING data_id, value_id, data_index, data_start_date, data_end_date INTO checked;

    RETURN checked;
END
$$;


CREATE  OR REPLACE PROCEDURE create_catalog(catalog_key_id INT, catalog_name VARCHAR)
LANGUAGE plpgsql
AS $$
DECLARE table_name VARCHAR;
BEGIN
    EXECUTE concat('INSERT INTO "catalog" ("key_id", "name") VALUES (', catalog_key_id, ', upper(''', catalog_name, '''))');

    table_name := lower(catalog_name);

    EXECUTE concat('
        CREATE TABLE "', table_name, '" (
            "id" BIGSERIAL PRIMARY KEY,  -- Идентификатор
            "catalog_id" BIGINT NOT NULL REFERENCES "catalog" ON DELETE CASCADE, -- Справочник
            "begin" TIMESTAMP NOT NULL DEFAULT current_timestamp,  -- Начало
            "end" TIMESTAMP  -- Окончание
        ); -- Объект');

    EXECUTE concat('CREATE INDEX ', table_name, '_index ON "', table_name, '" ("catalog_id", "begin", "end")');
    EXECUTE concat('CREATE UNIQUE INDEX ', table_name, '_unique ON "', table_name, '" ("catalog_id", "end", ("end" IS NULL))');


    EXECUTE concat('
    CREATE TABLE "', table_name, '_data" (
        "id" BIGSERIAL PRIMARY KEY,  -- Идентификатор
        "', table_name, '_id" BIGINT NOT NULL REFERENCES "', table_name,'" ON DELETE CASCADE,  -- Объект
        "value_id" BIGINT NOT NULL REFERENCES "value" ON DELETE CASCADE,  -- Значение
        "index" INT, -- Индекс
        "numeric" NUMERIC, -- Число
        "text" TEXT, -- Текст
        "timestamp" TIMESTAMP, -- Время
        "reference_id" BIGINT, -- Ссылка
        "begin" TIMESTAMP NOT NULL DEFAULT current_timestamp,  -- Начало
        "end" TIMESTAMP,  -- Окончание
        "start_date" DATE,  -- Начало актуальности
        "end_date" DATE,  -- Окончание актуальности
        CONSTRAINT ', table_name, '_data_check CHECK (check_data_reference("value_id", "reference_id")),
        CONSTRAINT ', table_name, '_data_exclude EXCLUDE USING gist (
            int8range("', table_name, '_id", "', table_name, '_id", ''[]'') WITH =,
            int8range("value_id", "value_id", ''[]'') WITH =,
            int4range("index", "index", ''[]'') WITH =,
            daterange("start_date", "end_date", ''[)'') WITH &&)
            WHERE ("end" IS NULL)
    )');

    EXECUTE concat('CREATE INDEX ', table_name, '_data_', table_name, '_id_index ON "', table_name, '_data" ("', table_name,'_id")');
    EXECUTE concat('CREATE INDEX ', table_name, '_data_numeric_index ON "', table_name, '_data" ("value_id",  "numeric")');
    EXECUTE concat('CREATE INDEX ', table_name, '_data_text_index ON "', table_name, '_data" ("value_id",  "text")');
    EXECUTE concat('CREATE INDEX ', table_name, '_data_timestamp_index ON "', table_name, '_data" ("value_id",  "timestamp")');
    EXECUTE concat('CREATE INDEX ', table_name, '_data_reference_id_index ON "', table_name, '_data" ("value_id",  "reference_id")');
    EXECUTE concat('CREATE INDEX ', table_name, '_data_date_index ON "', table_name, '_data" ("begin", "end",  "start_date", "end_date")');

    EXECUTE concat('CREATE FUNCTION ', table_name, '_relevance (actual_date DATE)
        RETURNS TABLE ("id" BIGINT, "', table_name, '_id" BIGINT, "value_id" BIGINT, "index" INT,
            "numeric" NUMERIC, "text" TEXT, "timestamp" TIMESTAMP, "reference_id" BIGINT,
            "start_date" DATE, "end_date" DATE)
        LANGUAGE plpgsql
        AS $query$
        BEGIN
            RETURN QUERY
                SELECT d."id", d."', table_name, '_id", d."value_id", d."index", d."numeric", d."text", d."timestamp",
                     d."reference_id", d."start_date", d."end_date" FROM "', table_name, '_data" d
                         WHERE (d."start_date" IS NULL OR d."start_date" >= actual_date)
                           AND (d."end_date" IS NULL OR d."end_date" <= actual_date)
                           AND d."end" IS NULL;
        END
        $query$;');
END
$$;


CREATE  PROCEDURE create_value(catalog_key_id INT, value_key_id INT, type_key_id INT, locale_key_id INT, reference_catalog_key_id INT, value_name VARCHAR)
LANGUAGE plpgsql
AS $$
DECLARE catalog_id BIGINT;
DECLARE type_id BIGINT;
DECLARE locale_id BIGINT;
DECLARE reference_catalog_id BIGINT;
BEGIN
    SELECT "id" INTO catalog_id FROM "catalog" WHERE "key_id" = catalog_key_id AND "end" IS  NULL;
    SELECT "id" INTO type_id FROM "type" WHERE "key_id" = type_key_id AND "end" IS NULL;
    SELECT "id" INTO locale_id FROM "locale" WHERE "key_id" = locale_key_id AND "end" IS NULL;
    SELECT "id" INTO reference_catalog_id FROM "catalog" WHERE "key_id" = reference_catalog_key_id AND "end" IS  NULL;

    EXECUTE 'INSERT INTO "value" ("catalog_id", "key_id", "type_id", "locale_id", "reference_catalog_id", "name") VALUES ($1, $2, $3, $4, $5, upper($6))'
        USING catalog_id, value_key_id, type_id, locale_id, reference_catalog_id, value_name;
END
$$;


CREATE PROCEDURE create_numeric(catalog_key_id INT, value_key_id INT, value_name VARCHAR)
LANGUAGE plpgsql
AS $$
BEGIN
    CALL create_value(catalog_key_id, value_key_id, 1, null, null, value_name);
END
$$;


CREATE PROCEDURE create_text(catalog_key_id INT, value_key_id INT, value_name VARCHAR)
LANGUAGE plpgsql
AS $$
BEGIN
    CALL create_value(catalog_key_id, value_key_id, 2, null, null, value_name);
END
$$;


CREATE PROCEDURE create_local_text(catalog_key_id INT, value_key_id INT, value_name VARCHAR)
LANGUAGE plpgsql
AS $$
BEGIN
    CALL create_value(catalog_key_id, value_key_id, 2, 1, null, value_name);
    CALL create_value(catalog_key_id, value_key_id, 2, 2, null, value_name);
END
$$;


CREATE PROCEDURE create_timestamp(catalog_key_id INT, value_key_id INT, value_name VARCHAR)
LANGUAGE plpgsql
AS $$
BEGIN
    CALL create_value(catalog_key_id, value_key_id, 3, null, null, value_name);
END
$$;


CREATE PROCEDURE create_reference(catalog_key_id INT, value_key_id INT, reference_catalog_key_id INT, value_name VARCHAR)
LANGUAGE plpgsql
AS $$
BEGIN
    CALL create_value(catalog_key_id, value_key_id, 4, null, reference_catalog_key_id, value_name);
END
$$;


CREATE PROCEDURE add_item(catalog_key_id INT, INOUT item_id BIGINT)
LANGUAGE plpgsql
AS $$
DECLARE table_name VARCHAR;
    DECLARE catalog_id BIGINT;
BEGIN
    SELECT "id", lower("name") INTO catalog_id, table_name FROM "catalog" WHERE "key_id" = catalog_key_id AND "end" IS NULL;

    EXECUTE concat('INSERT INTO "', table_name, '" ("catalog_id") VALUES ($1) RETURNING "id"')
        USING catalog_id INTO item_id;
END
$$;


CREATE PROCEDURE add_text(catalog_key_id INT, item_id BIGINT, value_key_id INT, text TEXT, INOUT data_id BIGINT)
LANGUAGE plpgsql
AS $$
DECLARE table_name VARCHAR;
DECLARE value_catalog_id BIGINT;
DECLARE value_id BIGINT;
DECLARE value_type_id BIGINT;
BEGIN
    SELECT "id", lower("name") INTO value_catalog_id, table_name FROM "catalog" WHERE "key_id" = catalog_key_id AND "end" IS NULL;
    SELECT "id" INTO value_type_id FROM "type" WHERE "key_id" = 2 AND "end" IS NULL;
    SELECT "id" INTO value_id FROM "value" WHERE "catalog_id" = value_catalog_id AND "key_id" = value_key_id AND "type_id" = value_type_id AND "end" IS NULL;

    EXECUTE concat('INSERT INTO "', table_name, '_data" ("', table_name, '_id", "value_id", "text") VALUES ($1, $2, $3) RETURNING "id"')
        USING item_id, value_id, text INTO data_id;
END
$$;


CREATE PROCEDURE add_reference(catalog_key_id INT, item_id BIGINT, value_key_id INT, index INT, reference_id BIGINT, INOUT data_id BIGINT)
LANGUAGE plpgsql
AS $$
DECLARE table_name VARCHAR;
DECLARE value_catalog_id BIGINT;
DECLARE value_id BIGINT;
DECLARE value_type_id BIGINT;
BEGIN
    SELECT "id", lower("name") INTO value_catalog_id, table_name FROM "catalog" WHERE "key_id" = catalog_key_id AND "end" IS NULL;
    SELECT "id" INTO value_type_id FROM "type" WHERE "key_id" = 4 AND "end" IS NULL;
    SELECT "id" INTO value_id FROM "value" WHERE "catalog_id" = value_catalog_id AND "key_id" = value_key_id AND "type_id" = value_type_id AND "end" IS NULL;

    EXECUTE concat('INSERT INTO "', table_name, '_data" ("', table_name, '_id", "value_id", "index", "reference_id") VALUES ($1, $2, $3, $4) RETURNING "id"')
        USING item_id, value_id, index, reference_id INTO data_id;
END
$$;

