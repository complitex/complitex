package ru.complitex.pspoffice.address.model;

import org.apache.wicket.cdi.NonContextual;
import org.apache.wicket.model.IModel;
import ru.complitex.catalog.service.CatalogService;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @author Ivanov Anatoliy
 */
public class ReferenceModel implements IModel<Long> {
    @Inject
    private CatalogService catalogService;

    private final int catalog;
    private final IModel<Long> model;
    private final int value;
    private final LocalDate date;

    private Long itemId = null;
    private Long referenceId = null;

    public ReferenceModel(int catalog, IModel<Long> model, int value, LocalDate date) {
        this.catalog = catalog;
        this.model = model;
        this.value = value;
        this.date = date;
    }

    @Override
    public Long getObject() {
        Long itemId = model.getObject();

        if (!Objects.equals(this.itemId, itemId)) {
            this.itemId = itemId;

            if (catalogService == null) {
                NonContextual.of(this).inject(this);
            }

            referenceId = catalogService.getReferenceId(catalog, itemId, value, date);
        }

        return referenceId;
    }

    @Override
    public void setObject(Long referenceId) {
        this.referenceId = referenceId;
    }
}
