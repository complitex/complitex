package ru.flexpay.eirc.eirc_account.entity;

import org.complitex.common.util.DateUtil;
import ru.flexpay.eirc.dictionary.entity.Address;
import ru.flexpay.eirc.dictionary.entity.DictionaryTemporalObject;
import ru.flexpay.eirc.dictionary.entity.Person;

/**
 * @author Pavel Sknar
 */
public class EircAccount extends DictionaryTemporalObject {

    private String accountNumber;

    private Address address;

    private Person person;

    private Boolean createdFromRegistry;

    public EircAccount() {
        setBeginDate(DateUtil.getCurrentDate());
    }

    public EircAccount(Long objectId) {
        setId(objectId);
    }

    public EircAccount(Address address) {
        this.address = address;
    }

    public EircAccount(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public boolean isCreatedFromRegistry2() {
        return createdFromRegistry != null? createdFromRegistry : false;
    }

    public Boolean getCreatedFromRegistry() {
        return createdFromRegistry;
    }

    public void setCreatedFromRegistry(Boolean createdFromRegistry) {
        this.createdFromRegistry = createdFromRegistry;
    }
}
