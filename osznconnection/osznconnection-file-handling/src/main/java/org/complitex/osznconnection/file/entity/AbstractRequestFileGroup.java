package org.complitex.osznconnection.file.entity;

import org.complitex.common.util.DateUtil;

import java.util.Date;

/**
 * inheaven on 04.04.2016.
 */
public abstract class AbstractRequestFileGroup extends AbstractRequestFile{
    private RequestFileGroupType groupType;

    private RequestFile firstRequestFile;
    private RequestFile secondRequestFile;

    public RequestFileGroupType getGroupType() {
        return groupType;
    }

    public void setGroupType(RequestFileGroupType groupType) {
        this.groupType = groupType;
    }

    public RequestFile getFirstRequestFile() {
        return firstRequestFile;
    }

    public void setFirstRequestFile(RequestFile firstRequestFile) {
        this.firstRequestFile = firstRequestFile;
    }

    public RequestFile getSecondRequestFile() {
        return secondRequestFile;
    }

    public void setSecondRequestFile(RequestFile secondRequestFile) {
        this.secondRequestFile = secondRequestFile;
    }

    @Override
    public String getFullName() {
        return (firstRequestFile != null ? firstRequestFile.getFullName() + ", "  : "")
                + (secondRequestFile != null ? secondRequestFile.getFullName() : "");
    }

    public String getEdrpou(){
        return firstRequestFile != null ? firstRequestFile.getEdrpou() : secondRequestFile != null ? secondRequestFile.getEdrpou() : null;
    }

    public Date getLoaded(){
//        if (firstRequestFile != null && secondRequestFile != null){
//            return DateUtil.getMax(firstRequestFile.getLoaded(), secondRequestFile.getLoaded());
//        }

        return firstRequestFile != null ? firstRequestFile.getLoaded() : secondRequestFile != null ? secondRequestFile.getLoaded() : null;
    }

    public Long getOrganizationId(){
        return firstRequestFile != null ? firstRequestFile.getOrganizationId() : secondRequestFile != null ? secondRequestFile.getOrganizationId() : null;
    }

    public Long getUserOrganizationId(){
        return firstRequestFile != null ? firstRequestFile.getUserOrganizationId() : secondRequestFile != null ? secondRequestFile.getUserOrganizationId() : null;
    }

    public int getMonth(){
        if (firstRequestFile != null) return DateUtil.getMonth(firstRequestFile.getBeginDate()) + 1;
        if (secondRequestFile != null) return DateUtil.getMonth(secondRequestFile.getBeginDate()) + 1;
        return 0;
    }

    public int getYear(){
        if (firstRequestFile != null) return DateUtil.getYear(firstRequestFile.getBeginDate());
        if (secondRequestFile != null) return DateUtil.getYear(secondRequestFile.getBeginDate());
        return 0;
    }

    public String getName(){
        if (firstRequestFile != null && firstRequestFile.getName() != null){
            return firstRequestFile.getName().substring(2,8);
        }

        return null;
    }

    public String getDirectory(){
        if (firstRequestFile != null){
            return firstRequestFile.getDirectory();
        }

        return null;
    }

    @Override
    public void cancel() {
        super.cancel();

        if (firstRequestFile != null){
            firstRequestFile.cancel();
        }

        if (secondRequestFile != null){
            secondRequestFile.cancel();
        }
    }

    @Override
    public boolean isProcessing() {
        return (getStatus() != null && getStatus().isProcessing()) ||
                (firstRequestFile != null && firstRequestFile.isProcessing()) ||
                (secondRequestFile != null && secondRequestFile.isProcessing());
    }

    @Override
    public boolean isWaiting() {
        return (getStatus() != null && getStatus().isWaiting()) ||
                (firstRequestFile != null && firstRequestFile.isWaiting()) ||
                (secondRequestFile != null && secondRequestFile.isWaiting());
    }

    @Override
    public String toString() {
        return "RequestFileGroup{" +
                "groupType=" + groupType +
                ", firstRequestFile=" + firstRequestFile +
                ", secondRequestFile=" + secondRequestFile +
                "} " + super.toString();
    }
}
