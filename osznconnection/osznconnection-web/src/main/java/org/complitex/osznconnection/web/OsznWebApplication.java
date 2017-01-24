package org.complitex.osznconnection.web;

import org.complitex.address.web.sync.AddressSyncPage;
import org.complitex.admin.web.UserEdit;
import org.complitex.admin.web.UserList;
import org.complitex.organization.strategy.web.edit.OrganizationEdit;
import org.complitex.osznconnection.file.web.pages.actualpayment.ActualPaymentList;
import org.complitex.osznconnection.file.web.pages.facility.*;
import org.complitex.osznconnection.file.web.pages.payment.PaymentList;
import org.complitex.osznconnection.file.web.pages.privilege.*;
import org.complitex.osznconnection.file.web.pages.subsidy.*;
import org.complitex.template.web.ComplitexWebApplication;
import org.complitex.template.web.pages.DomainObjectList;
import org.complitex.template.web.pages.login.Login;

/**
 * @author inheaven on 001 01.10.15 17:01
 */
public class OsznWebApplication extends ComplitexWebApplication{
    @Override
    protected void init() {
        super.init();

        getDebugSettings().setAjaxDebugModeEnabled(false);

        //subsidy
        mountPage("/subsidy/request", GroupList.class);
        mountPage("/subsidy/request/payment/${request_file_id}", PaymentList.class);
        mountPage("/subsidy/request/benefit/${request_file_id}", BenefitList.class);

        mountPage("/subsidy/actual", ActualPaymentFileList.class);
        mountPage("/subsidy/actual/${request_file_id}", ActualPaymentList.class);

        mountPage("/subsidy/file", SubsidyFileList.class);
        mountPage("/subsidy/file/${request_file_id}", SubsidyList.class);

        mountPage("/subsidy/tarif", SubsidyTarifFileList.class);

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


        //correction

        //address

        //dictionary

        //sync
        mountPage("/address-sync", AddressSyncPage.class);

        //admin
        mountPage("/users", UserList.class);
        mountPage("/users/${user_id}", UserEdit.class);
        mountPage("/domains", DomainObjectList.class);
        mountPage("/organizations/${object_id}", OrganizationEdit.class);

        //login
        mountPage("/login", Login.class);

    }
}
