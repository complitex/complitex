delete from address_sync;

alter table address_sync rename domain_sync;

alter table domain_sync drop column object_id;
alter table domain_sync change column external_id external_id bigint(20);
alter table domain_sync change column servicing_organization servicing_organization bigint(20);
alter table domain_sync change column balance_holder balance_holder bigint(20);

alter table domain_sync change column name name VARCHAR(250);
alter table domain_sync change column additional_name additional_name VARCHAR(50);
alter table domain_sync change column alt_name alt_name VARCHAR(250);
alter table domain_sync change column alt_additional_name alt_additional_name VARCHAR(50);

alter table domain_sync add column parent_object_id bigint(20);
create index key_parent_object_id on domain_sync (parent_object_id);
alter table domain_sync add column status_detail integer;
create index key_status_detail on domain_sync (status_detail);

drop index unique_external_id on organization_type;
drop index unique_external_id on organization;
drop index unique_external_id on country;
drop index unique_external_id on region;
drop index unique_external_id on city_type;
drop index unique_external_id on city;
drop index unique_external_id on district;
drop index unique_external_id on street_type;
drop index unique_external_id on building;
drop index unique_external_id on building_address;
drop index unique_external_id on apartment;
drop index unique_external_id on user_info;
drop index unique_external_id on service;

alter table organization_type drop column external_id;
alter table organization drop column external_id;
alter table country drop column external_id;
alter table region drop column external_id;
alter table city_type drop column external_id;
alter table city drop column external_id;
alter table district drop column external_id;
alter table street_type drop column external_id;
alter table building drop column external_id;
alter table building_address drop column external_id;
alter table apartment drop column external_id;
alter table user_info drop column external_id;
alter table service drop column external_id;

alter table city_correction change column external_id external_id bigint(20);
alter table city_type_correction change column external_id external_id bigint(20);
alter table district_correction change column external_id external_id bigint(20);
alter table street_correction change column external_id external_id bigint(20);
alter table street_type_correction change column external_id external_id bigint(20);
alter table building_correction change column external_id external_id bigint(20);
alter table organization_correction change column external_id external_id bigint(20);
alter table service_correction change column external_id external_id bigint(20);

create unique index unique_external_id on city_correction (external_id, organization_id, user_organization_id);
create unique index unique_external_id on city_type_correction (external_id, organization_id, user_organization_id);
create unique index unique_external_id on district_correction (external_id, organization_id, user_organization_id);
create unique index unique_external_id on street_correction (external_id, organization_id, user_organization_id);
create unique index unique_external_id on street_type_correction (external_id, organization_id, user_organization_id);
create unique index unique_external_id on building_correction (external_id, organization_id, user_organization_id);
create unique index unique_external_id on organization_correction (external_id, organization_id, user_organization_id);
create unique index unique_external_id on service_correction (external_id, organization_id, user_organization_id);

alter table city_correction drop column `status`;
alter table city_type_correction drop column `status`;
alter table district_correction drop column `status`;
alter table street_correction drop column `status`;
alter table street_type_correction drop column `status`;
alter table building_correction drop column `status`;
alter table organization_correction drop column `status`;
alter table service_correction drop column `status`;

INSERT INTO `update` (`version`) VALUE ('20180110_0.7.0');