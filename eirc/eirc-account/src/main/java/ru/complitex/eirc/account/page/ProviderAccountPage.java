package ru.complitex.eirc.account.page;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import ru.complitex.catalog.entity.Catalog;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.model.DataModel;
import ru.complitex.catalog.page.CatalogPage;
import ru.complitex.eirc.account.component.group.GivenNameGroup;
import ru.complitex.eirc.account.component.group.PatronymicGroup;
import ru.complitex.eirc.account.component.group.SurnameGroup;
import ru.complitex.eirc.account.entity.*;

/**
 * @author Ivanov Anatoliy
 */
public class ProviderAccountPage extends CatalogPage {
    public ProviderAccountPage() {
        super(ProviderAccount.CATALOG);
    }

    @Override
    protected int getReferenceValueKeyId(Value value) {
        return switch (value.getKeyId()) {
            case ProviderAccount.ACCOUNT -> Account.ACCOUNT_NUMBER;
            case ProviderAccount.SURNAME -> Surname.SURNAME;
            case ProviderAccount.GIVEN_NAME -> GivenName.GIVEN_NAME;
            case ProviderAccount.PATRONYMIC -> Patronymic.PATRONYMIC;

            default -> super.getReferenceValueKeyId(value);
        };
    }

    @Override
    protected boolean isLongColumn(Value value) {
        return value.is(ProviderAccount.PROVIDER_ACCOUNT_NUMBER);
    }

    @Override
    protected boolean isRequired(Value value) {
        return value.is(ProviderAccount.PROVIDER_ACCOUNT_NUMBER);
    }

    @Override
    protected Component newFormGroup(String id, Catalog catalog, Value value, IModel<Item> model) {
        return switch (value.getKeyId()) {
            case ProviderAccount.SURNAME -> new SurnameGroup(id, new DataModel<>(model, catalog.getValue(ProviderAccount.SURNAME)), getDate());
            case ProviderAccount.GIVEN_NAME -> new GivenNameGroup(id, new DataModel<>(model, catalog.getValue(ProviderAccount.GIVEN_NAME)), getDate());
            case ProviderAccount.PATRONYMIC -> new PatronymicGroup(id, new DataModel<>(model, catalog.getValue(ProviderAccount.PATRONYMIC)), getDate());

            default -> super.newFormGroup(id, catalog, value, model);
        };
    }
}
