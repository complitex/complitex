package ru.complitex.osznconnection.file.service_provider;

/**
 *
 * @author Artem
 */
public class ServiceProviderAccountNumberInfo {

    private String serviceProviderId;
    private String serviceProviderAccountNumber;

    public ServiceProviderAccountNumberInfo(String serviceProviderId, String serviceProviderAccountNumber) {
        this.serviceProviderId = serviceProviderId;
        this.serviceProviderAccountNumber = serviceProviderAccountNumber;
    }

    public String getServiceProviderId() {
        return serviceProviderId;
    }

    public String getServiceProviderAccountNumber() {
        return serviceProviderAccountNumber;
    }
}
