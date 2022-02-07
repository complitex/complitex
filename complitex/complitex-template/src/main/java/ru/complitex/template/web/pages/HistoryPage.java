package ru.complitex.template.web.pages;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.web.component.domain.HistoryPanel;
import ru.complitex.template.web.security.SecurityRole;
import ru.complitex.template.web.template.TemplatePage;
import ru.complitex.template.strategy.TemplateStrategy;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class HistoryPage extends TemplatePage {

    public HistoryPage(PageParameters params) {
        add(newHistoryPanel("historyPanel", params.get(TemplateStrategy.STRATEGY).toString(), params.get(TemplateStrategy.ENTITY).toString(),
                params.get(TemplateStrategy.OBJECT_ID).toLong()));
    }

    protected HistoryPanel newHistoryPanel(String id, String strategyName, String entity, long objectId) {
        return new HistoryPanel(id, strategyName, entity, objectId);
    }
}
