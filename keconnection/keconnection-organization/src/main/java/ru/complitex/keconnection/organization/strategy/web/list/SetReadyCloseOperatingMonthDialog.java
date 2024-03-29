package ru.complitex.keconnection.organization.strategy.web.list;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import ru.complitex.common.strategy.organization.IOrganizationStrategy;
import ru.complitex.keconnection.organization.strategy.KeOrganizationStrategy;
import ru.complitex.keconnection.organization.strategy.entity.KeOrganization;
import org.odlabs.wiquery.ui.dialog.Dialog;

import javax.ejb.EJB;
import java.text.MessageFormat;

/**
 *
 * @author Artem
 */
public abstract class SetReadyCloseOperatingMonthDialog extends Panel {
    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private KeOrganizationStrategy organizationStrategy;

    private final Dialog dialog;
    private final Label caption;
    private KeOrganization organization;

    public SetReadyCloseOperatingMonthDialog(String id) {
        super(id);

        dialog = new Dialog("dialog") {

            {
                getOptions().putLiteral("width", "auto");
                setMinHeight(1);
            }
        };
        dialog.setModal(true);
        add(dialog);

        caption = new Label("caption", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                if (organization != null) {
                    return MessageFormat.format(getString("caption"),
                            organizationStrategy.displayDomainObject(organization, getLocale()));
                }
                return null;
            }
        });
        caption.setOutputMarkupId(true);
        dialog.add(caption);

        AjaxCheckBox readyCloseOperatingMonthFlag = new AjaxCheckBox("readyCloseOperatingMonthFlag", new Model<Boolean>()) {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                organizationStrategy.setReadyCloseOperatingMonthFlag(organization);
                close(target);
                onSet(organization, target);
            }
        };
        dialog.add(readyCloseOperatingMonthFlag);
    }

    protected abstract void onSet(KeOrganization organization, AjaxRequestTarget target);

    private void close(AjaxRequestTarget target) {
        dialog.close(target);
    }

    public void open(AjaxRequestTarget target, KeOrganization organization) {
        this.organization = organization;
        target.add(caption);
        dialog.open(target);
    }
}
