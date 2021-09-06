CALL create_catalog(20, 'COUNTRY'); -- Страна
CALL create_local_text(20, 1, 'COUNTRY_NAME'); -- Название страны

CALL create_catalog(21, 'REGION'); -- Регион
CALL create_reference(21, 1, 20, 'COUNTRY'); -- Страна
CALL create_local_text(21, 2, 'REGION_NAME'); -- Название региона

CALL create_catalog(22, 'CITY_TYPE'); -- Тип неселённого пункта
CALL create_local_text(22, 1, 'CITY_TYPE_NAME'); -- Название типа населённого пункта
CALL create_local_text(22, 2, 'CITY_TYPE_SHORT_NAME'); -- Краткое название типа населённого пункта

CALL create_catalog(23, 'CITY'); -- Населённый пункт
CALL create_reference(23, 1, 21, 'REGION'); -- Регион
CALL create_reference(23, 2, 22, 'CITY_TYPE'); -- Тип населённого пункта
CALL create_local_text(23, 3, 'CITY_NAME'); -- Название населённого пункта

CALL create_catalog(24, 'DISTRICT'); -- Район
CALL create_reference(24, 1, 23, 'CITY'); -- Населённый пункт
CALL create_local_text(24, 2, 'DISTRICT_NAME'); -- Название района

CALL create_catalog(25, 'STREET_TYPE'); -- Тип улицы
CALL create_local_text(25, 1, 'STREET_TYPE_NAME'); -- Название типа улицы
CALL create_local_text(25, 2, 'STREET_TYPE_SHORT_NAME'); -- Краткое название типа улицы

CALL create_catalog(26, 'STREET'); -- Улица
CALL create_reference(26, 1, 23, 'CITY'); -- Населённый пункт
CALL create_reference(26, 2, 25, 'STREET_TYPE'); -- Тип улицы
CALL create_local_text(26, 3, 'STREET_NAME'); -- Название улицы

CALL create_catalog(27, 'HOUSE'); -- Дом
CALL create_reference(27, 1, 24, 'DISTRICT'); -- Район
CALL create_reference(27, 2, 26, 'STREET'); -- Улица
CALL create_local_text(27, 3, 'HOUSE_NUMBER'); -- Номер дома
CALL create_local_text(27, 4, 'HOUSE_PART'); -- Корпус дома

CALL create_catalog(28, 'FLAT'); -- Квартира
CALL create_reference(28, 1, 27, 'HOUSE'); -- Дом
CALL create_reference(28, 2, 26, 'STREET'); -- Улица
CALL create_local_text(28, 3, 'FLAT_NUMBER'); -- Номер квартиры
