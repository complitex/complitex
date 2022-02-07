package ru.complitex.logging.service;

import ru.complitex.common.entity.Log;
import ru.complitex.common.service.AbstractBean;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 19.08.2010 13:07:35
 */
@Stateless
public class LogListBean extends AbstractBean {
    public static final String STATEMENT_PREFIX = LogListBean.class.getCanonicalName();


    public List<Log> getLogs(LogFilter filter){
        return sqlSession().selectList(STATEMENT_PREFIX + ".selectLogs", filter);
    }


    public Long getLogsCount(LogFilter filter){
        return sqlSession().selectOne(STATEMENT_PREFIX + ".selectLogsCount", filter);
    }


    public List<String> getModules(){
        return sqlSession().selectList(STATEMENT_PREFIX + ".selectModules");
    }


    public List<String> getControllers(){
        return sqlSession().selectList(STATEMENT_PREFIX + ".selectControllers");
    }


    public List<String> getModels(){
        return sqlSession().selectList(STATEMENT_PREFIX + ".selectModels");
    }
}
