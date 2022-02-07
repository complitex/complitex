package ru.complitex.correction.web.address.component;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.address.entity.AddressEntity;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.EntityObjectInfo;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.strategy.StrategyFactory;
import ru.complitex.common.web.component.ShowMode;
import ru.complitex.common.web.component.search.ISearchCallback;
import ru.complitex.common.web.component.search.SearchComponentState;
import ru.complitex.common.web.component.search.WiQuerySearchComponent;
import ru.complitex.correction.entity.Correction;

import javax.ejb.EJB;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Стандартная панель редактирования коррекции элемента адреса.
 */
public abstract class AddressCorrectionEditPanel extends AbstractCorrectionEditPanel {
    @EJB
    private StrategyFactory strategyFactory;

    private class Callback implements ISearchCallback, Serializable {

        private Correction correction;
        private String entity;

        private Callback(Correction correction, String entity) {
            this.correction = correction;
            this.entity = entity;
        }

        @Override
        public void found(Component component, Map<String, Long> ids, AjaxRequestTarget target) {
            Long id = ids.get(entity);
            if (id != null && id > 0) {
                correction.setObjectId(id);
            } else {
                correction.setObjectId(null);
            }
        }
    }

    public AddressCorrectionEditPanel(AddressEntity addressEntity, String id, Long correctionId) {
        super(addressEntity.getEntityName(), id, correctionId);
    }

    @Override
    protected IModel<String> internalObjectLabel(Locale locale) {
        return new ResourceModel("address");
    }

    @Override
    protected Panel internalObjectPanel(String id) {
        Correction correction = getCorrection();
        String entity = correction.getEntityName();
        SearchComponentState componentState = new SearchComponentState();
        if (!isNew()) {
            long objectId = correction.getObjectId();
            EntityObjectInfo info = getStrategy(entity).findParentInSearchComponent(objectId, null);
            if (info != null) {
                componentState = getStrategy(entity).getSearchComponentStateForParent(info.getId(), info.getEntityName(), null);
            }

            componentState.put(entity, findObject(objectId, entity));
        }

        return new WiQuerySearchComponent(id, componentState, getSearchFilters(), new Callback(correction, entity),
                ShowMode.ACTIVE, true);
    }

    @Override
    protected String getNullObjectErrorMessage() {
        return getString("address_required");
    }

    protected IStrategy getStrategy(String entity) {
        return strategyFactory.getStrategy(entity);
    }

    protected DomainObject findObject(long objectId, String entity) {
        return getStrategy(entity).getDomainObject(objectId, true);
    }

    protected abstract List<String> getSearchFilters();

    @Override
    protected PageParameters getBackPageParameters() {
        return new PageParameters();
    }
}
