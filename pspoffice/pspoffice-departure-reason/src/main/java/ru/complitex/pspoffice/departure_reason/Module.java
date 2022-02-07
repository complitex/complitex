package ru.complitex.pspoffice.departure_reason;

import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.service.LogManager;
import ru.complitex.pspoffice.departure_reason.strategy.DepartureReasonStrategy;
import ru.complitex.template.strategy.TemplateStrategy;
import ru.complitex.template.web.pages.DomainObjectEdit;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton(name = "DepartureReasonModule")
@Startup
public class Module {

    public static final String NAME = "ru.complitex.pspoffice.departure_reason";
    @EJB
    private DepartureReasonStrategy departureReasonStrategy;

    @PostConstruct
    public void init() {
        LogManager.get().registerLink(DomainObject.class.getName(), departureReasonStrategy.getEntityName(), DomainObjectEdit.class,
                departureReasonStrategy.getEditPageParams(null, null, null), TemplateStrategy.OBJECT_ID);
    }
}
