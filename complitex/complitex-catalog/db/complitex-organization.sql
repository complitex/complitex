CALL create_catalog(10, 'ORGANIZATION_TYPE'); -- Тип огранизации
CALL create_local_text(10, 1, 'ORGANIZATION_TYPE_NAME'); -- Название типа организации

CALL create_catalog(11, 'ORGANIZATION'); -- Организация
CALL create_reference(11, 1, 11,  'ORGANIZATION_PARENT'); -- Родительская организация
CALL create_reference(11, 2, 10, 'ORGANIZATION_TYPE'); -- Тип организации
CALL create_local_text(11, 3, 'ORGANIZATION_NAME'); -- Название организации
CALL create_local_text(11, 4, 'ORGANIZATION_SHORT_NAME'); -- Краткое название организации

DO $$
    DECLARE organization_catalog_id BIGINT;
    DECLARE organization_name_value_id BIGINT;
    BEGIN
        SELECT id INTO organization_catalog_id FROM catalog_view WHERE key_id = 11;

        SELECT id INTO organization_name_value_id FROM value_relevance(current_date)
        WHERE catalog_id = organization_catalog_id AND key_id = 3
          AND locale_id = (SELECT id FROM locale_view WHERE key_id = 1);

        INSERT INTO organization (id, catalog_id) VALUES (1, organization_catalog_id);
        INSERT INTO organization_data (organization_id, value_id, text) VALUES (1, organization_name_value_id, upper('Паспортный стол'));

        INSERT INTO organization (id, catalog_id) VALUES (2, organization_catalog_id);
        INSERT INTO organization_data (organization_id, value_id, text) VALUES (2, organization_name_value_id, upper('Модуль начисления'));
    END
$$;