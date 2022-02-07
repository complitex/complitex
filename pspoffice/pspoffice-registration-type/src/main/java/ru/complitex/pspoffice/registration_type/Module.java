package ru.complitex.pspoffice.registration_type;

import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.service.LogManager;
import ru.complitex.pspoffice.registration_type.strategy.RegistrationTypeStrategy;
import ru.complitex.template.strategy.TemplateStrategy;
import ru.complitex.template.web.pages.DomainObjectEdit;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton(name = "RegistrationTypeModule")
@Startup
public class Module {

    public static final String NAME = "ru.complitex.pspoffice.registration_type";
    @EJB
    private RegistrationTypeStrategy registrationTypeStrategy;

    @PostConstruct
    public void init() {
        LogManager.get().registerLink(DomainObject.class.getName(), registrationTypeStrategy.getEntityName(), DomainObjectEdit.class,
                registrationTypeStrategy.getEditPageParams(null, null, null), TemplateStrategy.OBJECT_ID);
    }
}
