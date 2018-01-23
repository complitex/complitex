alter table city_correction drop column module_id;
alter table city_type_correction drop column module_id;
alter table district_correction drop column module_id;
alter table street_correction drop column module_id;
alter table street_type_correction drop column module_id;
alter table building_correction drop column module_id;
alter table organization_correction drop column module_id;
alter table service_correction drop column module_id;

alter table city_correction change column begin_date start_date DATETIME NOT NULL DEFAULT NOW() COMMENT 'Дата начала актуальности соответствия';
alter table city_type_correction change column begin_date start_date DATETIME NOT NULL DEFAULT NOW() COMMENT 'Дата начала актуальности соответствия';
alter table district_correction change column begin_date start_date DATETIME NOT NULL DEFAULT NOW() COMMENT 'Дата начала актуальности соответствия';
alter table street_correction change column begin_date start_date DATETIME NOT NULL DEFAULT NOW() COMMENT 'Дата начала актуальности соответствия';
alter table street_type_correction change column begin_date start_date DATETIME NOT NULL DEFAULT NOW() COMMENT 'Дата начала актуальности соответствия';
alter table building_correction change column begin_date start_date DATETIME NOT NULL DEFAULT NOW() COMMENT 'Дата начала актуальности соответствия';
alter table organization_correction change column begin_date start_date DATETIME NOT NULL DEFAULT NOW() COMMENT 'Дата начала актуальности соответствия';
alter table service_correction change column begin_date start_date DATETIME NOT NULL DEFAULT NOW() COMMENT 'Дата начала актуальности соответствия';

insert into `update` (`version`) VALUE ('20180123_0.7.5');