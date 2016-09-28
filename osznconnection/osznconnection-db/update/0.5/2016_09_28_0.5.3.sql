drop index `unique_person_account` on `person_account`;

alter table `person_account` add column `created` timestamp default current_timestamp comment 'Время создания';

INSERT INTO `update` (`version`) VALUE ('20160928_0.5.3');