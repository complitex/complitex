package org.complitex.osznconnection.file.entity;

import org.complitex.common.entity.LogChangeList;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 29.09.2010 14:31:02
 */
public class RequestFileGroup extends AbstractRequestFileGroup{

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

    @Override
    public String toString() {
        return "RequestFileGroup{" +
                "id=" + getId() +
                ", paymentFile=" + getPaymentFile() +
                ", benefitFile=" + getBenefitFile() +
                ", loadedRecordCount=" + getLoadedRecordCount() +
                ", bindedRecordCount=" + getBindedRecordCount() +
                ", filledRecordCount=" + getFilledRecordCount() +
                ", status=" + getStatus() +
                '}';
    }
}
