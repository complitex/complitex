-- Set mysql user-defined variable - system locale id.
SELECT (@system_locale_id := `id`) FROM `locales` WHERE `system` = 1;

-- Servicing organizations
insert into organization(object_id) values (10),(11);
insert into organization_string_culture(id, locale_id, `value`) values 
(10,1,UPPER('Обсл. организация №1')), (10,2,UPPER('Обсл. организация №1')), (11,@system_locale_id, UPPER('10')), (12,@system_locale_id, UPPER('FALSE')),
(13,1,UPPER('Обсл. организация №2')), (13,2,UPPER('Обсл. организация №2')), (14,@system_locale_id, UPPER('11')), (15,@system_locale_id, UPPER('TRUE'));
insert into organization_attribute(attribute_id, object_id, attribute_type_id, value_id, value_type_id) values
(1,10,900,10,900), (1,10,901,11,901), (1,10,904,4,904), (1,10,922,12,922),
(1,11,900,13,900), (1,11,901,14,901), (1,11,904,4,904), (1,11,922,15,922);

-- Service provider and calculation module organizations
insert into organization(object_id) values (20),(21);
insert into organization_string_culture(id, locale_id, value) values 
(20, 1, UPPER('Service provider #1')), (20,2,UPPER('Service provider #1')), (21,@system_locale_id, UPPER('20')),
(22, 1, UPPER('Calculation module #1')), (22, 2, UPPER('Calculation module #1')), (23,@system_locale_id, UPPER('21'));
insert into organization_attribute(attribute_id, object_id, attribute_type_id, value_id, value_type_id) values
-- service providers:
(1,20,900,20,900), (1,20,901,21,901), (1,20,902,3,902), (1,20,904,5,904),
-- calculation modules:
(1,21,900,22,900), (1,21,901,23,901), (1,21,904,2,904);

-- User organizations
insert into organization(object_id) values (30), (31);
insert into organization_string_culture(id, locale_id, value) values 
(30,@system_locale_id, UPPER('User organization #1')),(31,@system_locale_id, UPPER('30')),
(32,@system_locale_id,UPPER('User organization #2')),(33,@system_locale_id, UPPER('31'));
insert into organization_attribute(attribute_id, object_id, attribute_type_id, value_id, value_type_id) values
(1,30,900,30,900), (1,30,901,31,901), (1,30,904,1,904),
(1,31,900,32,900), (1,31,901,33,901), (1,31,903,30,903), (1,31,904,1,904);