package org.complitex.osznconnection.file.entity.subsidy;

import org.complitex.osznconnection.file.entity.AbstractAccountRequest;
import org.complitex.osznconnection.file.entity.RequestFileType;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.08.2010 18:22:55
 *
 * Запись файла запроса возмещения по льготам.
 * @see org.complitex.osznconnection.file.entity.AbstractRequest
 *
 * Имена полей фиксированы в <code>Enum<code> перечислении <code>BenefitDBF</code>
 * @see BenefitDBF
 */
public class Benefit extends AbstractAccountRequest<BenefitDBF> {
    public Benefit() {
        super(RequestFileType.BENEFIT);
    }

    public boolean hasPriv() {
        return getStringField(BenefitDBF.PRIV_CAT) != null;
    }
}
