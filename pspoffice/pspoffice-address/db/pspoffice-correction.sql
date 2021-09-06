CALL create_catalog(111, 'ORGANIZATION_CORRECTION'); -- Соответствие организации
CALL create_reference(111, 1, 11, 'ORGANIZATION'); -- Организация
CALL create_reference(111, 2, 11, 'CATALOG_ORGANIZATION'); -- Организация каталога
CALL create_reference(111, 3, 11, 'CORRECTION_ORGANIZATION'); -- Организация соответствия
CALL create_numeric(111, 4, 'ORGANIZATION_PARENT_ID'); -- Код родительской организации
CALL create_text(111, 5, 'ORGANIZATION_EDRPOU'); -- Код ЕДРПОУ
CALL create_numeric(111, 6, 'ORGANIZATION_ID'); -- Код организации
CALL create_text(111, 7, 'ORGANIZATION_CODE'); -- Дополнительный код организации
CALL create_local_text(111, 8, 'ORGANIZATION_NAME'); -- Название организации
CALL create_local_text(111, 9, 'ORGANIZATION_SHORT_NAME'); -- Краткое название организации
CALL create_timestamp(111, 10, 'SYNCHRONIZATION_DATE'); -- Дата синхронизации

CALL create_catalog(120, 'COUNTRY_CORRECTION'); -- Соответствие страны
CALL create_reference(120, 1, 20, 'COUNTRY'); -- Страна
CALL create_reference(120, 2, 11, 'CATALOG_ORGANIZATION'); -- Организация каталога
CALL create_reference(120, 3, 11, 'CORRECTION_ORGANIZATION'); -- Организация соответствия
CALL create_numeric(120, 4, 'COUNTRY_ID'); -- Код страны
CALL create_text(120, 5, 'COUNTRY_CODE'); -- Дополнительный код страны
CALL create_local_text(120, 6, 'COUNTRY_NAME'); -- Название страны
CALL create_timestamp(120, 7, 'SYNCHRONIZATION_DATE'); -- Дата синхронизации

CALL create_catalog(121, 'REGION_CORRECTION'); --Соответствие региона
CALL create_reference(121, 1, 21, 'REGION'); -- Регион
CALL create_reference(121, 2, 11, 'CATALOG_ORGANIZATION'); -- Организация каталога
CALL create_reference(121, 3, 11, 'CORRECTION_ORGANIZATION'); -- Организация соответствия
CALL create_numeric(121, 4, 'COUNTRY_ID'); -- Код страны
CALL create_numeric(121, 5, 'REGION_ID'); -- Код региона
CALL create_text(121, 6, 'REGION_CODE'); -- Дополнительный код региона
CALL create_local_text(121, 7, 'REGION_NAME'); -- Название региона
CALL create_timestamp(121, 8, 'SYNCHRONIZATION_DATE'); -- Дата синхронизации

CALL create_catalog(122, 'CITY_TYPE_CORRECTION'); -- Соответствие типа населённого пункта
CALL create_reference(122, 1, 22, 'CITY_TYPE'); -- Тип населённого пункта
CALL create_reference(122, 2, 11, 'CATALOG_ORGANIZATION'); -- Организация каталога
CALL create_reference(122, 3, 11, 'CORRECTION_ORGANIZATION'); -- Организация соответствия
CALL create_numeric(122, 4, 'CITY_TYPE_ID'); -- Код типа населённого пункта
CALL create_text(122, 5, 'CITY_TYPE_CODE'); -- Дополнительный код населённого пункта
CALL create_local_text(122, 6, 'CITY_TYPE_NAME'); -- Название типа населённого пункта
CALL create_local_text(122, 7, 'CITY_TYPE_SHORT_NAME'); -- Название типа населённого пункта
CALL create_timestamp(122, 8, 'SYNCHRONIZATION_DATE'); -- Дата синхронизации

CALL create_catalog(123, 'CITY_CORRECTION'); -- Соответствие населённого пункта
CALL create_reference(123, 1, 23, 'CITY'); -- Населённый пункт
CALL create_reference(123, 2, 11, 'CATALOG_ORGANIZATION'); -- Организация каталога
CALL create_reference(123, 3, 11, 'CORRECTION_ORGANIZATION'); -- Организация соответствия
CALL create_numeric(123, 4, 'REGION_ID'); -- Код региона
CALL create_numeric(123, 5, 'CITY_TYPE_ID'); -- Код типа населённого пункта
CALL create_numeric(123, 6, 'CITY_ID'); -- Код населённого пункта
CALL create_text(123, 7, 'CITY_CODE'); -- Дополнительный код населённого пункта
CALL create_local_text(123, 8, 'CITY_NAME'); -- Название населённого пункта
CALL create_timestamp(123, 9, 'SYNCHRONIZATION_DATE'); -- Дата синхронизации

