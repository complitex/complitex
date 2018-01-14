package org.complitex.address.strategy.building.web.edit;

import com.google.common.collect.ImmutableMap;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.strategy.StrategyFactory;
import org.complitex.common.util.EjbBeanLocator;
import org.complitex.common.web.component.domain.DomainObjectEditPanel;
import org.complitex.common.web.component.domain.validate.IValidator;

import java.text.MessageFormat;
import java.util.Locale;

/**
 *
 * @author Artem
 */
public class BuildingValidator implements IValidator {

    private final Locale systemLocale;

    public BuildingValidator(Locale systemLocale) {
        this.systemLocale = systemLocale;
    }

    private BuildingEditComponent getEditComponent(DomainObjectEditPanel editPanel) {
        return editPanel.visitChildren(BuildingEditComponent.class,
                    new IVisitor<BuildingEditComponent, BuildingEditComponent>() {

                        @Override
                        public void component(BuildingEditComponent object,
                                              IVisit<BuildingEditComponent> visit) {
                            visit.stop(object);
                        }
                    });
    }

    @Override
    public boolean validate(DomainObject object, DomainObjectEditPanel editPanel) {

        return true;
    }


    private Long getCityId(DomainObject address) {
        if (address.getParentEntityId().equals(400L)) {
            return address.getParentId();
        } else if (address.getParentEntityId().equals(300L)) {
            Long streetId = address.getParentId();
            if (streetId != null && streetId > 0) {
                IStrategy streetStrategy = getStrategyFactory().getStrategy("street");
                DomainObject streetObject = streetStrategy.getDomainObject(streetId, true);
                return streetObject.getParentId();
            }
        }
        return null;
    }

    private Long getStreetId(DomainObject address) {
        if (address.getParentEntityId().equals(300L)) {
            return address.getParentId();
        }
        return null;
    }


    private void error(String key, Component component, Object... formatArguments) {
        if (formatArguments == null) {
            component.error(findEditComponent(component).getString(key));
        } else {
            component.error(MessageFormat.format(findEditComponent(component).getString(key), formatArguments));
        }

    }

    private void error(String key, Component component, IModel<?> model) {
        component.error(findEditComponent(component).getString(key, model));
    }
    private BuildingEditComponent editComponent;

    private BuildingEditComponent findEditComponent(Component component) {
        if (editComponent == null) {
            editComponent = component.getPage().visitChildren(BuildingEditComponent.class,
                    new IVisitor<BuildingEditComponent, BuildingEditComponent>() {

                        @Override
                        public void component(BuildingEditComponent object, IVisit<BuildingEditComponent> visit) {
                            visit.stop(object);
                        }
                    });
        }
        return editComponent;
    }


    private StrategyFactory getStrategyFactory() {
        return EjbBeanLocator.getBean(StrategyFactory.class);
    }

    private void printExistingAddressErrorMessage(long id, String number, String corp, String structure, Long parentId, Long parentEntityId,
            Locale locale, DomainObjectEditPanel editPanel) {
        String parentEntity = parentEntityId == null ? null : (parentEntityId == 300 ? "street" : (parentEntityId == 400 ? "city" : null));
        IStrategy strategy = getStrategyFactory().getStrategy(parentEntity);
        DomainObject parentObject = strategy.getDomainObject(parentId, true);
        String parentTitle = strategy.displayDomainObject(parentObject, editPanel.getLocale());

        IModel<?> model = Model.ofMap(ImmutableMap.builder().
                put("id", id).
                put("number", number).
                put("corp", corp).
                put("structure", structure).
                put("parent", parentTitle).
                put("locale", locale).
                build());

        String errorMessageKey = null;
        if (Strings.isEmpty(corp)) {
            if (Strings.isEmpty(structure)) {
                errorMessageKey = "address_exists_already_number";
            } else {
                errorMessageKey = "address_exists_already_number_structure";
            }
        } else {
            if (Strings.isEmpty(structure)) {
                errorMessageKey = "address_exists_already_number_corp";
            } else {
                errorMessageKey = "address_exists_already_number_corp_structure";
            }
        }
        error(errorMessageKey, editPanel, model);
    }
}
