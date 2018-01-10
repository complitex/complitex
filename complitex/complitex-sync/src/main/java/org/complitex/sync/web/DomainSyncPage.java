package org.complitex.sync.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.util.StringUtil;
import org.complitex.common.web.component.ajax.AjaxFeedbackPanel;
import org.complitex.sync.entity.SyncEntity;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.TemplatePage;

/**
 * @author Anatoly Ivanov
 *         Date: 024 24.06.14 17:56
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class DomainSyncPage extends TemplatePage {
    public DomainSyncPage(PageParameters pageParameters) {
        String entity = pageParameters.get("entity").toOptionalString();

        add(new Label("title",  new ResourceModel("title_" + StringUtil.emptyOnNull(entity))));
        add(new Label("header",  new ResourceModel("title_" + StringUtil.emptyOnNull(entity))));

        AjaxFeedbackPanel messages = new AjaxFeedbackPanel("messages");
        add(messages);

        add(new DomainSyncPanel("addressSyncPanel", SyncEntity.getValue(entity)){
            @Override
            protected void onUpdate(IPartialPageRequestHandler target) {
                target.add(messages);
            }
        });
    }
}
