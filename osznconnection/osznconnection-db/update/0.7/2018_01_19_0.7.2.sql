alter table city_correction change column correction correction VARCHAR(250);
alter table city_type_correction change column correction correction VARCHAR(250);
alter table district_correction change column correction correction VARCHAR(250);
alter table street_correction change column correction correction VARCHAR(250);
alter table street_type_correction change column correction correction VARCHAR(250);
alter table building_correction change column correction correction VARCHAR(250);
alter table organization_correction change column correction correction VARCHAR(250);
alter table service_correction change column correction correction VARCHAR(250);

alter table domain_sync drop column parent_object_id;

alter table domain_sync change column additional_parent_id additional_parent_id VARCHAR(16);

insert into `update` (`version`) VALUE ('20180119_0.7.2');