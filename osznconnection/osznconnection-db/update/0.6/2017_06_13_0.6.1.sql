DELIMITER //
CREATE PROCEDURE updateStatus(tbl_name VARCHAR(40))
  BEGIN
    SET @s = CONCAT('UPDATE ', tbl_name, ' SET `status` = \'0\' WHERE `status` = \'INACTIVE\';');
    PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

    SET @s = CONCAT('UPDATE ', tbl_name, ' SET `status` = \'1\' WHERE `status` = \'ACTIVE\';');
    PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

    SET @s = CONCAT('UPDATE ', tbl_name, ' SET `status` = \'2\' WHERE `status` = \'ARCHIVE\';');
    PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

    SET @s = CONCAT('UPDATE ', tbl_name, ' SET `status` = \'2\' WHERE `status` = \'ARCHIVE\';');
    PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

    SET @s = CONCAT('ALTER TABLE ', tbl_name, ' CHANGE COLUMN `status` `status` INTEGER NOT NULL DEFAULT 1 COMMENT \'Статус: INACTIVE(0), ACTIVE(1), ARCHIVE(2)\';');
    PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;
  END //
DELIMITER ;

CALL updateStatus('organization_type');
CALL updateStatus('organization_type_attribute');
CALL updateStatus('organization');
CALL updateStatus('organization_attribute');
CALL updateStatus('country');
CALL updateStatus('country_attribute');
CALL updateStatus('region');
CALL updateStatus('region_attribute');
CALL updateStatus('city_type');
CALL updateStatus('city_type_attribute');
CALL updateStatus('city');
CALL updateStatus('city_attribute');
CALL updateStatus('district');
CALL updateStatus('district_attribute');
CALL updateStatus('street_type');
CALL updateStatus('street_type_attribute');
CALL updateStatus('street');
CALL updateStatus('street_attribute');
CALL updateStatus('building');
CALL updateStatus('building_attribute');
CALL updateStatus('building_address');
CALL updateStatus('building_address_attribute');
CALL updateStatus('apartment');
CALL updateStatus('apartment_attribute');
CALL updateStatus('room');
CALL updateStatus('room_attribute');
CALL updateStatus('user_info');
CALL updateStatus('user_info_attribute');
CALL updateStatus('service');
CALL updateStatus('service_attribute');

CALL updateStatus('ownership');
CALL updateStatus('ownership_attribute');
CALL updateStatus('privilege');
CALL updateStatus('privilege_attribute');

DROP PROCEDURE updateStatus;

INSERT INTO `update` (`version`) VALUE ('20170613_0.6.1');

