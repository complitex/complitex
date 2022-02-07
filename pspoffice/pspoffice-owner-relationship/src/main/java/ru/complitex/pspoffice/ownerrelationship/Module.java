package ru.complitex.pspoffice.ownerrelationship;

import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.service.LogManager;
import ru.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import ru.complitex.template.strategy.TemplateStrategy;
import ru.complitex.template.web.pages.DomainObjectEdit;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton(name = "OwnerRelationshipModule")
@Startup
public class Module {

    public static final String NAME = "ru.complitex.pspoffice.ownerrelationship";
    @EJB
    private OwnerRelationshipStrategy ownerRelationshipStrategy;

    @PostConstruct
    public void init() {
        LogManager.get().registerLink(DomainObject.class.getName(), ownerRelationshipStrategy.getEntityName(), DomainObjectEdit.class,
                ownerRelationshipStrategy.getEditPageParams(null, null, null), TemplateStrategy.OBJECT_ID);
    }
}
