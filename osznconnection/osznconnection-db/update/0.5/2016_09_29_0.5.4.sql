delete from request_file where type in (1, 2) and group_id is null;

INSERT INTO `update` (`version`) VALUE ('20160929_0.5.4');