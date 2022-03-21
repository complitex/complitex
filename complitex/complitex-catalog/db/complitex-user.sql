CALL create_catalog(1, 'ROLE'); -- Роль
CALL create_text(1, 1, 'ROLE_NAME'); -- Название роли

CALL create_catalog(2, 'GROUP'); -- Группа
CALL create_text(2, 1, 'GROUP_NAME'); -- Название группы
CALL create_reference(2, 2, 1, 'ROLES');

CALL create_catalog(3, 'USER'); -- Пользователь
CALL create_text(3, 1, 'USERNAME'); -- Имя пользователя
CALL create_text(3, 2, 'PASSWORD'); -- Пароль
CALL create_reference(3, 3, 2, 'GROUPS'); -- Группы пользователей


CREATE VIEW user_view AS
    SELECT u."id" AS id, u_username."text" AS username, u_password."text" AS password
        FROM catalog_view dir_user
            LEFT JOIN value_relevance(current_date) val_username ON dir_user."id" = val_username."catalog_id"
            LEFT JOIN value_relevance(current_date) val_password ON dir_user."id" = val_password."catalog_id"
            LEFT JOIN "user" u ON dir_user."id" = u."catalog_id"
            LEFT JOIN user_relevance(current_date) u_username ON u."id" = u_username."user_id" AND val_username."id" = u_username."value_id"
            LEFT JOIN user_relevance(current_date) u_password ON u."id" = u_password."user_id" AND val_password."id" = u_password."value_id"
     WHERE dir_user."key_id" = 3
            AND val_username."key_id" = 1
            AND val_password."key_id" = 2
            AND u."end" IS NULL;


CREATE VIEW group_view AS
    SELECT u."id" AS user_id, u_username."text" AS username, g."id" AS group_id,  g_group_name."text" AS "group_name"
        FROM
            catalog_view dir_group
                LEFT JOIN value_relevance(current_date) val_group_name ON dir_group."id" = val_group_name."catalog_id"
                LEFT JOIN "group" g ON dir_group."id" = g."catalog_id"
                LEFT JOIN group_relevance(current_date) g_group_name ON g."id" = g_group_name."group_id" AND val_group_name."id" = g_group_name."value_id",
            catalog_view dir_user
                LEFT JOIN value_relevance(current_date) val_username ON dir_user."id" = val_username."catalog_id"
                LEFT JOIN value_relevance(current_date) val_group_id ON dir_user."id" = val_group_id."catalog_id"
                LEFT JOIN "user" u ON dir_user."id" = u."catalog_id"
                LEFT JOIN user_relevance(current_date) u_username ON u."id" = u_username."user_id" AND val_username."id" = u_username."value_id"
                LEFT JOIN user_relevance(current_date) u_group_id ON u."id" = u_group_id."user_id" AND val_group_id."id" = u_group_id."value_id"
      WHERE dir_user."key_id" = 3
        AND val_username."key_id" = 1
        AND val_group_id."key_id" = 3
        AND u."end" IS NULL
        AND u_group_id."reference_id" = g."id"
        AND dir_group."key_id" = 2
        AND val_group_name."key_id" = 1
        AND g."end" IS NULL;

CREATE VIEW role_view AS
    SELECT u."id" AS user_id, u_username."text" AS username, g."id" AS group_id,  g_group_name."text" AS "group_name",
           r."id" AS role_id, r_role_name."text" AS "role_name"
        FROM
            catalog_view dir_group
                LEFT JOIN value_relevance(current_date) val_group_name ON dir_group."id" = val_group_name."catalog_id"
                LEFT JOIN value_relevance(current_date) val_role_id ON dir_group."id" = val_role_id."catalog_id"
                LEFT JOIN "group" g ON dir_group."id" = g."catalog_id"
                LEFT JOIN group_relevance(current_date) g_group_name ON g."id" = g_group_name."group_id" AND val_group_name."id" = g_group_name."value_id"
                LEFT JOIN "group_data" g_role_id ON g."id" = g_role_id."group_id" AND val_role_id."id" = g_role_id."value_id",
            catalog_view dir_user
                LEFT JOIN value_relevance(current_date)  val_username ON dir_user."id" = val_username."catalog_id"
                LEFT JOIN value_relevance(current_date)  val_group_id ON dir_user."id" = val_group_id."catalog_id"
                LEFT JOIN "user" u ON dir_user."id" = u."catalog_id"
                LEFT JOIN user_relevance(current_date) u_username ON u."id" = u_username."user_id" AND val_username."id" = u_username."value_id"
                LEFT JOIN user_relevance(current_date) u_group_id ON u."id" = u_group_id."user_id" AND val_group_id."id" = u_group_id."value_id",
            catalog_view dir_role
                LEFT JOIN value_relevance(current_date)  val_role_name ON dir_role."id" = val_role_name."catalog_id"
                LEFT JOIN "role" r ON dir_role."id" = r."catalog_id"
                LEFT JOIN role_relevance(current_date) r_role_name ON r."id" = r_role_name."role_id" AND val_role_name."id" = r_role_name."value_id"
      WHERE dir_user."key_id" = 3
        AND val_username."key_id" = 1
        AND val_group_id."key_id" = 3
        AND u."end" IS NULL
        AND u_group_id."reference_id" = g."id"
        AND dir_group."key_id" = 2
        AND val_group_name."key_id" = 1
        AND val_role_id."key_id" = 2
        AND g."end" IS NULL
        AND g_role_id."reference_id" = r."id"
        AND dir_role."key_id" = 1
        AND val_role_name."key_id" = 1
        AND r."end" IS NULL;

DO $$
    DECLARE role_item_id BIGINT;
    DECLARE group_item_id BIGINT;
    DECLARE user_item_id BIGINT;
    DECLARE data_id BIGINT;
    BEGIN
        CALL add_item(1, role_item_id);
        CALL add_text(1, role_item_id, 1, 'ADMINISTRATOR', data_id);

        CALL add_item(2, group_item_id);
        CALL add_text(2, group_item_id, 1, 'ADMINISTRATORS', data_id);
        CALL add_reference(2, group_item_id, 2, 1, role_item_id, data_id);

        CALL add_item(3, user_item_id);
        CALL add_text(3, user_item_id, 1, 'admin', data_id);
        CALL add_text(3, user_item_id, 2,  encode(sha256('admin'::bytea), 'hex'), data_id);
        CALL add_reference(3, user_item_id, 3, 1,  group_item_id, data_id);
    END
$$;
