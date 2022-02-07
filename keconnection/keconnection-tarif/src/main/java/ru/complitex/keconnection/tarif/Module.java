package ru.complitex.keconnection.tarif;

import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.service.LogManager;
import ru.complitex.keconnection.tarif.strategy.TarifGroupStrategy;
import ru.complitex.template.strategy.TemplateStrategy;
import ru.complitex.template.web.pages.DomainObjectEdit;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton(name = "TarifGroupModule")
@Startup
public class Module {

    public static final String NAME = "ru.complitex.keconnection.tarifgroup";
    @EJB
    private TarifGroupStrategy tarifGroupStrategy;

    @PostConstruct
    public void init() {
        LogManager.get().registerLink(DomainObject.class.getName(), tarifGroupStrategy.getEntityName(), DomainObjectEdit.class,
                tarifGroupStrategy.getEditPageParams(null, null, null), TemplateStrategy.OBJECT_ID);
    }
}
