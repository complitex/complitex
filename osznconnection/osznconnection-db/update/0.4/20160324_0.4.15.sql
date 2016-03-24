ALTER  TABLE facility_tarif_reference MODIFY COLUMN `TAR_CODE` BIGINT(10) COMMENT 'Код тарифа';
ALTER  TABLE facility_tarif_reference MODIFY COLUMN `TAR_CDPLG` BIGINT(10) COMMENT 'Код услуги';
ALTER  TABLE facility_tarif_reference MODIFY COLUMN `TAR_SERV` BIGINT(10) COMMENT 'Номер тарифа в услуге';
ALTER  TABLE facility_tarif_reference MODIFY COLUMN `TAR_DATEB` DATE COMMENT 'Дата начала действия тарифа';
ALTER  TABLE facility_tarif_reference MODIFY COLUMN `TAR_DATEE` DATE COMMENT 'Дата окончания действия тарифа';
ALTER  TABLE facility_tarif_reference MODIFY COLUMN `TAR_COEF` DECIMAL(11,2) COMMENT '';
ALTER  TABLE facility_tarif_reference MODIFY COLUMN `TAR_COST` DECIMAL(14,7) COMMENT 'Ставка тарифа';
ALTER  TABLE facility_tarif_reference MODIFY COLUMN `TAR_UNIT` BIGINT(10) COMMENT '';
ALTER  TABLE facility_tarif_reference MODIFY COLUMN `TAR_METER` INTEGER(3) COMMENT '';
ALTER  TABLE facility_tarif_reference MODIFY COLUMN `TAR_NMBAS` DECIMAL(11,2) COMMENT '';
ALTER  TABLE facility_tarif_reference MODIFY COLUMN `TAR_NMSUP` DECIMAL(11,2) COMMENT '';
ALTER  TABLE facility_tarif_reference MODIFY COLUMN `TAR_NMUBS` DECIMAL(22,6) COMMENT '';
ALTER  TABLE facility_tarif_reference MODIFY COLUMN `TAR_NMUSP` DECIMAL(22,6) COMMENT '';
ALTER  TABLE facility_tarif_reference MODIFY COLUMN `TAR_NMUMX` DECIMAL(11,4) COMMENT '';
ALTER  TABLE facility_tarif_reference MODIFY COLUMN `TAR_TPNMB` BIGINT(10) COMMENT '';
ALTER  TABLE facility_tarif_reference MODIFY COLUMN `TAR_TPNMS` BIGINT(10) COMMENT '';
ALTER  TABLE facility_tarif_reference MODIFY COLUMN `TAR_NMUPL` INTEGER(3) COMMENT '';
ALTER  TABLE facility_tarif_reference MODIFY COLUMN `TAR_PRIV` BIGINT(10) COMMENT '';


INSERT INTO `update` (`version`) VALUE ('20160324_0.4.15');