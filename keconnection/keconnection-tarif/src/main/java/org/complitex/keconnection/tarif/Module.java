package org.complitex.keconnection.tarif;

import org.complitex.common.entity.DomainObject;
import org.complitex.common.service.LogManager;
import org.complitex.keconnection.tarif.strategy.TarifGroupStrategy;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.pages.DomainObjectEdit;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton(name = "TarifGroupModule")
@Startup
public class Module {

    public static final String NAME = "org.complitex.keconnection.tarifgroup";
    @EJB
    private TarifGroupStrategy tarifGroupStrategy;

    @PostConstruct
    public void init() {
        LogManager.get().registerLink(DomainObject.class.getName(), tarifGroupStrategy.getEntityName(), DomainObjectEdit.class,
                tarifGroupStrategy.getEditPageParams(null, null, null), TemplateStrategy.OBJECT_ID);
    }
}
