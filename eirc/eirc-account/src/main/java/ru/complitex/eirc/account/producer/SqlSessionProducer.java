package ru.complitex.eirc.account.producer;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.mybatis.cdi.SessionFactoryProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import java.io.IOException;

/**
 * @author Ivanov Anatoliy
 */

public class SqlSessionProducer {
    @ApplicationScoped
    @Produces
    @Default
    @SessionFactoryProvider
    public SqlSessionFactory produceCatalogFactory() throws IOException {
        return new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream("mybatis-config.xml"), "catalog");
    }
}
