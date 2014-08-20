package ru.flexpay.eirc.registry.entity.changing;

import ru.flexpay.eirc.dictionary.entity.DictionaryObject;

/**
 * @author Pavel Sknar
 */
public class ServiceProviderAccountAttrChanging extends DictionaryObject {
    private Long spaOldAttributePkId;
    private Long spaNewAttributePkId;
    private Long registryRecordContainerId;

    public ServiceProviderAccountAttrChanging() {
    }

    public ServiceProviderAccountAttrChanging(Long spaOldAttributePkId) {
        this.spaOldAttributePkId = spaOldAttributePkId;
    }

    public ServiceProviderAccountAttrChanging(Long spaOldAttributePkId, Long spaNewAttributePkId, Long registryRecordContainerId) {
        this.spaOldAttributePkId = spaOldAttributePkId;
        this.spaNewAttributePkId = spaNewAttributePkId;
        this.registryRecordContainerId = registryRecordContainerId;
    }

    public Long getSpaOldAttributePkId() {
        return spaOldAttributePkId;
    }

    public void setSpaOldAttributePkId(Long spaOldAttributePkId) {
        this.spaOldAttributePkId = spaOldAttributePkId;
    }

    public Long getSpaNewAttributePkId() {
        return spaNewAttributePkId;
    }

    public void setSpaNewAttributePkId(Long spaNewAttributePkId) {
        this.spaNewAttributePkId = spaNewAttributePkId;
    }

    public Long getRegistryRecordContainerId() {
        return registryRecordContainerId;
    }

    public void setRegistryRecordContainerId(Long registryRecordContainerId) {
        this.registryRecordContainerId = registryRecordContainerId;
    }

}
