package org.complitex.keconnection.tarif.strategy.web.edit;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.common.entity.AttributeType;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.web.component.DisableAwareDropDownChoice;
import org.complitex.common.web.component.DomainObjectComponentUtil;
import org.complitex.common.web.component.DomainObjectDisableAwareRenderer;
import org.complitex.common.web.component.domain.AbstractComplexAttributesPanel;
import org.complitex.common.web.component.domain.DomainObjectAccessUtil;
import org.complitex.keconnection.tarif.strategy.TarifGroupStrategy;
import org.complitex.keconnection.tarif.strategy.TarifStrategy;

import javax.ejb.EJB;
import java.util.List;

/**
 *
 * @author Artem
 */
public final class TarifEditComponent extends AbstractComplexAttributesPanel {

    @EJB
    private TarifGroupStrategy tarifGroupStrategy;
    @EJB
    private TarifStrategy tarifStrategy;

    public TarifEditComponent(String id, boolean disabled) {
        super(id, disabled);
    }

    @Override
    protected void init() {
        final DomainObject tarif = getDomainObject();
        final boolean enabled = !isDisabled() && DomainObjectAccessUtil.canEdit(null, "tarif", tarif);

        final AttributeType tarifGroupAttributeType =
                tarifStrategy.getEntity().getAttributeType(TarifStrategy.TARIF_GROUP);
        final IModel<String> tarifGroupLabelModel =
                DomainObjectComponentUtil.labelModel(tarifGroupAttributeType.getAttributeNames(), getLocale());
        add(new Label("tarifGroupLabel", tarifGroupLabelModel));

        WebMarkupContainer tarifGroupRequiredContainer = new WebMarkupContainer("tarifGroupRequiredContainer");
        tarifGroupRequiredContainer.setVisible(tarifGroupAttributeType.isMandatory());
        add(tarifGroupRequiredContainer);

        final List<DomainObject> allTarifGroups = tarifGroupStrategy.getAll();

        final DomainObjectDisableAwareRenderer tarifGroupRenderer = new DomainObjectDisableAwareRenderer() {

            @Override
            public Object getDisplayValue(DomainObject object) {
                return tarifGroupStrategy.displayDomainObject(object, getLocale());
            }
        };

        IModel<DomainObject> model = new Model<DomainObject>() {

            @Override
            public void setObject(DomainObject tarifGroup) {
                Long tarifGroupId = tarifGroup != null ? tarifGroup.getObjectId() : null;
                tarif.getAttribute(TarifStrategy.TARIF_GROUP).setValueId(tarifGroupId);
            }

            @Override
            public DomainObject getObject() {
                Long tarifGroupId = tarif.getAttribute(TarifStrategy.TARIF_GROUP).getValueId();
                if (tarifGroupId != null) {
                    for (DomainObject tarifGroup : allTarifGroups) {
                        if (tarifGroup.getObjectId().equals(tarifGroupId)) {
                            return tarifGroup;
                        }
                    }
                }
                return null;
            }
        };

        DisableAwareDropDownChoice<DomainObject> tarifGroup =
                new DisableAwareDropDownChoice<DomainObject>("tarifGroup", model, allTarifGroups, tarifGroupRenderer);
        tarifGroup.setRequired(true);
        tarifGroup.setEnabled(enabled);
        tarifGroup.setLabel(tarifGroupLabelModel);
        add(tarifGroup);
    }
}
