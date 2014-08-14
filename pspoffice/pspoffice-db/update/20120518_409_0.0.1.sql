-- Updates structure of person: from here on person's military service relation is reference to corresponding reference table.

SET GLOBAL log_bin_trust_function_creators = 1;

-- make military service relation's code attribute optional as import data doesn't contain code information.
UPDATE `entity_attribute_type` SET `mandatory` = 0 WHERE `id` = 2901;

-- make person's military service relation attribute point to corresponding reference table.
UPDATE `entity_attribute_value_type` SET `attribute_value_type` = 'military_service_relation' WHERE `id` = 2014;

DELIMITER /
CREATE FUNCTION `insertString_0.0.1`(p_locale_id BIGINT(20), p_value VARCHAR(1000)) RETURNS BIGINT(20)
BEGIN
    DECLARE l_strings_seq BIGINT(20);
    
    SELECT `sequence_value` INTO l_strings_seq FROM `sequence` WHERE `sequence_name` = 'military_service_relation_string_culture';
    INSERT INTO `military_service_relation_string_culture`(`id`, `locale_id`, `value`) 
		VALUES (l_strings_seq, p_locale_id, UPPER(p_value));
    UPDATE `sequence` SET `sequence_value` = (l_strings_seq+1) WHERE `sequence_name` = 'military_service_relation_string_culture';
    RETURN l_strings_seq;
END/
DELIMITER ;

DELIMITER /
CREATE FUNCTION `insertObject_0.0.1`(p_start_date TIMESTAMP) RETURNS BIGINT(20)
BEGIN
    DECLARE l_object_seq BIGINT(20);  
    
    SELECT `sequence_value` INTO l_object_seq FROM `sequence` WHERE `sequence_name` = 'military_service_relation';
    -- by default new military service relation object visible by all (i.e. permission_id = 0).
    INSERT INTO `military_service_relation`(`object_id`, `start_date`, `status`, `permission_id`) 
	VALUES (l_object_seq, p_start_date, 'ACTIVE', 0);
    UPDATE `sequence` SET `sequence_value` = (l_object_seq+1) WHERE `sequence_name` = 'military_service_relation';
    RETURN l_object_seq;
END/
DELIMITER ;

DELIMITER /
CREATE PROCEDURE `insertAttribute_0.0.1`(p_object_id BIGINT(20), p_start_date TIMESTAMP, p_string_id BIGINT(20))
BEGIN
    INSERT INTO `military_service_relation_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`, 
			`start_date`) 
    VALUES (1, p_object_id, 2900, p_string_id, 2900, p_start_date);
END/
DELIMITER ;

DELIMITER /
CREATE PROCEDURE `update_0.0.1`()
BEGIN
    DECLARE l_a_id BIGINT(20);
    DECLARE l_s_id BIGINT(20);
    DECLARE l_s_value BIGINT(20);
    DECLARE l_o_id BIGINT(20);
    DECLARE l_string_id BIGINT(20);
    DECLARE l_start_date TIMESTAMP;
    DECLARE l_locale_id BIGINT(20);
    DECLARE done INT;
    DECLARE l_count INT;
    
    DECLARE p_cursor CURSOR FOR 
	SELECT p.`start_date`, a.`pk_id`, s.`pk_id`, s.`value` FROM `person` p 
	JOIN `person_attribute` a ON (a.`attribute_type_id` = 2014 AND a.`object_id` = p.`object_id` AND a.`value_id` IS NOT NULL)
	LEFT JOIN `person_string_culture` s ON (a.`value_id` = s.`id` AND s.`locale_id` = 
		(SELECT `id` FROM `locales` WHERE `system` = 1));
	
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
    
    SET done = 0;
    
    -- figure out system locale.
    SELECT `id` INTO l_locale_id FROM `locales` WHERE `system` = 1;
    
    OPEN p_cursor;
    p_loop: LOOP
	FETCH p_cursor INTO l_start_date, l_a_id, l_s_id, l_s_value;

	IF done = 1 THEN
	    LEAVE p_loop;
	END IF;
	
	IF l_s_value IS NOT NULL AND TRIM(l_s_value) != '' THEN
	    SET l_s_value = TRIM(l_s_value);
	    SET l_o_id = NULL;
	    
	    -- check if mililtary_service_relation reference table already contains item with such name.	   
	    SELECT MIN(m.`object_id`) INTO l_o_id FROM `military_service_relation` m
		JOIN `military_service_relation_attribute` a ON (a.`attribute_type_id` = 2900 AND a.`object_id` = m.`object_id` 
			AND a.`status` = 'ACTIVE')
		JOIN `military_service_relation_string_culture` s ON (a.`value_id` = s.`id` AND s.`locale_id` = l_locale_id)
		WHERE m.`status` IN ('ACTIVE', 'INACTIVE') AND TRIM(s.`value`) = l_s_value;
	    
	    IF l_o_id IS NULL THEN
		-- there is no item with such name then create new one and insert.
		SELECT `insertObject_0.0.1`(l_start_date) INTO l_o_id; -- inserts military_service_relation object.
		SELECT `insertString_0.0.1`(l_locale_id, l_s_value) INTO `l_string_id`; -- inserts military_service_relation's name.
		CALL `insertAttribute_0.0.1`(l_o_id, l_start_date, l_string_id); -- inserts military_service_relation name attribute.
	    END IF;
	    
	    -- update person's military service relation attribute to point to current item.
	    UPDATE `person_attribute` SET `value_id` = l_o_id WHERE `pk_id` = l_a_id;
	END IF;
	
	-- delete old person's military service relation string.
	DELETE FROM `person_string_culture` WHERE `pk_id` = l_s_id;
    END LOOP p_loop;
    CLOSE p_cursor;
    SET done = 0;
    
    -- delete all person's military service relation attributes with value_id = NULL.
    DELETE FROM `person_attribute` WHERE `attribute_type_id` = 2014 AND `value_id` IS NULL;
END/
DELIMITER ;

CALL `update_0.0.1`();

DROP PROCEDURE `update_0.0.1`;
DROP FUNCTION `insertObject_0.0.1`;
DROP FUNCTION `insertString_0.0.1`;
DROP PROCEDURE `insertAttribute_0.0.1`;

INSERT INTO `update` (`version`) VALUE ('20120518_409_0.0.1');

SET GLOBAL log_bin_trust_function_creators = 0;