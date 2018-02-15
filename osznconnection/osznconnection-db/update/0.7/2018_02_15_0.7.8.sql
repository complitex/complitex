create index key_first_name on person_account (first_name);
create index key_middle_name on person_account (middle_name);
create index key_last_name on person_account (last_name);
create index key_city on person_account (city);
create index key_street_type on person_account (street_type);
create index key_street on person_account (street);
create index key_building_number on person_account (building_number);
create index key_building_corp on person_account (building_corp);
create index key_apartment on person_account (apartment);
create index key_pu_account_number on person_account (pu_account_number);

insert into `update` (`version`) VALUE ('20180215_0.7.8');