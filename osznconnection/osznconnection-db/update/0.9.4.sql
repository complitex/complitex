ALTER TABLE privilege_prolongation MODIFY COLUMN  `LGCODE` INTEGER(6) COMMENT 'Код услуги';

ALTER TABLE privilege_prolongation ADD COLUMN `DATEFORM` VARCHAR(8);
ALTER TABLE privilege_prolongation ADD COLUMN `CODORG` BIGINT(10);
ALTER TABLE privilege_prolongation ADD COLUMN `TARIF` DECIMAL(14, 4);

INSERT INTO `update` (`version`) VALUE ('0.9.4');
