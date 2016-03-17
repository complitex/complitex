package org.complitex.osznconnection.file.entity;

import org.complitex.common.entity.LogChangeList;
import org.complitex.common.util.DateUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 29.09.2010 14:31:02
 */
public class RequestFileGroup extends AbstractRequestFile{
    private RequestFile benefitFile;
    private RequestFile paymentFile;

    private int loadedRecordCount;
    private int bindedRecordCount;
    private int filledRecordCount;

    private RequestFileStatus status;

    public String getEdrpou(){
        return paymentFile != null ? paymentFile.getEdrpou() : benefitFile != null ? benefitFile.getEdrpou() : null;
    }

    @Override
    public String getLogObjectName() {
        return getFullName();
    }

    @Override
    public LogChangeList getLogChangeList() {
        LogChangeList logChangeList = new LogChangeList();

        if (paymentFile != null){
            logChangeList.addAll(paymentFile.getLogChangeList("payment"));
        }

        if (benefitFile != null){
            logChangeList.addAll(benefitFile.getLogChangeList("benefit"));
        }

        return logChangeList;
    }

    public String getFullName(){
        return getDirectory() + File.separator + getName();
    }

    public boolean isProcessing() {
       return RequestFileStatus.LOADING.equals(status)
               || RequestFileStatus.BINDING.equals(status)
               || RequestFileStatus.FILLING.equals(status)
               || RequestFileStatus.SAVING.equals(status);
    }

    public Date getLoaded(){
        if (paymentFile != null && benefitFile != null){            
            return DateUtil.getMax(paymentFile.getLoaded(), benefitFile.getLoaded());
        }

        return paymentFile != null ? paymentFile.getLoaded() : benefitFile != null ? benefitFile.getLoaded() : null;
    }

    public Long getOrganizationId(){
        return paymentFile != null ? paymentFile.getOrganizationId() : benefitFile != null ? benefitFile.getOrganizationId() : null;
    }
    
    public Long getUserOrganizationId(){
        return paymentFile != null ? paymentFile.getUserOrganizationId() : benefitFile != null ? benefitFile.getUserOrganizationId() : null;
    }

    public int getRegistry(){
        if (paymentFile != null) return paymentFile.getRegistry();
        if (benefitFile != null) return benefitFile.getRegistry();
        return 0;
    }

    public int getMonth(){
        if (paymentFile != null) return DateUtil.getMonth(paymentFile.getBeginDate()) + 1;
        if (benefitFile != null) return DateUtil.getMonth(benefitFile.getBeginDate()) + 1;
        return 0;
    }

    public int getYear(){
        if (paymentFile != null) return DateUtil.getYear(paymentFile.getBeginDate());
        if (benefitFile != null) return DateUtil.getYear(benefitFile.getBeginDate());
        return 0;
    }
    
    public List<RequestFile> getRequestFiles(){
        List<RequestFile> list = new ArrayList<RequestFile>(2);

        if (paymentFile != null) list.add(paymentFile);
        if (benefitFile != null) list.add(benefitFile);

        return list;
    }

    public String getName(){
        if (paymentFile != null && paymentFile.getName() != null){
            return paymentFile.getName().substring(2,8);
        }

        return null;
    }

    public String getDirectory(){
        if (paymentFile != null){
            return paymentFile.getDirectory();
        }

        return null;
    }

    @Override
    public String getObjectName() {
        return getFullName();
    }

    public RequestFile getBenefitFile() {
        return benefitFile;
    }

    public void setBenefitFile(RequestFile benefitFile) {
        this.benefitFile = benefitFile;
    }

    public RequestFile getPaymentFile() {
        return paymentFile;
    }

    public void setPaymentFile(RequestFile paymentFile) {
        this.paymentFile = paymentFile;
    }

    public int getLoadedRecordCount() {
        return loadedRecordCount;
    }

    public void setLoadedRecordCount(int loadedRecordCount) {
        this.loadedRecordCount = loadedRecordCount;
    }

    public int getBindedRecordCount() {
        return bindedRecordCount;
    }

    public void setBindedRecordCount(int bindedRecordCount) {
        this.bindedRecordCount = bindedRecordCount;
    }

    public int getFilledRecordCount() {
        return filledRecordCount;
    }

    public void setFilledRecordCount(int filledRecordCount) {
        this.filledRecordCount = filledRecordCount;
    }

    public RequestFileStatus getStatus() {
        return status;
    }

    public void setStatus(RequestFileStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "RequestFileGroup{" +
                "id=" + getId() +
                ", benefitFile=" + benefitFile +
                ", paymentFile=" + paymentFile +
                ", loadedRecordCount=" + loadedRecordCount +
                ", bindedRecordCount=" + bindedRecordCount +
                ", filledRecordCount=" + filledRecordCount +
                ", status=" + status +
                '}';
    }

    @Override
    public void cancel() {
        super.cancel();
        paymentFile.cancel();
        benefitFile.cancel();
    }
}
