package org.complitex.pspoffice.housing_rights;

import org.complitex.common.entity.DomainObject;
import org.complitex.common.service.LogManager;
import org.complitex.pspoffice.housing_rights.strategy.HousingRightsStrategy;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.pages.DomainObjectEdit;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton(name = "HousingRightsModule")
@Startup
public class Module {

    public static final String NAME = "org.complitex.pspoffice.housing_rights";
    @EJB
    private HousingRightsStrategy housingRightsStrategy;

    @PostConstruct
    public void init() {
        LogManager.get().registerLink(DomainObject.class.getName(), housingRightsStrategy.getEntityName(), DomainObjectEdit.class,
                housingRightsStrategy.getEditPageParams(null, null, null), TemplateStrategy.OBJECT_ID);
    }
}
