package ru.complitex.common.service;

import ru.complitex.common.entity.DictionaryConfig;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.03.2014 19:25
 */
@Stateless
public class ModuleBean {
    @EJB
    private ConfigBean configBean;

    public Long getModuleId(){
        return configBean.getLong(DictionaryConfig.MODULE_ID, true);
    }
}
