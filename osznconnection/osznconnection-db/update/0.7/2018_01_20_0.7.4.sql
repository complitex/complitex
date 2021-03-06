alter table country_correction change column begin_date begin_date DATETIME NOT NULL DEFAULT NOW() COMMENT 'Дата начала актуальности соответствия';
alter table region_correction change column begin_date begin_date DATETIME NOT NULL DEFAULT NOW() COMMENT 'Дата начала актуальности соответствия';
alter table city_correction change column begin_date begin_date DATETIME NOT NULL DEFAULT NOW() COMMENT 'Дата начала актуальности соответствия';
alter table city_type_correction change column begin_date begin_date DATETIME NOT NULL DEFAULT NOW() COMMENT 'Дата начала актуальности соответствия';
alter table district_correction change column begin_date begin_date DATETIME NOT NULL DEFAULT NOW() COMMENT 'Дата начала актуальности соответствия';
alter table street_correction change column begin_date begin_date DATETIME NOT NULL DEFAULT NOW() COMMENT 'Дата начала актуальности соответствия';
alter table street_type_correction change column begin_date begin_date DATETIME NOT NULL DEFAULT NOW() COMMENT 'Дата начала актуальности соответствия';
alter table building_correction change column begin_date begin_date DATETIME NOT NULL DEFAULT NOW() COMMENT 'Дата начала актуальности соответствия';
alter table organization_correction change column begin_date begin_date DATETIME NOT NULL DEFAULT NOW() COMMENT 'Дата начала актуальности соответствия';
alter table service_correction change column begin_date begin_date DATETIME NOT NULL DEFAULT NOW() COMMENT 'Дата начала актуальности соответствия';

alter table country_correction change column end_date end_date DATETIME COMMENT 'Дата окончания актуальности соответствия';
alter table region_correction change column end_date end_date DATETIME COMMENT 'Дата окончания актуальности соответствия';
alter table city_correction change column end_date end_date DATETIME COMMENT 'Дата окончания актуальности соответствия';
alter table city_type_correction change column end_date end_date DATETIME COMMENT 'Дата окончания актуальности соответствия';
alter table district_correction change column end_date end_date DATETIME COMMENT 'Дата окончания актуальности соответствия';
alter table street_correction change column end_date end_date DATETIME COMMENT 'Дата окончания актуальности соответствия';
alter table street_type_correction change column end_date end_date DATETIME COMMENT 'Дата окончания актуальности соответствия';
alter table building_correction change column end_date end_date DATETIME COMMENT 'Дата окончания актуальности соответствия';
alter table organization_correction change column end_date end_date DATETIME COMMENT 'Дата окончания актуальности соответствия';
alter table service_correction change column end_date end_date DATETIME COMMENT 'Дата окончания актуальности соответствия';

alter table city_correction change column module_id module_id BIGINT(20) COMMENT 'Идентификатор модуля';
alter table city_type_correction change column module_id module_id BIGINT(20) COMMENT 'Идентификатор модуля';
alter table district_correction change column module_id module_id BIGINT(20) COMMENT 'Идентификатор модуля';
alter table street_correction change column module_id module_id BIGINT(20) COMMENT 'Идентификатор модуля';
alter table street_type_correction change column module_id module_id BIGINT(20) COMMENT 'Идентификатор модуля';
alter table building_correction change column module_id module_id BIGINT(20) COMMENT 'Идентификатор модуля';
alter table organization_correction change column module_id module_id BIGINT(20) COMMENT 'Идентификатор модуля';
alter table service_correction change column module_id module_id BIGINT(20) COMMENT 'Идентификатор модуля';

insert into `update` (`version`) VALUE ('20180122_0.7.4');