package ru.complitex.eirc.dictionary.service;

import com.google.common.collect.ImmutableSet;
import ru.complitex.common.entity.IConfig;
import ru.complitex.common.service.ConfigBean;
import ru.complitex.eirc.dictionary.entity.EircConfig;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.Set;

/**
 * @author Pavel Sknar
 */
@Startup
@Singleton(name = "EircConfigBean")
public class EircConfigBean extends ConfigBean {
    @Override
    protected Set<Class<? extends IConfig>> getIConfigClasses() {
        return ImmutableSet.<Class<? extends IConfig>>of(EircConfig.class);
    }
}