CALL create_catalog(124, 'DISTRICT_CORRECTION'); -- Соответствие района
CALL create_reference(124, 1, 24, 'DISTRICT'); -- Район
CALL create_reference(124, 2, 11, 'CATALOG_ORGANIZATION'); -- Организация каталога
CALL create_reference(124, 3, 11, 'CORRECTION_ORGANIZATION'); -- Организация соответствия
CALL create_numeric(124, 4, 'CITY_ID'); -- Код населённого пункта
CALL create_numeric(124, 5, 'DISTRICT_ID'); -- Код района
CALL create_text(124, 6, 'DISTRICT_CODE'); -- Дополнительный код района
CALL create_local_text(124, 7, 'DISTRICT_NAME'); -- Название района
CALL create_timestamp(124, 8, 'SYNCHRONIZATION_DATE'); -- Дата синхронизации

CALL create_catalog(125, 'STREET_TYPE_CORRECTION'); -- Соответствие типа улицы
CALL create_reference(125, 1, 25, 'STREET_TYPE'); -- Тип улицы
CALL create_reference(125, 2, 11, 'CATALOG_ORGANIZATION'); -- Организация каталога
CALL create_reference(125, 3, 11, 'CORRECTION_ORGANIZATION'); -- Организация соответствия
CALL create_numeric(125, 4, 'STREET_TYPE_ID'); -- Код типа улицы
CALL create_text(125, 5, 'STREET_TYPE_CODE'); -- Дополнительный код типа улицы
CALL create_local_text(125, 6, 'STREET_TYPE_NAME'); -- Название типа улицы
CALL create_local_text(125, 7, 'STREET_TYPE_SHORT_NAME'); -- Краткое название типа улицы
CALL create_timestamp(125, 8, 'SYNCHRONIZATION_DATE'); -- Дата синхронизации

CALL create_catalog(126, 'STREET_CORRECTION'); -- Соответствие улицы
CALL create_reference(126, 1, 26, 'STREET');  -- Улица
CALL create_reference(126, 2, 11, 'CATALOG_ORGANIZATION'); -- Организация каталога
CALL create_reference(126, 3, 11, 'CORRECTION_ORGANIZATION'); -- Организация соответствия
CALL create_numeric(126, 4, 'CITY_ID'); -- Код населённого пункта
CALL create_numeric(126, 5, 'STREET_TYPE_ID'); -- Код типа улицы
CALL create_numeric(126, 6, 'STREET_ID'); -- Код улицы
CALL create_text(126, 7, 'STREET_CODE'); -- Дополнительный код улицы
CALL create_local_text(126, 8, 'STREET_NAME'); -- Название улицы
CALL create_timestamp(126, 9, 'SYNCHRONIZATION_DATE'); -- Дата синхронизации

CALL create_catalog(127, 'HOUSE_CORRECTION'); -- Соответствие дома
CALL create_reference(127, 1, 27, 'HOUSE'); -- Дом
CALL create_reference(127, 2, 11, 'CATALOG_ORGANIZATION'); -- Организация каталога
CALL create_reference(127, 3, 11, 'CORRECTION_ORGANIZATION'); -- Организация соответствия
CALL create_numeric(127, 4, 'DISTRICT_ID'); -- Код района
CALL create_numeric(127, 5, 'STREET_ID'); --Код улицы
CALL create_numeric(127, 6, 'HOUSE_ID'); -- Код дома
CALL create_text(127, 7, 'HOUSE_CODE'); -- Дополнительный код улицы
CALL create_local_text(127, 8, 'HOUSE_NUMBER'); -- Номер дома
CALL create_local_text(127, 9, 'HOUSE_PART'); -- Корпус дома
CALL create_numeric(127, 10, 'SERVICING_ORGANIZATION_ID'); -- Код обслуживающей организации
CALL create_numeric(127, 11, 'BALANCE_HOLDER_ID'); -- Код балансодержателя
CALL create_timestamp(127, 12, 'SYNCHRONIZATION_DATE'); -- Дата синхронизации

CALL create_catalog(128, 'FLAT_CORRECTION'); -- Соответствие квартиры
CALL create_reference(128, 1, 28, 'FLAT'); -- Квартира
CALL create_reference(128, 2, 11, 'CATALOG_ORGANIZATION'); -- Организация каталога
CALL create_reference(128, 3, 11, 'CORRECTION_ORGANIZATION'); -- Организация соответствия
CALL create_numeric(128, 4, 'STREET_ID'); -- Код улицы
CALL create_numeric(128, 5, 'HOUSE_ID'); -- Код дома
CALL create_numeric(128, 6, 'FLAT_ID'); -- Код квартиры
CALL create_local_text(128, 7, 'FLAT_NUMBER'); -- Номер квартиры
CALL create_numeric(128, 8, 'SERVICING_ORGANIZATION_ID'); -- Код обслуживающей организации
CALL create_numeric(128, 9, 'BALANCE_HOLDER_ID'); -- Код балансодержателя
CALL create_timestamp(128, 10, 'SYNCHRONIZATION_DATE'); -- Дата синхронизации
