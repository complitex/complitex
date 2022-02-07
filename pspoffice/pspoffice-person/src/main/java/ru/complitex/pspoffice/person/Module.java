package ru.complitex.pspoffice.person;

import ru.complitex.common.service.LogManager;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.pspoffice.person.strategy.web.edit.person.PersonEdit;
import ru.complitex.template.strategy.TemplateStrategy;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.08.2010 18:41:01
 */
@Singleton(name = "PersonModule")
@Startup
public class Module {

    public static final String NAME = "ru.complitex.pspoffice.person";

    @PostConstruct
    public void init() {
        LogManager.get().registerLink(DomainObject.class.getName(), "person", PersonEdit.class, null, TemplateStrategy.OBJECT_ID);
    }
}
