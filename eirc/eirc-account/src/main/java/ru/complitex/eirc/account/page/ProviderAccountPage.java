package ru.complitex.eirc.account.page;

import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.page.CatalogPage;
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
            case ProviderAccount.SURNAME -> Surname.SURNAME;
            case ProviderAccount.GIVEN_NAME -> GivenName.GIVEN_NAME;
            case ProviderAccount.PATRONYMIC -> Patronymic.PATRONYMIC;

            default -> super.getReferenceValueKeyId(value);
        };
    }
}
