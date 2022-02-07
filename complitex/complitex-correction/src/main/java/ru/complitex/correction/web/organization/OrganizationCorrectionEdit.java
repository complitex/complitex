package ru.complitex.correction.web.organization;

import org.apache.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.web.component.organization.OrganizationIdPicker;
import ru.complitex.correction.web.address.component.AbstractCorrectionEditPanel;
import ru.complitex.organization_type.strategy.OrganizationTypeStrategy;
import ru.complitex.template.web.security.SecurityRole;
import ru.complitex.template.web.template.FormTemplatePage;

import java.util.Locale;

/**
 * @author Anatoly Ivanov java@inheaven.ru
 *         Date: 29.11.13 18:44
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class OrganizationCorrectionEdit extends FormTemplatePage {
    public static final String CORRECTION_ID = "correction_id";

    public OrganizationCorrectionEdit(PageParameters params) {
        add(new AbstractCorrectionEditPanel("organization", "organization_edit_panel",
                params.get(CORRECTION_ID).toOptionalLong()) {

            @Override
            protected IModel<String> internalObjectLabel(Locale locale) {
                return new ResourceModel("internal_object");
            }

            @Override
            protected WebMarkupContainer internalObjectPanel(String id) {
                return new OrganizationIdPicker(id, new PropertyModel<>(getCorrection(), "objectId"),
                        OrganizationTypeStrategy.SERVICING_ORGANIZATION_TYPE);
            }

            @Override
            protected String getNullObjectErrorMessage() {
                return getString("organization_required");
            }
            @Override
            protected Class<? extends Page> getBackPageClass() {
                return OrganizationCorrectionList.class;
            }

            @Override
            protected PageParameters getBackPageParameters() {
                return new PageParameters();
            }

            @Override
            protected IModel<String> getTitleModel() {
                return new ResourceModel("title");
            }
        });
    }
}
