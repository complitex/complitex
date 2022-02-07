package ru.complitex.organization_type;

import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.service.LogManager;
import ru.complitex.organization_type.strategy.OrganizationTypeStrategy;
import ru.complitex.template.strategy.TemplateStrategy;
import ru.complitex.template.web.pages.DomainObjectEdit;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton(name = "OrganizationTypeModule")
@Startup
public class Module {

    public static final String NAME = "ru.complitex.organization_type";
    @EJB
    private OrganizationTypeStrategy organizationTypeStrategy;

    @PostConstruct
    public void init() {
        LogManager.get().registerLink(DomainObject.class.getName(), organizationTypeStrategy.getEntityName(), DomainObjectEdit.class,
                organizationTypeStrategy.getEditPageParams(null, null, null), TemplateStrategy.OBJECT_ID);
    }
}
