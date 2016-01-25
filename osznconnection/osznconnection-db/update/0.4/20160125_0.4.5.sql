ALTER TABLE building_code MODIFY COLUMN code BIGINT(20) NOT NULL COMMENT 'Код дома для данной обслуживающей организации';

INSERT INTO `update` (`version`) VALUE ('20160125_0.4.5');