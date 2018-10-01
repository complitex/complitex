package org.complitex.osznconnection.file;

import org.complitex.common.entity.Preference;
import org.complitex.common.service.PreferenceBean;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.subsidy.RequestFileGroupBean;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 02.09.2010 17:16:01
 */
@Singleton(name = "FileHandlingModule")
@Startup
public class Module {
    public static final String NAME = "org.complitex.osznconnection.file";

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private RequestFileGroupBean requestFileGroupBean;

    @EJB
    private PreferenceBean preferenceBean;

    @PostConstruct
    public void init() {
        requestFileBean.fixProcessingOnInit();
        requestFileGroupBean.fixProcessingOnInit();

        fixPreferences();
    }

    private void fixPreferences(){
        List<Preference> preferences = preferenceBean.getPreferences();

        preferences.stream().filter(p -> p.getPage().matches(".*\\d+")).forEach(p -> {
            p.setValue(null);

            preferenceBean.save(p);
        });

    }
}
