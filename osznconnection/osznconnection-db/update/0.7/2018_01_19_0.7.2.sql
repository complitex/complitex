alter table domain_sync drop column parent_object_id;

insert into `update` (`version`) VALUE ('20180119_0.7.2');