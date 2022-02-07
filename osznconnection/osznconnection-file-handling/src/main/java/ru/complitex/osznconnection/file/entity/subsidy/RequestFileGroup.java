package ru.complitex.osznconnection.file.entity.subsidy;

import ru.complitex.common.entity.LogChangeList;
import ru.complitex.osznconnection.file.entity.AbstractRequestFileGroup;
import ru.complitex.osznconnection.file.entity.RequestFile;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 29.09.2010 14:31:02
 */
public class RequestFileGroup extends AbstractRequestFileGroup {
    @Override
    public LogChangeList getLogChangeList() {
        LogChangeList logChangeList = new LogChangeList();

        if (getPaymentFile() != null){
            logChangeList.addAll(getPaymentFile().getLogChangeList("payment"));
        }

        if (getBenefitFile() != null){
            logChangeList.addAll(getBenefitFile().getLogChangeList("benefit"));
        }

        return logChangeList;
    }

    public RequestFile getPaymentFile() {
        return getFirstRequestFile();
    }

    public void setPaymentFile(RequestFile paymentFile) {
        setFirstRequestFile(paymentFile);
    }

    public RequestFile getBenefitFile() {
        return getSecondRequestFile();
    }

    public void setBenefitFile(RequestFile benefitFile) {
        setSecondRequestFile(benefitFile);
    }
}
