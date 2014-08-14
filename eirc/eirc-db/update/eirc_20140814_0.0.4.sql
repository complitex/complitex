SET autocommit=0;

-- --------------------------------
-- Current database version
-- --------------------------------
INSERT INTO `update` (`version`) VALUE ('eirc_20140814_0.0.4');

-- --------------------------------
-- Registry record indexes
-- --------------------------------

INSERT INTO `registry_status` (`code`, `name`) values
  (12, 'Частично обработан')
;

COMMIT;
SET autocommit=1;