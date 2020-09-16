package org.complitex.osznconnection.web;

import org.complitex.admin.web.ProfilePage;
import org.complitex.admin.web.UserEdit;
import org.complitex.admin.web.UserList;
import org.complitex.correction.web.address.*;
import org.complitex.correction.web.organization.OrganizationCorrectionEdit;
import org.complitex.correction.web.organization.OrganizationCorrectionList;
import org.complitex.correction.web.service.ServiceCorrectionList;
import org.complitex.logging.web.LogList;
import org.complitex.organization.strategy.web.edit.OrganizationEdit;
import org.complitex.osznconnection.file.web.ImportPage;
import org.complitex.osznconnection.file.web.file_description.RequestFileDescriptionPage;
import org.complitex.osznconnection.file.web.pages.account.PersonAccountList;
import org.complitex.osznconnection.file.web.pages.actualpayment.ActualPaymentList;
import org.complitex.osznconnection.file.web.pages.facility.*;
import org.complitex.osznconnection.file.web.pages.ownership.OwnershipCorrectionList;
import org.complitex.osznconnection.file.web.pages.payment.PaymentList;
import org.complitex.osznconnection.file.web.pages.privilege.*;
import org.complitex.osznconnection.file.web.pages.subsidy.*;
import org.complitex.sync.web.DomainSyncPage;
import org.complitex.template.web.ComplitexWebApplication;
import org.complitex.template.web.pages.ConfigEdit;
import org.complitex.template.web.pages.DomainObjectEdit;
import org.complitex.template.web.pages.DomainObjectList;
import org.complitex.template.web.pages.EntityDescription;
import org.complitex.template.web.pages.login.Login;

/**
 * @author inheaven on 001 01.10.15 17:01
 */
public class OsznWebApplication extends ComplitexWebApplication{
    @Override
    protected void init() {
        super.init();

        getDebugSettings().setAjaxDebugModeEnabled(false);
        getMarkupSettings().setStripWicketTags(true);

        //subsidy
        mountPage("/subsidy/request", GroupList.class);
        mountPage("/subsidy/request/payment/${request_file_id}", PaymentList.class);
        mountPage("/subsidy/request/benefit/${request_file_id}", BenefitList.class);

        mountPage("/subsidy/oschadbank/request", OschadbankRequestFileList.class);
        mountPage("/subsidy/oschadbank/request/${request_file_id}", OschadbankRequestList.class);

        mountPage("/subsidy/oschadbank/response", OschadbankResponseFileList.class);
        mountPage("/subsidy/oschadbank/response/${request_file_id}", OschadbankResponseList.class);

        mountPage("/subsidy/actual", ActualPaymentFileList.class);
        mountPage("/subsidy/actual/${request_file_id}", ActualPaymentList.class);

        mountPage("/subsidy", SubsidyFileList.class);
        mountPage("/subsidy/${request_file_id}", SubsidyList.class);

        mountPage("/subsidy/tarif", SubsidyTarifFileList.class);
        mountPage("/subsidy/tarif/${request_file_id}", SubsidyTarifList.class);

        mountPage("/subsidy/split/${request_file_id}/${subsidy_id}", SubsidySplitList.class);

        //privilege
        mountPage("/privilege/request", PrivilegeFileGroupList.class);
        mountPage("/privilege/dwelling/${request_file_id}", DwellingCharacteristicsList.class);
        mountPage("/privilege/service/${request_file_id}", FacilityServiceTypeList.class);

        mountPage("/privilege/form-2", FacilityForm2FileList.class);
        mountPage("/privilege/form-2/${request_file_id}", FacilityForm2List.class);

        mountPage("/privilege/local", FacilityLocalFileList.class);
        mountPage("/privilege/local/${request_file_id}", FacilityLocalList.class);

        mountPage("/privilege/janitor", FacilityLocalJanitorFileList.class);
        mountPage("/privilege/janitor/${request_file_id}", FacilityLocalJanitorList.class);

        mountPage("/privilege/compensation", FacilityLocalCompensationFileList.class);
        mountPage("/privilege/compensation/${request_file_id}", FacilityLocalCompensationList.class);

        mountPage("/privilege/street-type", FacilityStreetTypeFileList.class);
        mountPage("/privilege/street-type/${request_file_id}", FacilityStreetTypeList.class);

        mountPage("/privilege/street", FacilityStreetFileList.class);
        mountPage("/privilege/street/${request_file_id}", FacilityStreetList.class);

        mountPage("/privilege/tarif", FacilityTarifFileList.class);
        mountPage("/privilege/tarif/${request_file_id}", FacilityTarifList.class);

        mountPage("/privilege/prolongation", PrivilegeProlongationFileList.class); //todo add types
        mountPage("/privilege/prolongation/${request_file_id}", PrivilegeProlongationList.class);

        mountPage("/privilege/debt", DebtFileList.class);
        mountPage("/privilege/debt/${request_file_id}", DebtList.class);

        //correction
        mountPage("/correction/country", CountryCorrectionList.class);
        mountPage("/correction/region", RegionCorrectionList.class);
        mountPage("/correction/city_type", CityTypeCorrectionList.class);
        mountPage("/correction/city", CityCorrectionList.class);
        mountPage("/correction/district", DistrictCorrectionList.class);
        mountPage("/correction/street", StreetCorrectionList.class);
        mountPage("/correction/street-type", StreetTypeCorrectionList.class);
        mountPage("/correction/building", BuildingCorrectionList.class);
        mountPage("/correction/organization", OrganizationCorrectionList.class);
        mountPage("/correction/ownership", OwnershipCorrectionList.class);
        mountPage("/correction/account", PersonAccountList.class);
        mountPage("/correction/privilege", PrivilegeCorrectionList.class);
        mountPage("/correction/service", ServiceCorrectionList.class);

        mountPage("/correction/${entity}/${correction_id}", AddressCorrectionEdit.class);
        mountPage("/correction/organization/${correction_id}", OrganizationCorrectionEdit.class);

        //description
        mountPage("/description/${entity}", EntityDescription.class);

        //sync
        mountPage("/sync", DomainSyncPage.class);

        //admin
        mountPage("/user", UserList.class);
        mountPage("/user/${user_id}", UserEdit.class);
        mountPage("/domain/${entity}", DomainObjectList.class);
        mountPage("/domain/${entity}/${object_id}", DomainObjectEdit.class);
        mountPage("/organization/${object_id}", OrganizationEdit.class);
        mountPage("/description/file", RequestFileDescriptionPage.class);
        mountPage("/import", ImportPage.class);
        mountPage("/log", LogList.class);
        mountPage("/config", ConfigEdit.class);
        mountPage("/profile", ProfilePage.class);

        //login
        mountPage("/login", Login.class);
    }
}
