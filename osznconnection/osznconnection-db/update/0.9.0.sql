ALTER TABLE `payment` DROP CONSTRAINT `fk_payment__request_file`;
ALTER TABLE `payment` ADD CONSTRAINT `fk_payment__request_file` FOREIGN KEY (`request_file_id`) REFERENCES `request_file` (`id`) ON DELETE CASCADE;

ALTER TABLE `benefit` DROP CONSTRAINT `fk_benefit__request_file`;
ALTER TABLE `benefit` ADD CONSTRAINT `fk_benefit__request_file` FOREIGN KEY (`request_file_id`) REFERENCES `request_file` (`id`) ON DELETE CASCADE;

ALTER TABLE `actual_payment` DROP CONSTRAINT `fk_actual_payment__request_file`;
ALTER TABLE `actual_payment` ADD CONSTRAINT `fk_actual_payment__request_file` FOREIGN KEY (`request_file_id`) REFERENCES `request_file` (`id`) ON DELETE CASCADE;

ALTER TABLE `subsidy` DROP CONSTRAINT `fk_subsidy__request_file`;
ALTER TABLE `subsidy` ADD CONSTRAINT `fk_subsidy__request_file` FOREIGN KEY (`request_file_id`) REFERENCES `request_file` (`id`) ON DELETE CASCADE;

ALTER TABLE `dwelling_characteristics` DROP CONSTRAINT `fk_dwelling_characteristics__request_file`;
ALTER TABLE `dwelling_characteristics` ADD CONSTRAINT `fk_dwelling_characteristics__request_file` FOREIGN KEY (`request_file_id`) REFERENCES `request_file` (`id`) ON DELETE CASCADE;

ALTER TABLE `facility_service_type` DROP CONSTRAINT `fk_facility_service_type__request_file`;
ALTER TABLE `facility_service_type` ADD CONSTRAINT `fk_facility_service_type__request_file` FOREIGN KEY (`request_file_id`) REFERENCES `request_file` (`id`) ON DELETE CASCADE;

ALTER TABLE `facility_form2` DROP CONSTRAINT `fk_facility_form2__request_file`;
ALTER TABLE `facility_form2` ADD CONSTRAINT `fk_facility_form2__request_file` FOREIGN KEY (`request_file_id`) REFERENCES `request_file` (`id`) ON DELETE CASCADE;

ALTER TABLE `facility_local` DROP CONSTRAINT `fk_facility_local__request_file`;
ALTER TABLE `facility_local` ADD CONSTRAINT `fk_facility_local__request_file` FOREIGN KEY (`request_file_id`) REFERENCES `request_file` (`id`) ON DELETE CASCADE;

ALTER TABLE `privilege_prolongation` DROP CONSTRAINT `fk_privilege_prolongation__request_file`;
ALTER TABLE `privilege_prolongation` ADD CONSTRAINT `fk_privilege_prolongation__request_file` FOREIGN KEY (`request_file_id`) REFERENCES `request_file` (`id`) ON DELETE CASCADE;

ALTER TABLE `oschadbank_request_file` DROP CONSTRAINT `fk_oschadbank_request_file__request_file`;
ALTER TABLE `oschadbank_request_file` ADD CONSTRAINT `fk_oschadbank_request_file__request_file` FOREIGN KEY (`request_file_id`) REFERENCES `request_file` (`id`) ON DELETE CASCADE;

ALTER TABLE `oschadbank_request` DROP CONSTRAINT `fk_oschadbank_request__request_file`;
ALTER TABLE `oschadbank_request` ADD CONSTRAINT `fk_oschadbank_request__request_file` FOREIGN KEY (`request_file_id`) REFERENCES `request_file` (`id`) ON DELETE CASCADE;

ALTER TABLE `oschadbank_response` DROP CONSTRAINT `fk_oschadbank_responce__request_file`;
ALTER TABLE `oschadbank_response` ADD CONSTRAINT `fk_oschadbank_responce__request_file` FOREIGN KEY (`request_file_id`) REFERENCES `request_file` (`id`) ON DELETE CASCADE;

ALTER TABLE `oschadbank_response_file` DROP CONSTRAINT `fk_oschadbank_responce_file__request_file`;
ALTER TABLE `oschadbank_response_file` ADD CONSTRAINT `fk_oschadbank_responce_file__request_file` FOREIGN KEY (`request_file_id`) REFERENCES `request_file` (`id`) ON DELETE CASCADE;

ALTER TABLE `debt` DROP CONSTRAINT `fk_debt__request_file`;
ALTER TABLE `debt` ADD CONSTRAINT `fk_debt__request_file` FOREIGN KEY (`request_file_id`) REFERENCES `request_file` (`id`) ON DELETE CASCADE;

INSERT INTO `update` (`version`) VALUE ('0.9.0');
