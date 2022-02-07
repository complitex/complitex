package ru.complitex.pspoffice.document_type;

import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.service.LogManager;
import ru.complitex.pspoffice.document_type.strategy.DocumentTypeStrategy;
import ru.complitex.template.strategy.TemplateStrategy;
import ru.complitex.template.web.pages.DomainObjectEdit;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton(name = "DocumentTypeModule")
@Startup
public class Module {

    public static final String NAME = "ru.complitex.pspoffice.document_type";
    @EJB
    private DocumentTypeStrategy documentTypeStrategy;

    @PostConstruct
    public void init() {
        LogManager.get().registerLink(DomainObject.class.getName(), documentTypeStrategy.getEntityName(), DomainObjectEdit.class,
                documentTypeStrategy.getEditPageParams(null, null, null), TemplateStrategy.OBJECT_ID);
    }
}
