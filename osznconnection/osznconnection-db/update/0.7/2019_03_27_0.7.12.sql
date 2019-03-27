INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (933, 1, UPPER('Директория исходящих файлов запросов от ощадбанка')),
    (933, 2, UPPER('Директорія вихідних файлів запитів від ощадбанку'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (933, 900, 0, 933, 1, 1);

INSERT INTO `update` (`version`) VALUE ('20190327_0.7.12');