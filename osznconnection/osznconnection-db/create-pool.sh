#!/bin/sh

GLASSFISH_ASADMIN=asadmin

echo ---------------------------------------------------
echo Local database and Realm
echo ---------------------------------------------------
echo
echo Register the JDBC connection pool
$GLASSFISH_ASADMIN create-jdbc-connection-pool --datasourceclassname="org.postgresql.ds.PGConnectionPoolDataSource" --restype="javax.sql.ConnectionPoolDataSource" --property="serverName=localhost:databaseName=osznconnection:user=osznconnection:password=osznconnection:characterResultSets=utf8:characterEncoding=utf8mb4:useUnicode=true:connectionCollation=utf8mb4_unicode_ci" osznconnectionPool

echo
echo Create a JDBC resource with the specified JNDI name
$GLASSFISH_ASADMIN create-jdbc-resource --connectionpoolid osznconnectionPool jdbc/osznconnectionResource

echo
echo Add the named authentication realm
$GLASSFISH_ASADMIN create-auth-realm --classname="com.sun.enterprise.security.auth.realm.jdbc.JDBCRealm" --property="jaas-context=jdbcRealm:datasource-jndi=jdbc/osznconnectionResource:user-table=user_view:user-name-column=login:password-column=password:group-table=usergroup:group-name-column=group_name:charset=UTF-8" osznconnectionRealm

echo
echo ---------------------------------------------------
echo Remote database
echo ---------------------------------------------------
echo
echo Register the JDBC connection pool
$GLASSFISH_ASADMIN create-jdbc-connection-pool --driverclassname oracle.jdbc.OracleDriver --restype java.sql.Driver --property="url=jdbc\:oracle\:thin\:10.50.4.15\:1521\:cnreal:user=export:password=export:characterResultSets=utf8:characterEncoding=utf8mb4:useUnicode=true:connectionCollation=utf8mb4_unicode_ci:autoReconnect=true" osznconnectionRemotePool

echo
echo Create a JDBC resource with the specified JNDI name
$GLASSFISH_ASADMIN create-jdbc-resource --connectionpoolid osznconnectionRemotePool jdbc/osznconnectionRemoteResource
