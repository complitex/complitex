package org.complitex.common.mybatis;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.SqlSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.io.Reader;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author Artem
 * @author Anatoly A. Ivanov java@inheaven.ru
 */
@Startup
@Singleton(name = "SqlSessionFactoryBean")
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class SqlSessionFactoryBean {
    private final Logger log = LoggerFactory.getLogger(SqlSessionFactoryBean.class);

    public static final String CONFIGURATION_FILE = "mybatis-config.xml";
    public static final String LOCAL_ENVIRONMENT = "local";

    private ConcurrentMap<JdbcEnvironment, SqlSessionManager> sqlSessionManagerMap = new ConcurrentHashMap<>();

    public SqlSessionManager getSqlSessionManager() {
        return getSqlSessionManager(null, LOCAL_ENVIRONMENT);
    }

    public SqlSessionManager getSqlSessionManager(String dataSource, String environment){
        JdbcEnvironment jdbcEnvironment = new JdbcEnvironment(dataSource, environment);

        return sqlSessionManagerMap.computeIfAbsent(jdbcEnvironment, this::newSqlSessionManager);
    }

    private SqlSessionManager newSqlSessionManager(JdbcEnvironment jdbcEnvironment){
        try(Reader reader = Resources.getResourceAsReader(CONFIGURATION_FILE)){
            Properties properties = new Properties();

            if (jdbcEnvironment.getDataSource() != null) {
                properties.setProperty("remoteDataSource", jdbcEnvironment.getDataSource());
            }

            SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
            XMLConfigBuilder parser = new XMLConfigBuilder(reader, jdbcEnvironment.getEnvironment(), properties);

            //Configuration
            Configuration configuration = parser.parse();

            return SqlSessionManager.newInstance(builder.build(configuration));
        } catch (Exception e) {
            throw ExceptionFactory.wrapException("Error building SqlSession.", e);
        } finally {
            ErrorContext.instance().reset();
        }
    }
}
