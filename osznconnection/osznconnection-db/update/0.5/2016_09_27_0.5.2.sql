update request_file_description set request_file_type = 'FACILITY_STREET_TYPE_REFERENCE' where request_file_type = 'FACILITY_STREET_TYPE';
update request_file_description set request_file_type = 'FACILITY_STREET_REFERENCE' where request_file_type = 'FACILITY_STREET';
update request_file_description set request_file_type = 'FACILITY_TARIF_REFERENCE' where request_file_type = 'FACILITY_TARIF';
  
INSERT INTO `update` (`version`) VALUE ('20160927_0.5.2');