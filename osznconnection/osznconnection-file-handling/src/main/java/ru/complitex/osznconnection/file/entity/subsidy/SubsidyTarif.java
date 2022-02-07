package ru.complitex.osznconnection.file.entity.subsidy;

import ru.complitex.osznconnection.file.entity.AbstractRequest;
import ru.complitex.osznconnection.file.entity.RequestFileType;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.09.2010 18:14:28
 *
 * Файл тарифа.
 * Имя файла TARIF12.DBF
 */
public class SubsidyTarif extends AbstractRequest {
    public SubsidyTarif() {
        super(RequestFileType.SUBSIDY_TARIF);
    }

    public SubsidyTarif(Long requestFileId) {
        this();

        setRequestFileId(requestFileId);
    }
}
