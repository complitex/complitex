alter table `request_file` add column `user_id` BIGINT(20) COMMENT 'Идентификатор пользователя';
alter table `request_file` add key `key_user_id` (`user_id`);
alter table `request_file` add constraint `fk_request_file__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

INSERT INTO `update` (`version`) VALUE ('20170125_0.5.9');