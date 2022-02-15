package ru.complitex.pspoffice.address.page;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.catalog.component.ItemGroup;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Locale;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.model.DataModel;
import ru.complitex.catalog.service.CatalogService;
import ru.complitex.pspoffice.address.entity.Organization;
import ru.complitex.pspoffice.address.entity.OrganizationType;

import javax.inject.Inject;

/**
 * @author Ivanov Anatoliy
 */
public class OrganizationPage extends AddressPage {
    @Inject
    private CatalogService catalogService;

    public OrganizationPage() {
        super(Organization.CATALOG);
    }

    @Override
    protected Component newReferenceGroup(String id, IModel<Item> model, Value value) {
        if (value.is(Organization.ORGANIZATION_TYPE)) {
            return new ItemGroup(id, new ResourceModel("organization_type"), OrganizationType.CATALOG,
                    new DataModel<>(model, value), OrganizationType.ORGANIZATION_TYPE_NAME, getDate());
        }

        return super.newReferenceGroup(id, model, value);
    }

    @Override
    protected boolean isRequired(Value value) {
        if (value.is(Organization.ORGANIZATION_NAME)) {
            return true;
        }

        return super.isRequired(value);
    }

    @Override
    protected boolean validate(Item item) {
        if (catalogService.getItemsCount(Organization.CATALOG, getDate())
                .withoutId(item.getId())
                .withReferenceId(Organization.ORGANIZATION_PARENT, item.getReferenceId(Organization.ORGANIZATION_PARENT))
                .withText(Organization.ORGANIZATION_NAME, Locale.SYSTEM, item.getText(Organization.ORGANIZATION_NAME, Locale.SYSTEM))
                .get() > 0) {
            error(getString("error_unique"));

            return false;
        }

        return super.validate(item);
    }
}
