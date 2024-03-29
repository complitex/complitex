package ru.complitex.keconnection.tarif.strategy.web.edit;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.EntityAttribute;
import ru.complitex.common.web.component.DisableAwareDropDownChoice;
import ru.complitex.common.web.component.DomainObjectComponentUtil;
import ru.complitex.common.web.component.DomainObjectDisableAwareRenderer;
import ru.complitex.common.web.component.domain.AbstractComplexAttributesPanel;
import ru.complitex.common.web.component.domain.DomainObjectAccessUtil;
import ru.complitex.keconnection.tarif.strategy.TarifGroupStrategy;
import ru.complitex.keconnection.tarif.strategy.TarifStrategy;

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

        final EntityAttribute tarifGroupEntityAttribute =
                tarifStrategy.getEntity().getAttribute(TarifStrategy.TARIF_GROUP);
        final IModel<String> tarifGroupLabelModel =
                DomainObjectComponentUtil.labelModel(tarifGroupEntityAttribute.getNames(), getLocale());
        add(new Label("tarifGroupLabel", tarifGroupLabelModel));

        WebMarkupContainer tarifGroupRequiredContainer = new WebMarkupContainer("tarifGroupRequiredContainer");
        tarifGroupRequiredContainer.setVisible(tarifGroupEntityAttribute.isRequired());
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
