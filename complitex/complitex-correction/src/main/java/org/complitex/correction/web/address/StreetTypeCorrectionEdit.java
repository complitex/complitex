package org.complitex.correction.web.address;

import com.google.common.collect.Lists;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.address.strategy.street_type.StreetTypeStrategy;
import org.complitex.common.web.component.EntityTypePanel;
import org.complitex.correction.web.address.component.AbstractCorrectionEditPanel;
import org.complitex.template.web.component.toolbar.DeleteItemButton;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.FormTemplatePage;

import java.util.List;
import java.util.Locale;

import static org.complitex.address.entity.AddressEntity.STREET_TYPE;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class StreetTypeCorrectionEdit extends FormTemplatePage {

    public static final String CORRECTION_ID = "correction_id";
    private AbstractCorrectionEditPanel correctionEditPanel;

    public StreetTypeCorrectionEdit(PageParameters params) {
        Long correctionId = params.get(CORRECTION_ID).toOptionalLong();
        add(correctionEditPanel = new AbstractCorrectionEditPanel(STREET_TYPE.getEntityName(), "correctionEditPanel",
                correctionId) {
            @Override
            protected IModel<String> internalObjectLabel(Locale locale) {
                return new ResourceModel("street_type");
            }

            @Override
            protected Panel internalObjectPanel(String id) {
                IModel<Long> streetTypeModel = new Model<Long>() {

                    @Override
                    public Long getObject() {
                        return getCorrection().getObjectId();
                    }

                    @Override
                    public void setObject(Long streetTypeId) {
                        getCorrection().setObjectId(streetTypeId);

                    }
                };
                return new EntityTypePanel(id, "street_type", StreetTypeStrategy.NAME, streetTypeModel,
                        new ResourceModel("street_type"), true, true);
            }

            @Override
            protected String getNullObjectErrorMessage() {
                return getString("street_type_required");
            }

            @Override
            protected Class<? extends Page> getBackPageClass() {
                return StreetTypeCorrectionList.class;
            }

            @Override
            protected PageParameters getBackPageParameters() {
                return new PageParameters();
            }

            @Override
            protected IModel<String> getTitleModel() {
                return new StringResourceModel("title", this, null);
            }
        });
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        List<ToolbarButton> toolbar = Lists.newArrayList();
        toolbar.add(new DeleteItemButton(id) {

            @Override
            protected void onClick() {
                correctionEditPanel.executeDeletion();
            }

            @Override
            public boolean isVisible() {
                return !correctionEditPanel.isNew();
            }
        });
        return toolbar;
    }
}
