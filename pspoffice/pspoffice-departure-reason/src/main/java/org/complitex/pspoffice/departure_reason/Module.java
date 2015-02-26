package org.complitex.pspoffice.departure_reason;

import org.complitex.common.entity.DomainObject;
import org.complitex.common.service.LogManager;
import org.complitex.pspoffice.departure_reason.strategy.DepartureReasonStrategy;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.pages.DomainObjectEdit;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton(name = "DepartureReasonModule")
@Startup
public class Module {

    public static final String NAME = "org.complitex.pspoffice.departure_reason";
    @EJB
    private DepartureReasonStrategy departureReasonStrategy;

    @PostConstruct
    public void init() {
        LogManager.get().registerLink(DomainObject.class.getName(), departureReasonStrategy.getEntityName(), DomainObjectEdit.class,
                departureReasonStrategy.getEditPageParams(null, null, null), TemplateStrategy.OBJECT_ID);
    }
}
