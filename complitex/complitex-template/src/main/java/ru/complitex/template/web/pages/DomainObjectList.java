package ru.complitex.template.web.pages;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.wicket.Page;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.strategy.StrategyFactory;
import ru.complitex.common.web.DictionaryFwSession;
import ru.complitex.common.web.component.domain.DomainObjectAccessUtil;
import ru.complitex.common.web.component.domain.DomainObjectListPanel;
import ru.complitex.common.web.component.search.SearchComponentState;
import ru.complitex.template.web.component.toolbar.AddItemButton;
import ru.complitex.template.web.component.toolbar.ToolbarButton;
import ru.complitex.template.web.component.toolbar.search.CollapsibleSearchToolbarButton;
import ru.complitex.template.web.security.SecurityRole;
import ru.complitex.template.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.Collections;
import java.util.List;

/**
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public final class DomainObjectList extends TemplatePage {

    public static final String ENTITY = "entity";
    public static final String STRATEGY = "strategy";
    @EJB
    private StrategyFactory strategyFactory;
    private DomainObjectListPanel listPanel;
    private String entity;
    private String strategyName;

    public DomainObjectList(PageParameters params) {
        entity = params.get(ENTITY).toString();
        strategyName = params.get(STRATEGY).toString();

        add(new Label("title", strategyFactory.getStrategy(strategyName, entity).getEntity().getName(getLocale())));

        if (!hasAnyRole(strategyFactory.getStrategy(strategyName, entity).getListRoles())) {
            throw new UnauthorizedInstantiationException(getClass());
        }

        add(listPanel = new DomainObjectListPanel("listPanel", entity, strategyName, false));
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        return ImmutableList.of(new AddItemButton(id) {

            @Override
            protected void onClick() {
                onAddObject(this.getPage(), strategyName, entity, getTemplateSession());
            }

            @Override
            protected void onBeforeRender() {
                if (!DomainObjectAccessUtil.canAddNew(strategyName, entity)) {
                    setVisible(false);
                }
                super.onBeforeRender();
            }
        }, new CollapsibleSearchToolbarButton(id, listPanel.getSearchPanel()));
    }

    protected void onAddObject(Page page, String strategyName, String entity, DictionaryFwSession session) {
        IStrategy strategy = strategyFactory.getStrategy(strategyName, entity);

        if (strategy.getSearchFilters() != null && !strategy.getSearchFilters().isEmpty()) {
            SearchComponentState globalSearchComponentState = session.getGlobalSearchComponentState();
            List<String> reverseSearchFilters = Lists.newArrayList(strategy.getSearchFilters());
            Collections.reverse(reverseSearchFilters);
            for (String searchFilter : reverseSearchFilters) {
                DomainObject parentObject = globalSearchComponentState.get(searchFilter);
                long parentId = parentObject == null ? SearchComponentState.NOT_SPECIFIED_ID
                        : (parentObject.getObjectId() != null ? parentObject.getObjectId() : SearchComponentState.NOT_SPECIFIED_ID);
                if (parentId > 0) {
                    page.setResponsePage(strategy.getEditPage(), strategy.getEditPageParams(null, parentId, searchFilter));
                    return;
                }
            }
            page.setResponsePage(strategy.getEditPage(), strategy.getEditPageParams(null,
                    SearchComponentState.NOT_SPECIFIED_ID, reverseSearchFilters.get(0)));
            return;
        }
        page.setResponsePage(strategy.getEditPage(), strategy.getEditPageParams(null, null, null));
    }
}
