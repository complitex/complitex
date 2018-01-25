DROP TABLE IF EXIST `subsidy_split`;

CREATE TABLE `subsidy_split` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор разбивки субсидии помесячно',
  `subsidy_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор субсидии',

  `DAT1` DATE COMMENT 'Дата начала периода',
  `DAT2` DATE COMMENT 'Дата конца периода',

  `SM1` DECIMAL(9,2),
  `SM2` DECIMAL(9,2),
  `SM3` DECIMAL(9,2),
  `SM4` DECIMAL(9,2),
  `SM5` DECIMAL(9,2),
  `SM6` DECIMAL(9,2),
  `SM7` DECIMAL(9,2),
  `SM8` DECIMAL(9,2),

  `SUMMA` DECIMAL(13,2),
  `NUMM` INTEGER(2),
  `SUBS` DECIMAL(13,2),

  PRIMARY KEY (`id`),
  KEY `key_subsidy_id` (`subsidy_id`),
  CONSTRAINT `fk_subsidy_split__subsidy` FOREIGN KEY (`subsidy_id`) REFERENCES `subsidy` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Разбивка судсидий помесячно';

insert into `update` (`version`) VALUE ('20180125_0.7.7');