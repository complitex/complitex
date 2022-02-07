package ru.complitex.pspoffice.housing_rights;

import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.service.LogManager;
import ru.complitex.pspoffice.housing_rights.strategy.HousingRightsStrategy;
import ru.complitex.template.strategy.TemplateStrategy;
import ru.complitex.template.web.pages.DomainObjectEdit;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton(name = "HousingRightsModule")
@Startup
public class Module {

    public static final String NAME = "ru.complitex.pspoffice.housing_rights";
    @EJB
    private HousingRightsStrategy housingRightsStrategy;

    @PostConstruct
    public void init() {
        LogManager.get().registerLink(DomainObject.class.getName(), housingRightsStrategy.getEntityName(), DomainObjectEdit.class,
                housingRightsStrategy.getEditPageParams(null, null, null), TemplateStrategy.OBJECT_ID);
    }
}
