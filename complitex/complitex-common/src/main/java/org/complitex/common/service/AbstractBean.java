package org.complitex.common.service;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.complitex.common.mybatis.SqlSessionFactoryBean;

import javax.ejb.EJB;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 09.08.2010 15:29:48
 */
public abstract class AbstractBean {
    protected final String NS_PREFIX = getClass().getName() + ".";
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
    
    protected String wrapStatement(String statement){
        return NS_PREFIX + statement;
    }
    
    protected <E> List<E> selectList(String statement, Object parameter){
        return sqlSession().selectList(wrapStatement(statement), parameter);        
    }
    
    protected <T> T selectOne(String statement, Object parameter){
        return sqlSession().selectOne(wrapStatement(statement), parameter);        
    }
    
    protected int insert(String statement, Object parameter){
        return sqlSession().insert(wrapStatement(statement), parameter);        
    }
    
    protected int update(String statement, Object parameter){
        return sqlSession().update(wrapStatement(statement), parameter);        
    }
    
    protected int delete(String statement, Object parameter){
        return sqlSession().update(wrapStatement(statement), parameter);         
    }
}
