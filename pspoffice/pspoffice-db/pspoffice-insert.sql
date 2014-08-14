
-- Person --
INSERT INTO `sequence` (`sequence_name`, `sequence_value`) VALUES ('person',1), ('person_string_culture',1);

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2000, 1, 'Персона'), (2000, 2, 'Персона');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (2000, 'person', 2000, '');
/* ФИО */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2001, 1, UPPER('Фамилия')), (2001, 2, UPPER('Прізвище'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2000, 2000, 1, 2001, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2002, 1, UPPER('Имя')), (2002, 2, UPPER('Ім\'я'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2001, 2000, 1, 2002, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2003, 1, UPPER('Отчество')), (2003, 2, UPPER('По батькові'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2002, 2000, 1, 2003, 1);
/* Идентификационный код  */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2004, 1, UPPER('Идентификационный код')), (2004, 2, UPPER('Идентификационный код'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2003, 2000, 0, 2004, 1);
/* Дата рождения */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2005, 1, UPPER('Дата рождения')), (2005, 2, UPPER('Дата нарождения'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2004, 2000, 1, 2005, 1);
/* Место рождения */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2006, 1, UPPER('Страна')), (2006, 2, UPPER('Страна'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2005, 2000, 0, 2006, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2007, 1, UPPER('Регион')), (2007, 2, UPPER('Регион'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2006, 2000, 0, 2007, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2008, 1, UPPER('Район')), (2008, 2, UPPER('Район'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2007, 2000, 0, 2008, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2009, 1, UPPER('Нас. пункт')), (2009, 2, UPPER('Місто'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2008, 2000, 0, 2009, 1);
/* Документ */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2010, 1, UPPER('Документ')), (2010, 2, UPPER('Документ'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2009, 2000, 1, 2010, 1);
/* Дата смерти  */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2014, 1, UPPER('Дата смерти')), (2014, 2, UPPER('Дата смерти'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2013, 2000, 0, 2014, 1);
/* Отношение к воинской службе */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2015, 1, UPPER('Отношение к воинской службе')), (2015, 2, UPPER('Відношення до військової служби'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2014, 2000, 0, 2015, 1);
/* Ссылки на детей до 16 лет */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2016, 1, UPPER('Дети до 16 лет')), (2016, 2, UPPER('Діти віком до 16 років'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2015, 2000, 0, 2016, 1);
/* Пол */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2017, 1, UPPER('Пол')), (2017, 2, UPPER('Стать'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2016, 2000, 1, 2017, 1);
/* Является ли гражданином Украины */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2021, 1, UPPER('Является гражданином Украины')), (2021, 2, UPPER('Громадянин України'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2020, 2000, 1, 2021, 1);
/* Обоснование изменения */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2022, 1, UPPER('Обоснование изменения')), (2022, 2, UPPER('Обоснование изменения'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2021, 2000, 0, 2022, 1);
/* ID пользователя, отредактировавшего персону */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2023, 1, UPPER('ID пользователя, отредактировавшего персону')), (2023, 2, UPPER('ID пользователя, отредактировавшего персону'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2022, 2000, 0, 2023, 1);
/* ID персоны в старой системе паспортного стола */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2024, 1, UPPER('ID персоны в старой системе паспортного стола')), (2024, 2, UPPER('ID персоны в старой системе паспортного стола'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2023, 2000, 0, 2024, 1);

INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2000, 2000, UPPER('last_name'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2001, 2001, UPPER('first_name'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2002, 2002, UPPER('middle_name'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2003, 2003, UPPER('string'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2004, 2004, UPPER('masked_date'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2005, 2005, UPPER('string'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2006, 2006, UPPER('string'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2007, 2007, UPPER('string'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2008, 2008, UPPER('string'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2009, 2009, UPPER('document'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2013, 2013, UPPER('masked_date'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2014, 2014, 'military_service_relation');
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2015, 2015, 'person');
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2016, 2016, UPPER('gender'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2020, 2020, UPPER('boolean'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2021, 2021, UPPER('big_string'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2022, 2022, UPPER('integer'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2023, 2023, UPPER('string'));

-- Apartment Card --
INSERT INTO `sequence` (`sequence_name`, `sequence_value`) VALUES ('apartment_card',1), ('apartment_card_string_culture',1);

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2400, 1, 'Поквартирная карточка'), (2400, 2, 'Поквартирная карточка');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (2400, 'apartment_card', 2400, '');
/* Собственник */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2402, 1, UPPER('Собственник')), (2402, 2, UPPER('Собственник'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2401, 2400, 1, 2402, 1);
/* Адрес */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2403, 1, UPPER('Адрес')), (2403, 2, UPPER('Адрес'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2402, 2400, 1, 2403, 1);
/* Форма собственности */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2404, 1, UPPER('Форма собственности')), (2404, 2, UPPER('Форма собственности'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2403, 2400, 1, 2404, 1);
/* Документ права на жилплощадь */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2405, 1, UPPER('Документ права на жилплощадь')), (2405, 2, UPPER('Документ права на жилплощадь'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2404, 2400, 0, 2405, 1);
/* Ссылка на регистрации */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2406, 1, UPPER('Зарегистрированные')), (2406, 2, UPPER('Зарегистрированные'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2405, 2400, 0, 2406, 1);
/* Обоснование изменения */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2407, 1, UPPER('Обоснование изменения')), (2407, 2, UPPER('Обоснование изменения'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2406, 2400, 0, 2407, 1);
/* ID пользователя, отредактировавшего карточку */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2408, 1, UPPER('ID пользователя, отредактировавшего карточку')), (2408, 2, UPPER('ID пользователя, отредактировавшего карточку'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2407, 2400, 0, 2408, 1);
/* ID поквартирной карточки в старой системе паспортного стола */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2409, 1, UPPER('ID поквартирной карточки в старой системе паспортного стола')), (2409, 2, UPPER('ID поквартирной карточки в старой системе паспортного стола'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2408, 2400, 0, 2409, 1);

INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2401, 2401, 'person');
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2402, 2402, 'room');
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2403, 2402, 'apartment');
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2404, 2402, 'building');
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2405, 2403, 'ownership_form');
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2406, 2404, UPPER('big_string'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2407, 2405, 'registration');
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2408, 2406, UPPER('big_string'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2409, 2407, UPPER('integer'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2410, 2408, UPPER('string'));

-- Registration --
INSERT INTO `sequence` (`sequence_name`, `sequence_value`) VALUES ('registration',1), ('registration_string_culture',1);

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2100, 1, 'Регистрация'), (2100, 2, 'Регистрация');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (2100, 'registration', 2100, '');
/* Адрес прибытия */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2101, 1, UPPER('Страна')), (2101, 2, UPPER('Страна'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2100, 2100, 0, 2101, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2102, 1, UPPER('Регион')), (2102, 2, UPPER('Регион'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2101, 2100, 0, 2102, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2103, 1, UPPER('Район')), (2103, 2, UPPER('Район'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2102, 2100, 0, 2103, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2104, 1, UPPER('Нас. пункт')), (2104, 2, UPPER('Місто'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2103, 2100, 0, 2104, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2105, 1, UPPER('Улица')), (2105, 2, UPPER('Улица'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2104, 2100, 0, 2105, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2106, 1, UPPER('Дом №')), (2106, 2, UPPER('Дом №'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2105, 2100, 0, 2106, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2107, 1, UPPER('Корп.')), (2107, 2, UPPER('Корп.'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2106, 2100, 0, 2107, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2108, 1, UPPER('Кв.')), (2108, 2, UPPER('Кв.'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2107, 2100, 0, 2108, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2109, 1, UPPER('Дата прибытия')), (2109, 2, UPPER('Дата прибытия'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2108, 2100, 0, 2109, 1);
/* Адрес выбытия */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2110, 1, UPPER('Страна')), (2110, 2, UPPER('Страна'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2109, 2100, 0, 2110, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2111, 1, UPPER('Регион')), (2111, 2, UPPER('Регион'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2110, 2100, 0, 2111, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2112, 1, UPPER('Район')), (2112, 2, UPPER('Район'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2111, 2100, 0, 2112, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2113, 1, UPPER('Нас. пункт')), (2113, 2, UPPER('Місто'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2112, 2100, 0, 2113, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2114, 1, UPPER('Улица')), (2114, 2, UPPER('Улица'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2113, 2100, 0, 2114, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2115, 1, UPPER('Дом №')), (2115, 2, UPPER('Дом №'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2114, 2100, 0, 2115, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2116, 1, UPPER('Корп.')), (2116, 2, UPPER('Корп.'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2115, 2100, 0, 2116, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2117, 1, UPPER('Кв.')), (2117, 2, UPPER('Кв.'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2116, 2100, 0, 2117, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2118, 1, UPPER('Дата выбытия')), (2118, 2, UPPER('Дата вибуття'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2117, 2100, 0, 2118, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2119, 1, UPPER('Причина выбытия')), (2119, 2, UPPER('Причина вибуття'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2118, 2100, 0, 2119, 1);
/* Дата регистрации */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2120, 1, UPPER('Дата начала регистрации')), (2120, 2, UPPER('Дата начала регистрации'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2119, 2100, 1, 2120, 1);
/* Тип регистрации */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2121, 1, UPPER('Тип регистрации')), (2121, 2, UPPER('Тип регистрации'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2120, 2100, 1, 2121, 1);
/* Отношение к владельцу */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2122, 1, UPPER('Отношение к владельцу')), (2122, 2, UPPER('Отношение к владельцу'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2121, 2100, 0, 2122, 1);
/* Ссылка на Person */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2123, 1, UPPER('Персона')), (2123, 2, UPPER('Персона'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2122, 2100, 1, 2123, 1);
/* Обоснование изменения */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2124, 1, UPPER('Обоснование изменения')), (2124, 2, UPPER('Обоснование изменения'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2123, 2100, 0, 2124, 1);
/* ID пользователя, отредактировавшего регистрацию */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2125, 1, UPPER('ID пользователя, отредактировавшего регистрацию')), (2125, 2, UPPER('ID пользователя, отредактировавшего регистрацию'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2124, 2100, 0, 2125, 1);

INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2100, 2100, UPPER('string'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2101, 2101, UPPER('string'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2102, 2102, UPPER('string'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2103, 2103, UPPER('string'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2104, 2104, UPPER('string'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2105, 2105, UPPER('string'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2106, 2106, UPPER('string'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2107, 2107, UPPER('string'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2108, 2108, UPPER('masked_date'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2109, 2109, UPPER('string'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2110, 2110, UPPER('string'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2111, 2111, UPPER('string'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2112, 2112, UPPER('string'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2113, 2113, UPPER('string'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2114, 2114, UPPER('string'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2115, 2115, UPPER('string'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2116, 2116, UPPER('string'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2117, 2117, UPPER('masked_date'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2118, 2118, UPPER('string'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2119, 2119, UPPER('masked_date'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2120, 2120, 'registration_type');
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2121, 2121, 'owner_relationship');
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2122, 2122, 'person');
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2123, 2123, UPPER('big_string'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2124, 2124, UPPER('integer'));

-- Owner relationship --
INSERT INTO `sequence` (`sequence_name`, `sequence_value`) VALUES ('owner_relationship',1), ('owner_relationship_string_culture',1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2200, 1, 'Отношение к владельцу'), (2200, 2, 'Отношение к владельцу');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (2200, 'owner_relationship', 2200, '');
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2201, 1, UPPER('Наименование')), (2201, 2, UPPER('Наименование'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2200, 2200, 1, 2201, 1);
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2200, 2200, UPPER('string_culture'));

INSERT INTO `owner_relationship`(`object_id`) VALUES (1),(2);
INSERT INTO `owner_relationship_string_culture`(`id`, `locale_id`, `value`) VALUES
(1, 1, UPPER('сын')), (1, 2, UPPER('СИН')),
(2, 1, UPPER('дочь')), (2, 2, UPPER('ДОЧКА'));
INSERT INTO `owner_relationship_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`) VALUES
(1,1,2200,1,2200),(1,2,2200,2,2200);

-- Forms of ownerships -- 
INSERT INTO `sequence` (`sequence_name`, `sequence_value`) VALUES ('ownership_form',1), ('ownership_form_string_culture',1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2500, 1, 'Форма собственности'), (2500, 2, 'Форма власності');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (2500, 'ownership_form', 2500, '');
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2501, 1, UPPER('Название')), (2501, 2, UPPER('Назва'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2500, 2500, 1, 2501, 1);
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2500, 2500, UPPER('string_culture'));

-- Registration type -- 
INSERT INTO `sequence` (`sequence_name`, `sequence_value`) VALUES ('registration_type',1), ('registration_type_string_culture',1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2600, 1, 'Тип регистрации'), (2600, 2, 'Тип регистрации');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (2600, 'registration_type', 2600, '');
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2601, 1, UPPER('Название')), (2601, 2, UPPER('Назва'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2600, 2600, 1, 2601, 1);
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2600, 2600, UPPER('string_culture'));

INSERT INTO `registration_type`(`object_id`) VALUES (1);
INSERT INTO `registration_type_string_culture`(`id`, `locale_id`, `value`) VALUES 
(1, 1, UPPER('постоянная')), (1, 2, UPPER('постійна'));
INSERT INTO `registration_type_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`) VALUES
(1,1,2600,1,2600);

-- Document type --
INSERT INTO `sequence` (`sequence_name`, `sequence_value`) VALUES ('document_type',1), ('document_type_string_culture',1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2700, 1, 'Тип документа'), (2700, 2, 'Тип документа');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (2700, 'document_type', 2700, '');
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2701, 1, UPPER('Название')), (2701, 2, UPPER('Назва'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2700, 2700, 1, 2701, 1);
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2700, 2700, UPPER('string_culture'));

INSERT INTO `document_type`(`object_id`) VALUES (1),(2);
INSERT INTO `document_type_string_culture`(`id`, `locale_id`, `value`) VALUES (1, 1, UPPER('паспорт')),(1, 2, UPPER('ПАСПОРТ')),
(2, 1, UPPER('свидетельство о рождении')), (2, 2, UPPER('СВIДОЦТВО ПРО НАРОДЖЕННЯ'));
INSERT INTO `document_type_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`) VALUES
(1,1,2700,1,2700),(1,2,2700,2,2700);

-- Document --
INSERT INTO `sequence` (`sequence_name`, `sequence_value`) VALUES ('document',1), ('document_string_culture',1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2800, 1, 'Документ'), (2800, 2, 'Документ');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (2800, 'document', 2800, '');
/* Зарезервированные attribute ids для всех документов: 2800-2810 */
/* Ссылка на document_type */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2801, 1, UPPER('Тип документа')), (2801, 2, UPPER('Тип документа'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2800, 2800, 1, 2801, 1);
/* Серия документа */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2802, 1, UPPER('Серия')), (2802, 2, UPPER('Серія'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2801, 2800, 1, 2802, 1);
/* Номер документа */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2803, 1, UPPER('Номер')), (2803, 2, UPPER('Номер'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2802, 2800, 1, 2803, 1);
/* Кем выдан */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2804, 1, UPPER('Кем выдан')), (2804, 2, UPPER('Орган видачі'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2803, 2800, 0, 2804, 1);
/* Дата выдачи */
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2805, 1, UPPER('Дата выдачи')), (2805, 2, UPPER('Дата видачі'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2804, 2800, 0, 2805, 1);

INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2800, 2800, 'document_type');
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2801, 2801, UPPER('string'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2802, 2802, UPPER('string'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2803, 2803, UPPER('string'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2804, 2804, UPPER('masked_date'));

-- Military service relation --
INSERT INTO `sequence` (`sequence_name`, `sequence_value`) VALUES ('military_service_relation',1), ('military_service_relation_string_culture',1);

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2900, 1, 'Отношение к воинской обязанности'), (2900, 2, 'Отношение к воинской обязанности');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (2900, 'military_service_relation', 2900, '');
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2901, 1, UPPER('Название')), (2901, 2, UPPER('Назва'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2900, 2900, 1, 2901, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (2902, 1, UPPER('Код')), (2902, 2, UPPER('Код'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (2901, 2900, 0, 2902, 1);
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2900, 2900, UPPER('string_culture'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (2901, 2901, UPPER('integer'));

-- Departure reason --
INSERT INTO `sequence` (`sequence_name`, `sequence_value`) VALUES ('departure_reason',1), ('departure_reason_string_culture',1);

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (3000, 1, 'Причина выбытия'), (3000, 2, 'Причина вибуття');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (3000, 'departure_reason', 3000, '');
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (3001, 1, UPPER('Название')), (3001, 2, UPPER('Назва'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (3000, 3000, 1, 3001, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (3002, 1, UPPER('Код')), (3002, 2, UPPER('Код'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (3001, 3000, 1, 3002, 1);
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (3000, 3000, UPPER('string_culture'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (3001, 3001, UPPER('integer'));

-- Housing rights --
INSERT INTO `sequence` (`sequence_name`, `sequence_value`) VALUES ('housing_rights',1), ('housing_rights_string_culture',1);

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (3100, 1, 'Документ права на жилплощадь'), (3100, 2, 'Документ права на жилплощадь');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (3100, 'housing_rights', 3100, '');
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (3101, 1, UPPER('Название')), (3101, 2, UPPER('Назва'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (3100, 3100, 1, 3101, 1);
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (3102, 1, UPPER('Код')), (3102, 2, UPPER('Код'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (3101, 3100, 1, 3102, 1);
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (3100, 3100, UPPER('string_culture'));
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (3101, 3101, UPPER('integer'));

-- Current database version
 INSERT INTO `update` (`version`) VALUE ('20120704_420_0.0.2');