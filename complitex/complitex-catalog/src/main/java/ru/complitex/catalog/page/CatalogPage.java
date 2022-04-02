package ru.complitex.catalog.page;

import ru.complitex.catalog.component.CatalogPanel;
import ru.complitex.catalog.entity.Value;
import ru.complitex.ui.page.BasePage;

/**
 * @author Ivanov Anatoliy
 */
public class CatalogPage extends BasePage {
    public CatalogPage(int catalog) {
        add(new CatalogPanel("catalog", catalog) {
            @Override
            protected int getReferenceValueKeyId(Value value) {
                return CatalogPage.this.getReferenceValueKeyId(value);
            }
        });
    }

    protected int getReferenceValueKeyId(Value value) {
        return -1;
    }
}
