package ru.complitex.template.strategy;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.strategy.DomainObjectStrategy;
import ru.complitex.common.web.component.domain.validate.DefaultValidator;
import ru.complitex.common.web.component.domain.validate.IValidator;
import ru.complitex.template.web.pages.DomainObjectEdit;
import ru.complitex.template.web.pages.DomainObjectList;
import ru.complitex.template.web.pages.HistoryPage;
import ru.complitex.template.web.pages.ObjectNotFoundPage;
import ru.complitex.template.web.security.SecurityRole;

public abstract class TemplateStrategy extends DomainObjectStrategy {

    public static final String ENTITY = "entity";
    public static final String STRATEGY = "strategy";
    public static final String OBJECT_ID = "object_id";
    public static final String PARENT_ID = "parent_id";
    public static final String PARENT_ENTITY = "parent_entity";

    @Override
    public Class<? extends WebPage> getListPage() {
        return DomainObjectList.class;
    }

    @Override
    public PageParameters getListPageParams() {
        PageParameters params = new PageParameters();
        params.set(ENTITY, getEntityName());
        return params;
    }

    @Override
    public Class<? extends WebPage> getEditPage() {
        return DomainObjectEdit.class;
    }

    @Override
    public PageParameters getEditPageParams(Long objectId, Long parentId, String parentEntity) {
        PageParameters params = new PageParameters();
        params.set(ENTITY, getEntityName());
        params.set(OBJECT_ID, objectId);
        params.set(PARENT_ID, parentId);
        params.set(PARENT_ENTITY, parentEntity);
        return params;
    }

    @Override
    public Class<? extends WebPage> getHistoryPage() {
        return HistoryPage.class;
    }

    @Override
    public PageParameters getHistoryPageParams(Long objectId) {
        PageParameters params = new PageParameters();
        params.set(ENTITY, getEntityName());
        params.set(OBJECT_ID, objectId);
        return params;
    }

    @Override
    public IValidator getValidator() {
        return new DefaultValidator(getEntityName());
    }

    @Override
    public Page getObjectNotFoundPage() {
        return new ObjectNotFoundPage(getListPage(), getListPageParams());
    }

    @Override
    public String[] getListRoles() {
        return new String[]{SecurityRole.AUTHORIZED};
    }
}
