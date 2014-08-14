 -- Report
CREATE TABLE `report`(
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `markup` TEXT NOT NULL,
  `updated` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY(id),
  KEY `key_name` (`name`),
  KEY `key_updated` (`updated`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Report SQL
CREATE TABLE `report_sql`(
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `report_id` BIGINT(20) NOT NULL,
  `sql` TEXT NOT NULL,
  PRIMARY KEY(id),
  KEY `key_report_id` (`report_id`),
  CONSTRAINT `fk_report_sql__report` FOREIGN KEY (`report_id`) REFERENCES `report` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;

INSERT INTO `update` (`version`) VALUE ('20120704_420_0.0.2');