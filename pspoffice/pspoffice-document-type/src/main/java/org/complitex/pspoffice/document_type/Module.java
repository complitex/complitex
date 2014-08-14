package org.complitex.pspoffice.document_type;

import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.LogManager;
import org.complitex.template.web.pages.DomainObjectEdit;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.complitex.pspoffice.document_type.strategy.DocumentTypeStrategy;
import org.complitex.template.strategy.TemplateStrategy;

@Singleton(name = "DocumentTypeModule")
@Startup
public class Module {

    public static final String NAME = "org.complitex.pspoffice.document_type";
    @EJB
    private DocumentTypeStrategy documentTypeStrategy;

    @PostConstruct
    public void init() {
        LogManager.get().registerLink(DomainObject.class.getName(), documentTypeStrategy.getEntityTable(), DomainObjectEdit.class,
                documentTypeStrategy.getEditPageParams(null, null, null), TemplateStrategy.OBJECT_ID);
    }
}
