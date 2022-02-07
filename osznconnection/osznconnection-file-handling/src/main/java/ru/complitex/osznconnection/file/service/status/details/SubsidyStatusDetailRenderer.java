package ru.complitex.osznconnection.file.service.status.details;

import ru.complitex.osznconnection.file.entity.RequestStatus;
import ru.complitex.osznconnection.file.entity.StatusDetail;

import java.io.Serializable;
import java.util.Locale;

public class SubsidyStatusDetailRenderer implements IStatusDetailRenderer, Serializable {

    @Override
    public String displayStatusDetail(StatusDetail statusDetail, RequestStatus status, Locale locale) {
        switch (status) {
            case ACCOUNT_NUMBER_NOT_FOUND:
            case MORE_ONE_ACCOUNTS: {
                return statusDetail.getDetail("fio");
            }
            case CITY_UNRESOLVED_LOCALLY:
            case CITY_UNRESOLVED:
            case CITY_NOT_FOUND: {
                return statusDetail.getDetail("city");
            }
            case STREET_UNRESOLVED_LOCALLY:
            case STREET_UNRESOLVED:
            case STREET_TYPE_UNRESOLVED_LOCALLY:
            case STREET_TYPE_UNRESOLVED:
            case STREET_NOT_FOUND:
            case STREET_TYPE_NOT_FOUND: {
                String city = statusDetail.getDetail("city");
                String street = statusDetail.getDetail("street");
                String streetType = statusDetail.getDetail("streetType");
                return city + ", " + street + ", " + streetType;
            }
            case BUILDING_UNRESOLVED_LOCALLY:
            case BUILDING_UNRESOLVED:
            case BUILDING_NOT_FOUND: {
                String city = statusDetail.getDetail("city");
                String street = statusDetail.getDetail("street");
                String streetType = statusDetail.getDetail("streetType");
                String building = statusDetail.getDetail("building");
                return city + ", " + street + ", " + streetType + ", " + building;
            }
            case BUILDING_CORP_NOT_FOUND: {
                String city = statusDetail.getDetail("city");
                String street = statusDetail.getDetail("street");
                String streetType = statusDetail.getDetail("streetType");
                String building = statusDetail.getDetail("building");
                String buildingCorp = statusDetail.getDetail("buildingCorp");
                return city + ", " + street + ", " + streetType + ", " + building + ", " + buildingCorp;
            }
            case APARTMENT_NOT_FOUND: {
                String city = statusDetail.getDetail("city");
                String street = statusDetail.getDetail("street");
                String streetType = statusDetail.getDetail("streetType");
                String building = statusDetail.getDetail("building");
                String buildingCorp = statusDetail.getDetail("buildingCorp");
                String apartment = statusDetail.getDetail("apartment");
                return city + ", " + street + ", " + streetType + ", " + building + ", " + buildingCorp + ", " + apartment;
            }
        }
        return "";
    }
}
