package ru.complitex.eirc.account.page;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import ru.complitex.address.component.group.HouseFlatGroup;
import ru.complitex.address.entity.Flat;
import ru.complitex.address.entity.House;
import ru.complitex.address.service.AddressService;
import ru.complitex.catalog.entity.Catalog;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.model.DataModel;
import ru.complitex.catalog.page.CatalogPage;
import ru.complitex.eirc.account.entity.Account;
import ru.complitex.eirc.account.entity.GivenName;
import ru.complitex.eirc.account.entity.Patronymic;
import ru.complitex.eirc.account.entity.Surname;

import javax.inject.Inject;
import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class AccountPage extends CatalogPage {
    @Inject
    private AddressService addressService;

    public AccountPage() {
        super(Account.CATALOG);
    }

    @Override
    protected IModel<String> getColumnModel(IModel<Item> model, Value value) {
        if (value.is(Account.FLAT)) {
            return () -> addressService.getFullFlatName(model.getObject().getReferenceId(Account.FLAT), getDate());
        }

        return super.getColumnModel(model, value);
    }

    @Override
    protected int getReferenceValueKeyId(Value value) {
        return switch (value.getKeyId()) {
            case Account.HOUSE -> House.HOUSE_NUMBER;
            case Account.FLAT -> Flat.FLAT_NUMBER;
            case Account.SURNAME -> Surname.SURNAME;
            case Account.GIVEN_NAME -> GivenName.GIVEN_NAME;
            case Account.PATRONYMIC -> Patronymic.PATRONYMIC;

            default -> super.getReferenceValueKeyId(value);
        };
    }

    @Override
    protected boolean isVisible(Value value) {
        return value.getKeyId() != Account.HOUSE;
    }

    @Override
    protected boolean isLongColumn(Value value) {
        return value.getKeyId() == Account.ACCOUNT_NUMBER;
    }

    @Override
    protected boolean isRequired(Value value) {
        return value.is(Account.ACCOUNT_NUMBER);
    }

    @Override
    protected Component newFormGroup(String id, Catalog catalog, Value value, IModel<Item> model) {
        if (value.getKeyId() == Account.FLAT) {
            return new HouseFlatGroup(id, new DataModel<>(model, catalog.getValue(Account.HOUSE)),
                    new DataModel<>(model, value), LocalDate.now());
        }

        return super.newFormGroup(id, catalog, value, model);
    }
}
