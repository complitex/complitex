package ru.complitex.eirc.mb_transformer.service;

import com.google.common.collect.ImmutableSet;
import ru.complitex.common.entity.IConfig;
import ru.complitex.common.service.ConfigBean;
import ru.complitex.eirc.mb_transformer.entity.MbTransformerConfig;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.Set;

/**
 * @author Pavel Sknar
 */
@Startup
@Singleton(name = "MbTransformerConfigBean")
public class MbTransformerConfigBean extends ConfigBean {
    @Override
    protected Set<Class<? extends IConfig>> getIConfigClasses() {
        return ImmutableSet.<Class<? extends IConfig>>of(MbTransformerConfig.class);
    }
}
