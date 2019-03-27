package org.complitex.osznconnection.file.entity;

import org.complitex.common.entity.LogChangeList;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 25.08.2010 17:35:35
 *
 * Информация о файле запроса: имя, дата загрузки, организация, дата, количество записей, размер файла, статус.
 */
public class RequestFile extends AbstractRequestFile {
    private List<? extends AbstractRequest> requests;

    private BigDecimal sum;

    @Override
    public String getLogObjectName() {
        return getFullName();
    }

    public LogChangeList getLogChangeList() {
        return getLogChangeList(null);
    }

    public LogChangeList getLogChangeList(String collection) {
        LogChangeList logChangeList = new LogChangeList();

        logChangeList.add(collection, "id", getId())
                .add(collection, "group_id", getGroupId())
                .add(collection, "loaded", getLoaded())
                .add(collection, "directory", getDirectory())
                .add(collection, "name", getName())
                .add(collection, "organizationId", getOrganizationId())
                .add(collection, "beginDate", getBeginDate())
                .add(collection, "endDate", getEndDate())
                .add(collection, "dbfRecordCount", getDbfRecordCount())
                .add(collection, "length", getLength())
                .add(collection, "checkSum", getCheckSum())
                .add(collection, "bindedRecordCount", getBindedRecordCount())
                .add(collection, "filledRecordCount", getFilledRecordCount());

        return logChangeList;
    }

    public String getEdrpou(){
        if (getType().equals(RequestFileType.OSCHADBANK_REQUEST)){
            return getName().substring(0, getName().indexOf("_"));
        }

        Matcher matcher = getEdrpouPattern().matcher(getName());

        if (matcher.matches()){
            return matcher.group(2);
        }

        return null;
    }

    private Pattern getEdrpouPattern(){
        switch (getType()){
            case PAYMENT:
            case BENEFIT:
                return Pattern.compile("^(\\D*)(\\d*)(\\d{6})(\\..*)");
            case SUBSIDY:
                return Pattern.compile("^(\\D*)(\\d*)(\\d{4})(\\..*)");
            case DWELLING_CHARACTERISTICS:
            case FACILITY_SERVICE_TYPE:
                return Pattern.compile("^(\\D*)(\\d*)(\\..*)");

        }

        return Pattern.compile("^(\\D*)(.*)(\\..*)");
    }

    public List<? extends AbstractRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<? extends AbstractRequest> requests) {
        this.requests = requests;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }
}
