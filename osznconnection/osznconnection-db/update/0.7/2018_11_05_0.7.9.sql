ALTER TABLE ownership_correction CHANGE COLUMN begin_date start_date DATE NOT NULL DEFAULT '1970-01-01' COMMENT 'Дата начала актуальности соответствия';
ALTER TABLE ownership_correction ADD KEY `key_start_date` (`start_date`);

ALTER TABLE privilege_correction CHANGE COLUMN begin_date start_date DATE NOT NULL DEFAULT '1970-01-01' COMMENT 'Дата начала актуальности соответствия';
ALTER TABLE privilege_correction DROP KEY key_begin_date;
ALTER TABLE privilege_correction ADD KEY `key_start_date` (`start_date`);

INSERT INTO `update` (`version`) VALUE ('20181105_0.7.9');

