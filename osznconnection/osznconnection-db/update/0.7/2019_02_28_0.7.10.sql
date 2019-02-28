set @id = (select d.id from request_file_description d where request_file_type = 'SUBSIDY'
  and not exists(select name from request_file_field_description where name = 'MON'
    and request_file_description_id = d.id));

insert into request_file_field_description(request_file_description_id, name, type, length)
   value (@id, 'MON', 'java.lang.Integer', 1) ;

alter table subsidy add column `MON` integer(1);

INSERT INTO `update` (`version`) VALUE ('20190228_0.7.10');