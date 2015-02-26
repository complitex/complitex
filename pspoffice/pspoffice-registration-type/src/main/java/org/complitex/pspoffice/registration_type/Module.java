package org.complitex.pspoffice.registration_type;

import org.complitex.common.entity.DomainObject;
import org.complitex.common.service.LogManager;
import org.complitex.pspoffice.registration_type.strategy.RegistrationTypeStrategy;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.pages.DomainObjectEdit;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton(name = "RegistrationTypeModule")
@Startup
public class Module {

    public static final String NAME = "org.complitex.pspoffice.registration_type";
    @EJB
    private RegistrationTypeStrategy registrationTypeStrategy;

    @PostConstruct
    public void init() {
        LogManager.get().registerLink(DomainObject.class.getName(), registrationTypeStrategy.getEntityName(), DomainObjectEdit.class,
                registrationTypeStrategy.getEditPageParams(null, null, null), TemplateStrategy.OBJECT_ID);
    }
}
