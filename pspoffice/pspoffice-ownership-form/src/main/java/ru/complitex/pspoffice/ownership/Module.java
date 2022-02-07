package ru.complitex.pspoffice.ownership;

import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.service.LogManager;
import ru.complitex.pspoffice.ownership.strategy.OwnershipFormStrategy;
import ru.complitex.template.strategy.TemplateStrategy;
import ru.complitex.template.web.pages.DomainObjectEdit;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton(name = "OwnershipModule")
@Startup
public class Module {

    public static final String NAME = "ru.complitex.pspoffice.ownership";
    @EJB
    private OwnershipFormStrategy ownershipFormStrategy;

    @PostConstruct
    public void init() {
        LogManager.get().registerLink(DomainObject.class.getName(), ownershipFormStrategy.getEntityName(), DomainObjectEdit.class,
                ownershipFormStrategy.getEditPageParams(null, null, null), TemplateStrategy.OBJECT_ID);
    }
}
