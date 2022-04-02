package ru.complitex.eirc.account.page;

import ru.complitex.address.entity.Flat;
import ru.complitex.address.entity.House;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.page.CatalogPage;
import ru.complitex.eirc.account.entity.Account;
import ru.complitex.eirc.account.entity.GivenName;
import ru.complitex.eirc.account.entity.Patronymic;
import ru.complitex.eirc.account.entity.Surname;

/**
 * @author Ivanov Anatoliy
 */
public class AccountPage extends CatalogPage {
    public AccountPage() {
        super(Account.CATALOG);
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
}
