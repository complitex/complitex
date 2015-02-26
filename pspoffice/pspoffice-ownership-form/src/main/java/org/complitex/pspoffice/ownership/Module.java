package org.complitex.pspoffice.ownership;

import org.complitex.common.entity.DomainObject;
import org.complitex.common.service.LogManager;
import org.complitex.pspoffice.ownership.strategy.OwnershipFormStrategy;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.pages.DomainObjectEdit;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton(name = "OwnershipModule")
@Startup
public class Module {

    public static final String NAME = "org.complitex.pspoffice.ownership";
    @EJB
    private OwnershipFormStrategy ownershipFormStrategy;

    @PostConstruct
    public void init() {
        LogManager.get().registerLink(DomainObject.class.getName(), ownershipFormStrategy.getEntityName(), DomainObjectEdit.class,
                ownershipFormStrategy.getEditPageParams(null, null, null), TemplateStrategy.OBJECT_ID);
    }
}
