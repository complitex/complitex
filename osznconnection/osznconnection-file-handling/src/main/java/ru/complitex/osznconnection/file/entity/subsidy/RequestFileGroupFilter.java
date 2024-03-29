package ru.complitex.osznconnection.file.entity.subsidy;

import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.service.AbstractFilter;
import ru.complitex.osznconnection.file.entity.RequestFileStatus;
import ru.complitex.osznconnection.file.entity.RequestFileType;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 03.11.10 13:24
 */
public class RequestFileGroupFilter extends AbstractFilter {

    private Long groupId;
    private Date loaded;
    private String name;
    private Integer registry;
    private Integer year;
    private Integer month;
    private String directory;
    private String paymentName;
    private String benefitName;

    private RequestFileType type;
    private RequestFileStatus status;

    private DomainObject serviceProvider;
    private String edrpou;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Date getLoaded() {
        return loaded;
    }

    public void setLoaded(Date loaded) {
        this.loaded = loaded;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRegistry() {
        return registry;
    }

    public void setRegistry(Integer registry) {
        this.registry = registry;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public String getBenefitName() {
        return benefitName;
    }

    public void setBenefitName(String benefitName) {
        this.benefitName = benefitName;
    }

    public RequestFileType getType() {
        return type;
    }

    public void setType(RequestFileType type) {
        this.type = type;
    }

    public RequestFileStatus getStatus() {
        return status;
    }

    public void setStatus(RequestFileStatus status) {
        this.status = status;
    }

    public DomainObject getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(DomainObject serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public String getEdrpou() {
        return edrpou;
    }

    public void setEdrpou(String edrpou) {
        this.edrpou = edrpou;
    }
}
