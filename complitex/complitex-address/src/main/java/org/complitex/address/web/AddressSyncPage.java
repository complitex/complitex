package org.complitex.address.web;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.address.entity.AddressEntity;
import org.complitex.address.web.component.AddressSyncPanel;
import org.complitex.common.web.component.ajax.AjaxFeedbackPanel;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.TemplatePage;

/**
 * @author Anatoly Ivanov
 *         Date: 024 24.06.14 17:56
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class AddressSyncPage extends TemplatePage {
    public AddressSyncPage(PageParameters pageParameters) {
        add(new Label("title",  new ResourceModel("title")));

        AjaxFeedbackPanel messages = new AjaxFeedbackPanel("messages");
        add(messages);

        add(new AddressSyncPanel("addressSyncPanel", AddressEntity.getValue(pageParameters.get("entity").toOptionalString())){
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(messages);
            }
        });
    }
}
