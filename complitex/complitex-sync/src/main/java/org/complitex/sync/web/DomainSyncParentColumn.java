package org.complitex.sync.web;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.complitex.common.strategy.StrategyFactory;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.util.EjbBeanLocator;
import org.complitex.common.web.component.datatable.column.FilteredColumn;
import org.complitex.correction.entity.CityCorrection;
import org.complitex.correction.entity.OrganizationCorrection;
import org.complitex.correction.entity.StreetCorrection;
import org.complitex.correction.service.AddressCorrectionBean;
import org.complitex.correction.service.OrganizationCorrectionBean;
import org.complitex.sync.entity.DomainSync;
import org.complitex.sync.entity.SyncEntity;
import org.complitex.sync.service.DomainSyncAdapter;

import java.util.List;

/**
 * @author Anatoly Ivanov
 *         Date: 031 31.07.14 19:33
 */
public class DomainSyncParentColumn extends FilteredColumn<DomainSync>{

    public DomainSyncParentColumn(IModel<String> displayModel, String id) {
        super(displayModel, id);
    }

    public DomainSyncParentColumn(String id) {
        super(new ResourceModel(id), id);
    }

    @Override
    public Component getFilter(String componentId, FilterForm<?> form) {
        return new TextFilter<>(componentId, Model.of(""), form);
    }

    @Override
    public void populateItem(Item<ICellPopulator<DomainSync>> cellItem, String componentId, IModel<DomainSync> rowModel) {
        DomainSync domainSync = rowModel.getObject();

        String objectName = "";

        AddressCorrectionBean addressCorrectionBean = EjbBeanLocator.getBean(AddressCorrectionBean.class);
        Long organizationId = EjbBeanLocator.getBean(DomainSyncAdapter.class).getOrganization().getObjectId();

        if (domainSync.getParentId() != null) {
            objectName = "[" +domainSync.getParentId() + "]";

            if (domainSync.getType().equals(SyncEntity.DISTRICT) || domainSync.getType().equals(SyncEntity.STREET)){
                List<CityCorrection> cityCorrections = addressCorrectionBean.getCityCorrections(domainSync.getParentId(), organizationId);

                if (!cityCorrections.isEmpty()){
                    objectName = cityCorrections.get(0).getCorrection();
                }
            }else if (domainSync.getType().equals(SyncEntity.BUILDING) ){
                List<StreetCorrection> streetCorrections = addressCorrectionBean.getStreetCorrections(domainSync.getParentId(), organizationId);

                if (!streetCorrections.isEmpty()){
                    objectName = streetCorrections.get(0).getCorrection();
                }
            } else if (domainSync.getType().equals(SyncEntity.ORGANIZATION)){
                List<OrganizationCorrection> organizationCorrections = EjbBeanLocator.getBean(OrganizationCorrectionBean.class)
                        .getOrganizationCorrections(domainSync.getParentId(), null, organizationId);

                if (!organizationCorrections.isEmpty()){
                    Long orgId = organizationCorrections.get(0).getObjectId();

                    if (orgId != null) {
                        objectName = EjbBeanLocator.getBean(StrategyFactory.class).getStrategy("organization")
                                .getDomainObject(orgId)
                                .getStringValue(IOrganizationStrategy.SHORT_NAME);
                    }
                }
            }
        }

        cellItem.add(new Label(componentId, Model.of(objectName)));
    }
}
