package ru.complitex.pspoffice.address.model;

import org.apache.wicket.cdi.NonContextual;
import org.apache.wicket.model.IModel;
import ru.complitex.catalog.service.CatalogService;

import javax.inject.Inject;
import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class ReferenceModel implements IModel<Long> {
    @Inject
    private CatalogService catalogService;

    private final int catalog;
    private final IModel<Long> model;
    private final int value;
    private LocalDate date;

    private Long referenceId;

    public ReferenceModel(int catalog, IModel<Long> model, int value, LocalDate date) {
        this.catalog = catalog;
        this.model = model;
        this.value = value;
        this.date = date;
    }

    @Override
    public Long getObject() {
        if (referenceId == null) {
            Long itemId = model.getObject();

            if (itemId != null) {
                if (catalogService == null) {
                    NonContextual.of(this).inject(this);
                }

                referenceId = catalogService.getReferenceId(catalog, itemId, value, date);
            }
        }

        return referenceId;
    }

    @Override
    public void setObject(Long referenceId) {
        this.referenceId = referenceId;
    }
}
