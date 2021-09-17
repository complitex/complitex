package ru.complitex.pspoffice.address.sync.page.component;

import ru.complitex.catalog.service.CatalogService;
import ru.complitex.pspoffice.address.sync.service.ISyncService;

import javax.inject.Inject;
import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class CatalogPanel extends SyncPanel {
    @Inject
    private CatalogService catalogService;

    private final int catalog;
    private final int correctionCatalog;
    private final int catalogOrganization;
    private final int correctionOrganization;
    private final int item;
    private final LocalDate date;

    public CatalogPanel(String id, int catalog, int correctionCatalog, int catalogOrganization, int correctionOrganization, int item, LocalDate date) {
        super(id);

        this.catalog = catalog;
        this.correctionCatalog = correctionCatalog;
        this.catalogOrganization = catalogOrganization;
        this.correctionOrganization = correctionOrganization;
        this.item = item;
        this.date = date;
    }

    protected Integer getSize() {
        return catalogService.getItemsCount(catalog, date).get().intValue();
    }

    protected Integer getCorrectionSize() {
        return catalogService.getItemsCount(correctionCatalog, date)
                .withReferenceId(catalogOrganization, ISyncService.CATALOG_ORGANIZATION)
                .withReferenceId(correctionOrganization, ISyncService.CORRECTION_ORGANIZATION)
                .get().intValue();
    }

    protected Integer getSyncedCorrection() {
        return catalogService.getItemsCount(correctionCatalog, date)
                .withNotNull(item)
                .withReferenceId(catalogOrganization, ISyncService.CATALOG_ORGANIZATION)
                .withReferenceId(correctionOrganization, ISyncService.CORRECTION_ORGANIZATION)
                .get().intValue();
    }
}
