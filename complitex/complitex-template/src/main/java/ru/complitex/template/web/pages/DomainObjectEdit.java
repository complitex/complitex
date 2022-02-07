package ru.complitex.template.web.pages;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.strategy.StrategyFactory;
import ru.complitex.common.web.component.domain.DomainObjectAccessUtil;
import ru.complitex.common.web.component.domain.DomainObjectEditPanel;
import ru.complitex.template.web.component.toolbar.DeleteItemButton;
import ru.complitex.template.web.component.toolbar.DisableItemButton;
import ru.complitex.template.web.component.toolbar.EnableItemButton;
import ru.complitex.template.web.component.toolbar.ToolbarButton;
import ru.complitex.template.web.security.SecurityRole;
import ru.complitex.template.web.template.FormTemplatePage;
import ru.complitex.template.strategy.TemplateStrategy;

import javax.ejb.EJB;
import java.util.List;

@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class DomainObjectEdit extends FormTemplatePage {

    @EJB
    private StrategyFactory strategyFactory;
    private DomainObjectEditPanel editPanel;
    private String entity;
    private String strategy;

    public DomainObjectEdit(PageParameters parameters) {
        init(parameters.get(TemplateStrategy.ENTITY).toString(), parameters.get(TemplateStrategy.STRATEGY).toString(), parameters.get(TemplateStrategy.OBJECT_ID).toOptionalLong(),
                parameters.get(TemplateStrategy.PARENT_ID).toOptionalLong(), parameters.get(TemplateStrategy.PARENT_ENTITY).toString(),
                parameters.get(BACK_INFO_SESSION_KEY).toString());
    }

    protected void init(String entity, String strategy, Long objectId, Long parentId, String parentEntity,
            String backInfoSessionKey) {
        this.entity = entity;
        this.strategy = strategy;

        if (!hasAnyRole(strategyFactory.getStrategy(strategy, entity).getListRoles())) {
            throw new UnauthorizedInstantiationException(getClass());
        }

        add(editPanel = newEditPanel("editPanel", entity, strategy, objectId, parentId, parentEntity, backInfoSessionKey));
    }

    protected DomainObjectEditPanel newEditPanel(String id, String entity, String strategy, Long objectId, Long parentId,
            String parentEntity, String backInfoSessionKey) {
        return new DomainObjectEditPanel(id, entity, strategy, objectId, parentId, parentEntity, backInfoSessionKey);
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        return ImmutableList.of(new DisableItemButton(id) {

            @Override
            protected void onClick() {
                editPanel.disable();
            }

            @Override
            protected void onBeforeRender() {
                if (!DomainObjectAccessUtil.canDisable(strategy, entity, editPanel.getNewObject())) {
                    setVisible(false);
                }
                super.onBeforeRender();
            }
        }, new EnableItemButton(id) {

            @Override
            protected void onClick() {
                editPanel.enable();
            }

            @Override
            protected void onBeforeRender() {
                if (!DomainObjectAccessUtil.canEnable(strategy, entity, editPanel.getNewObject())) {
                    setVisible(false);
                }
                super.onBeforeRender();
            }
        }, new DeleteItemButton(id) {

            @Override
            protected void onClick() {
                editPanel.delete();
            }

            @Override
            protected void onBeforeRender() {
                if (!DomainObjectAccessUtil.canDelete(strategy, entity, editPanel.getNewObject())) {
                    setVisible(false);
                }
                super.onBeforeRender();
            }
        });
    }
}
