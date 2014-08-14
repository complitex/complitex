@ECHO off

SET GLASSFISH_ASADMIN=C:\glassfish3\bin\asadmin.bat

ECHO ---------------------------------------------------
ECHO Local database and Realm
ECHO ---------------------------------------------------
ECHO.
ECHO Register the JDBC connection pool
call %GLASSFISH_ASADMIN% create-jdbc-connection-pool --driverclassname com.mysql.jdbc.Driver --restype java.sql.Driver --property url=jdbc\:mysql\://localhost\:3306/pspoffice:user=pspoffice:password=pspoffice:characterResultSets=utf8:characterEncoding=utf8:useUnicode=true pspofficePool

ECHO.
ECHO Create a JDBC resource with the specified JNDI name
call %GLASSFISH_ASADMIN% create-jdbc-resource --connectionpoolid pspofficePool jdbc/pspofficeResource

ECHO.
ECHO Add the named authentication realm
call %GLASSFISH_ASADMIN% create-auth-realm --classname com.sun.enterprise.security.auth.realm.jdbc.JDBCRealm --property jaas-context=jdbcRealm:datasource-jndi=jdbc/pspofficeResource:user-table=user:user-name-column=login:password-column=password:group-table=usergroup:group-name-column=group_name:charset=UTF-8:digest-algorithm=MD5 pspofficeRealm

