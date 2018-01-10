delete from address_sync;

alter table address_sync rename domain_sync;

alter table domain_sync drop column object_id;
alter table domain_sync change column external_id external_id bigint(20);
alter table domain_sync change column servicing_organization servicing_organization bigint(20);
alter table domain_sync change column balance_holder balance_holder bigint(20);

alter table domain_sync add column status_detail integer;
create index key_status_detail on domain_sync (status_detail);

INSERT INTO `update` (`version`) VALUE ('20180110_0.7.0');