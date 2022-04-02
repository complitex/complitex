CALL create_catalog(30, 'SURNAME'); -- Фамилия
CALL create_local_text(30, 1, 'SURNAME'); -- Фамилия

CALL create_catalog(31, 'GIVEN_NAME'); -- Имя
CALL create_local_text(31, 1, 'GIVEN_NAME'); -- Имя

CALL create_catalog(32, 'PATRONYMIC'); -- Отчество
CALL create_local_text(32, 1, 'PATRONYMIC'); -- Отчество

CALL create_catalog(34, 'ACCOUNT'); -- Счёт
CALL create_text(34, 1, 'ACCOUNT_NUMBER'); -- Номер счёта
CALL create_reference(34, 2, 27, 'HOUSE'); -- Дом
CALL create_reference(34, 3, 28, 'FLAT'); -- Квартира
CALL create_reference(34, 4, 30, 'SURNAME'); -- Фамилия
CALL create_reference(34, 5, 31, 'GIVEN_NAME'); -- Имя
CALL create_reference(34, 6, 32, 'PATRONYMIC'); -- Отчество

CALL create_catalog(35, 'SERVICE');  -- Услуга
CALL create_local_text(35, 1, 'SERVICE_NAME'); -- Название услуги

CALL create_catalog(36, 'PROVIDER_ACCOUNT'); -- Счёт поставщика услуг
CALL create_text(36, 1, 'ACCOUNT_NUMBER'); -- Номер счёта
CALL create_reference(36, 2, 34, 'ACCOUNT'); -- Счёт
CALL create_reference(36, 3, 11, 'ORGANIZATION'); -- Организация
CALL create_reference(36, 4, 35, 'SERVICE'); -- Услуга
CALL create_reference(36, 5, 30, 'SURNAME'); -- Фамилия
CALL create_reference(36, 6, 31, 'GIVEN_NAME'); -- Имя
CALL create_reference(36, 7, 32, 'PATRONYMIC'); -- Отчество


CALL create_catalog(130, 'SERVICE_CORRECTION'); -- Соответствие услуги
CALL create_reference(130, 1, 30, 'SERVICE'); -- Услуга
CALL create_reference(130, 2, 30, 'CATALOG_ORGANIZATION'); -- Организация каталога
CALL create_reference(130, 3, 30, 'CORRECTION_ORGANIZATION'); -- Организация соответствия
CALL create_text(130, 4, 'SERVICE_CODE'); -- Код услуги


