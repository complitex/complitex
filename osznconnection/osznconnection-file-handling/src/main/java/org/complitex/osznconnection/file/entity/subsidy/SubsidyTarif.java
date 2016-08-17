package org.complitex.osznconnection.file.entity.subsidy;

import org.complitex.osznconnection.file.entity.AbstractRequest;
import org.complitex.osznconnection.file.entity.RequestFileType;

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
}
