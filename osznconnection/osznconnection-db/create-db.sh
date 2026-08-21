#!/usr/bin/env bash
#set -x
set -euo pipefail

USER_NAME=root
DBPWD=${USER_NAME}
DB_NAME=osznconnection
USR_NAME=${DB_NAME}
USR_PWD=${DB_NAME}
CHAR_SET=utf8mb4
COLLATE=${CHAR_SET}_unicode_ci

# recreate database
echo "recreate db ${DB_NAME}"
mysql -u ${USER_NAME} -p${DBPWD} -e "drop database if exists ${DB_NAME};"
mysql -u ${USER_NAME} -p${DBPWD} -e "create database ${DB_NAME} charset = ${CHAR_SET} collate = ${COLLATE};"
CREATE_USR=$(printf "mysql -u ${USER_NAME} -p${DBPWD} -e \"create user if not exists ${USR_NAME} identified by '%s';\"" ${USR_PWD})
eval ${CREATE_USR}
mysql -u ${USER_NAME} -p${DBPWD} -e "grant all privileges on ${DB_NAME}.* to ${USR_NAME}@'%';"
mysql -u ${USER_NAME} -p${DBPWD} -e "flush privileges;"

