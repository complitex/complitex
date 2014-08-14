package org.complitex.pspoffice.ownership;

import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.LogManager;
import org.complitex.template.web.pages.DomainObjectEdit;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.complitex.pspoffice.ownership.strategy.OwnershipFormStrategy;
import org.complitex.template.strategy.TemplateStrategy;

@Singleton(name = "OwnershipModule")
@Startup
public class Module {

    public static final String NAME = "org.complitex.pspoffice.ownership";
    @EJB
    private OwnershipFormStrategy ownershipFormStrategy;

    @PostConstruct
    public void init() {
        LogManager.get().registerLink(DomainObject.class.getName(), ownershipFormStrategy.getEntityTable(), DomainObjectEdit.class,
                ownershipFormStrategy.getEditPageParams(null, null, null), TemplateStrategy.OBJECT_ID);
    }
}
