package org.complitex.common.service;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.complitex.common.mybatis.SqlSessionFactoryBean;

import javax.ejb.EJB;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 09.08.2010 15:29:48
 */
public abstract class AbstractBean {
    protected final static String DEFAULT_ENVIRONMENT = "remote";

    @EJB(beanName = "SqlSessionFactoryBean")
    private SqlSessionFactoryBean sqlSessionFactoryBean;

    protected SqlSessionManager getSqlSessionManager() {
        return sqlSessionFactoryBean.getSqlSessionManager();
    }

    protected SqlSession sqlSession(){
        return sqlSessionFactoryBean.getSqlSessionManager();
    }

    protected SqlSession sqlSession(String environment, String dataSource){
        return sqlSessionFactoryBean.getSqlSessionManager(environment, dataSource);
    }

    protected SqlSession sqlSession(String dataSource){
        return sqlSessionFactoryBean.getSqlSessionManager(dataSource, DEFAULT_ENVIRONMENT);
    }

    public void setSqlSessionFactoryBean(SqlSessionFactoryBean sqlSessionFactoryBean) {
        this.sqlSessionFactoryBean = sqlSessionFactoryBean;
    }
}
