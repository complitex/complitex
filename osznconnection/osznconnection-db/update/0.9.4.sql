ALTER TABLE privilege_prolongation MODIFY COLUMN  `LGCODE` INTEGER(6) COMMENT 'Код услуги';

# ALTER TABLE privilege_prolongation CHANGE COLUMN `DATEIN` `DATEIN_STR` VARCHAR(10);
# ALTER TABLE privilege_prolongation ADD COLUMN `DATEIN` DATE COMMENT 'Дата начала действия льготы' AFTER `DATEIN_STR`;
# UPDATE privilege_prolongation SET `DATEIN` = str_to_date(`DATEIN_STR`, '%Y-%m-%d') WHERE  id > 0;
# ALTER TABLE privilege_prolongation DROP COLUMN `DATEIN_STR`;
#
# ALTER TABLE privilege_prolongation CHANGE COLUMN `DATEOUT` `DATEOUT_STR` VARCHAR(10);
# ALTER TABLE privilege_prolongation ADD COLUMN `DATEOUT` DATE COMMENT 'Дата начала действия льготы' AFTER `DATEOUT_STR`;
# UPDATE privilege_prolongation SET `DATEOUT` = str_to_date(`DATEOUT_STR`, '%d.%m.%Y') WHERE  id > 0;
# ALTER TABLE privilege_prolongation DROP COLUMN `DATEOUT_STR`;

# ALTER TABLE privilege_prolongation CHANGE COLUMN `DATEIN` `DATEIN_DATE` DATE;
# ALTER TABLE privilege_prolongation ADD COLUMN `DATEIN` VARCHAR(10) COMMENT 'Дата начала действия льготы' AFTER `DATEIN_DATE`;
# UPDATE privilege_prolongation SET `DATEIN` = date_format(`DATEIN_DATE`, '%Y-%m-%d') WHERE  id > 0;
# ALTER TABLE privilege_prolongation DROP COLUMN `DATEIN_DATE`;
#
# ALTER TABLE privilege_prolongation CHANGE COLUMN `DATEOUT` `DATEOUT_DATE` DATE;
# ALTER TABLE privilege_prolongation ADD COLUMN `DATEOUT` VARCHAR(10) COMMENT 'Дата начала действия льготы' AFTER `DATEOUT_DATE`;
# UPDATE privilege_prolongation SET `DATEOUT` = date_format(`DATEOUT_DATE`, '%d.%m.%Y') WHERE  id > 0;
# ALTER TABLE privilege_prolongation DROP COLUMN `DATEOUT_DATE`;

ALTER TABLE privilege_prolongation ADD COLUMN `DATEFORM` VARCHAR(8);
ALTER TABLE privilege_prolongation ADD COLUMN `CODORG` BIGINT(10);
ALTER TABLE privilege_prolongation ADD COLUMN `TARIF` BIGINT(10);

INSERT INTO `update` (`version`) VALUE ('0.9.4');
