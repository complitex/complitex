package org.complitex.pspoffice.military;

import org.complitex.common.entity.DomainObject;
import org.complitex.common.service.LogManager;
import org.complitex.pspoffice.military.strategy.MilitaryServiceRelationStrategy;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.pages.DomainObjectEdit;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton(name = "MilitaryServiceRelationModule")
@Startup
public class Module {

    public static final String NAME = "org.complitex.pspoffice.military_service_relation";
    @EJB
    private MilitaryServiceRelationStrategy militaryServiceRelationStrategy;

    @PostConstruct
    public void init() {
        LogManager.get().registerLink(DomainObject.class.getName(), militaryServiceRelationStrategy.getEntityName(), DomainObjectEdit.class,
                militaryServiceRelationStrategy.getEditPageParams(null, null, null), TemplateStrategy.OBJECT_ID);
    }
}
