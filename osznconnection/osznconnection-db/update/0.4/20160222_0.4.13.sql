ALTER TABLE subsidy_tarif MODIFY COLUMN `T11_DATA_T` DATE;
ALTER TABLE subsidy_tarif MODIFY COLUMN `T11_DATA_E` DATE;
ALTER TABLE subsidy_tarif MODIFY COLUMN `T11_DATA_R` DATE;
ALTER TABLE subsidy_tarif MODIFY COLUMN `T11_NORM_U` DECIMAL(15, 4);
ALTER TABLE subsidy_tarif MODIFY COLUMN `T11_NOR_US` DECIMAL(15, 4);
ALTER TABLE subsidy_tarif MODIFY COLUMN `T11_CS_UNI` DECIMAL (15, 4);
ALTER TABLE subsidy_tarif MODIFY COLUMN `T11_NORM` DECIMAL (15, 4);
ALTER TABLE subsidy_tarif MODIFY COLUMN `T11_NRM_DO` DECIMAL (15, 4);
ALTER TABLE subsidy_tarif MODIFY COLUMN `T11_NRM_MA` DECIMAL (15, 4);
ALTER TABLE subsidy_tarif MODIFY COLUMN `T11_K_NADL` DECIMAL (15, 4);

INSERT INTO `update` (`version`) VALUE ('20160222_0.4.13');