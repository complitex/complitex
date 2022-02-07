package ru.complitex.pspoffice.military;

import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.service.LogManager;
import ru.complitex.pspoffice.military.strategy.MilitaryServiceRelationStrategy;
import ru.complitex.template.strategy.TemplateStrategy;
import ru.complitex.template.web.pages.DomainObjectEdit;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton(name = "MilitaryServiceRelationModule")
@Startup
public class Module {

    public static final String NAME = "ru.complitex.pspoffice.military_service_relation";
    @EJB
    private MilitaryServiceRelationStrategy militaryServiceRelationStrategy;

    @PostConstruct
    public void init() {
        LogManager.get().registerLink(DomainObject.class.getName(), militaryServiceRelationStrategy.getEntityName(), DomainObjectEdit.class,
                militaryServiceRelationStrategy.getEditPageParams(null, null, null), TemplateStrategy.OBJECT_ID);
    }
}
