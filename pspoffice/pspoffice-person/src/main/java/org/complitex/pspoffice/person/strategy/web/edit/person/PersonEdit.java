/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit.person;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.util.CloneUtil;
import org.complitex.dictionary.web.component.back.BackInfo;
import org.complitex.dictionary.web.component.back.BackInfoManager;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.PersonAgeType;
import org.complitex.pspoffice.person.strategy.web.edit.person.toolbar.DeathButton;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.pages.DomainObjectList;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.FormTemplatePage;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class PersonEdit extends FormTemplatePage {

    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private StringCultureBean stringBean;
    private Person oldPerson;
    private Person newPerson;
    private PersonDeathDialog personDeathDialog;
    private final String backInfoSessionKey;

    public PersonEdit(PageParameters parameters) {
        if (!hasAnyRole(personStrategy.getEditRoles())) {
            throw new UnauthorizedInstantiationException(getClass());
        }

        Long objectId = parameters.get(TemplateStrategy.OBJECT_ID).toOptionalLong();
        if (objectId == null) {
            //create new entity
            oldPerson = null;
            newPerson = personStrategy.newInstance();

        } else {
            //edit existing entity
            newPerson = personStrategy.findById(objectId, false);
            if (newPerson == null) {
                throw new RestartResponseException(personStrategy.getObjectNotFoundPage());
            }
            oldPerson = CloneUtil.cloneObject(newPerson);
        }

        this.backInfoSessionKey = parameters.get(BACK_INFO_SESSION_KEY).toString();

        init();
    }

    private void init() {
        Label title = new Label("title", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return stringBean.displayValue(personStrategy.getEntity().getEntityNames(), getLocale());
            }
        });
        add(title);

        add(new PersonEditPanel("personEditPanel", PersonAgeType.ANY, oldPerson, newPerson) {

            @Override
            protected void onBack(AjaxRequestTarget target) {
                PersonEdit.this.back();
            }

            @Override
            protected void onSave(Person oldPerson, Person newPerson, AjaxRequestTarget target) {
                this.onBack(target);
            }
        });

        personDeathDialog = new PersonDeathDialog("personDeathDialog");
        personDeathDialog.setVisible(oldPerson != null && !oldPerson.isDead());
        add(personDeathDialog);
    }

    private void back() {
        if (!Strings.isEmpty(backInfoSessionKey)) {
            BackInfo backInfo = BackInfoManager.get(this, backInfoSessionKey);
            if (backInfo != null) {
                backInfo.back(this);
                return;
            }
        }

        PageParameters listPageParams = personStrategy.getListPageParams();
        listPageParams.set(DomainObjectList.SCROLL_PARAMETER, newPerson.getId());
        setResponsePage(personStrategy.getListPage(), listPageParams);
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        return ImmutableList.of(
                new DeathButton(id) {

                    @Override
                    protected void onClick(AjaxRequestTarget target) {
                        personDeathDialog.open(target, oldPerson);
                    }

                    @Override
                    protected void onBeforeRender() {
                        super.onBeforeRender();
                        setVisible(personDeathDialog.isVisible());
                    }
                });
    }
}
