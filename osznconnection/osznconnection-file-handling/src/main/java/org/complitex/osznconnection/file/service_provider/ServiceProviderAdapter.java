package org.complitex.osznconnection.file.service_provider;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.util.string.Strings;
import org.complitex.address.exception.RemoteCallException;
import org.complitex.common.entity.Cursor;
import org.complitex.common.exception.DBRuntimeException;
import org.complitex.common.service.AbstractBean;
import org.complitex.common.util.MapUtil;
import org.complitex.organization.strategy.OrganizationStrategy;
import org.complitex.organization.strategy.ServiceStrategy;
import org.complitex.osznconnection.file.entity.*;
import org.complitex.osznconnection.file.entity.privilege.FacilityForm2;
import org.complitex.osznconnection.file.entity.privilege.FacilityLocal;
import org.complitex.osznconnection.file.entity.privilege.PrivilegeProlongation;
import org.complitex.osznconnection.file.entity.subsidy.*;
import org.complitex.osznconnection.file.service.privilege.OwnershipCorrectionBean;
import org.complitex.osznconnection.file.service.subsidy.SubsidyTarifBean;
import org.complitex.osznconnection.file.service.warning.RequestWarningBean;
import org.complitex.osznconnection.file.service_provider.exception.UnknownAccountNumberTypeException;
import org.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.complitex.address.util.AddressUtil.replaceApartmentSymbol;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ServiceProviderAdapter extends AbstractBean {
    private final Logger log = LoggerFactory.getLogger(ServiceProviderAdapter.class);

    private static final String NS = ServiceProviderAdapter.class.getName();

    @EJB
    private OwnershipCorrectionBean ownershipCorrectionBean;

    @EJB
    private SubsidyTarifBean subsidyTarifBean;

    @EJB
    private RequestWarningBean warningBean;

    @EJB
    private OsznOrganizationStrategy organizationStrategy;


    /**
     * Получить номер личного счета в ЦН.
     *
     * Обработка возвращаемых значений при получении л/с.
     */
    public AccountDetail acquireAccountDetail(AbstractAccountRequest request, String lastName,
                                              String puAccountNumber, String district, String organizationCode, String streetType,
                                              String street, String buildingNumber, String buildingCorp, String apartment,
                                              Date date, Boolean updatePUAccount){
        String dataSource = organizationStrategy.getDataSourceByUserOrganizationId(request.getUserOrganizationId());

        request.setStatus(RequestStatus.ACCOUNT_NUMBER_MISMATCH);

        if (Strings.isEmpty(puAccountNumber)) {
            puAccountNumber = "0";
        }

        puAccountNumber = puAccountNumber.trim();

        //из номера л/с из записи исключаются лидирующие нули
        puAccountNumber = puAccountNumber.replaceFirst("^0+(?!$)", "");

        //z$runtime_sz_utl.getAccAttrs()
        Cursor<AccountDetail> cursor = getAccountDetails(dataSource, district, organizationCode, streetType,
                street, buildingNumber, buildingCorp, apartment, date);

        if (cursor.isEmpty()) {
            updateCursorResultCode(request, cursor);

            return null;
        }

        for (AccountDetail accountDetail : cursor.getData()) {
            if (puAccountNumber.equals(accountDetail.getAccCode())
                    || puAccountNumber.equals(accountDetail.getErcCode())
                    || puAccountNumber.equals(accountDetail.getZheuCode())){
                request.setAccountNumber(accountDetail.getAccCode());
                request.setStatus(RequestStatus.ACCOUNT_NUMBER_RESOLVED);

                return accountDetail;
            }

            //zheu code.account
            if (accountDetail.getZheuCode() != null) {
                String[] zheuCodeAccount = accountDetail.getZheuCode().split("\\.");

                if (zheuCodeAccount.length == 2 &&
                        ((puAccountNumber.length() >= zheuCodeAccount[0].length() + zheuCodeAccount[1].length() &&
                                puAccountNumber.startsWith(zheuCodeAccount[0]) &&
                                puAccountNumber.endsWith(zheuCodeAccount[1])) ||
                                puAccountNumber.equals(zheuCodeAccount[1]))){
                    request.setAccountNumber(accountDetail.getAccCode());
                    request.setStatus(RequestStatus.ACCOUNT_NUMBER_RESOLVED);

                        return accountDetail;
                    }
            }
        }

        if (cursor.getData().size() == 1) {
            // если установлена опция перезаписи номера л/с ПУ номером л/с МН и номер л/с ПУ в файле запроса равен 0
            // и получена только одна запись из МН для данного адреса, то запись считаем связанной
            if (updatePUAccount && 0 == Long.valueOf(puAccountNumber)) {

                request.setAccountNumber(cursor.getData().get(0).getAccCode());
                request.setStatus(RequestStatus.ACCOUNT_NUMBER_RESOLVED);

                return cursor.getData().get(0);
            } else {
                request.setStatus(RequestStatus.ACCOUNT_NUMBER_MISMATCH);
            }
        } else {
            request.setStatus(RequestStatus.MORE_ONE_ACCOUNTS);
        }

        return null;
    }

    public void acquireFacilityPersonAccount(AbstractAccountRequest request,
                                             String district, String organizationCode, String streetType, String street, String buildingNumber,
                                             String buildingCorp, String apartment, Date date, String inn,
                                             String passport){
        String dataSource = organizationStrategy.getDataSourceByUserOrganizationId(request.getUserOrganizationId());

        Cursor<AccountDetail> cursor = getAccountDetails(dataSource, district, organizationCode, streetType, street, buildingNumber,
                buildingCorp, apartment, date);

        if (cursor.isEmpty()) {
            updateCursorResultCode(request, cursor);

            return;
        }

        for (AccountDetail accountDetail : cursor.getData()) {
            Cursor<Lodger> lodgerCursor = getLodgers(request.getUserOrganizationId(), accountDetail.getAccCode(), date);

            if (!lodgerCursor.isEmpty() && cursor.getResultCode() == 1) {
                for (Lodger l : lodgerCursor.getData()){
                    if ((l.getIdCode() != null && !l.getIdCode().isEmpty() && l.getIdCode().equals(inn)) ||
                            (passport != null && !passport.isEmpty() && l.getPassport() != null && !l.getPassport().isEmpty() &&
                                    passport.replace(" ", "")
                                            .equalsIgnoreCase(l.getPassport().replace(" ", "")))){
                        request.setAccountNumber(accountDetail.getAccCode());
                        request.setStatus(RequestStatus.ACCOUNT_NUMBER_RESOLVED);

                        return;
                    }
                }
            }
        }
    }

    public void checkLodgerPerson(AbstractAccountRequest request, String accountNumber, Date date, String inn, String passport){
        Cursor<Lodger> cursor = getLodgers(request.getUserOrganizationId(), accountNumber, date);

        if (cursor.isEmpty() && cursor.getResultCode() == 1){
            warningBean.save(request.getRequestFileType(), request.getId(), RequestWarningStatus.EMPTY_BENEFIT_DATA);
        }

        if (cursor.getData() != null) {
            for (Lodger l : cursor.getData()){
                if ((l.getIdCode() != null && !l.getIdCode().isEmpty() && l.getIdCode().equals(inn)) ||
                        (passport != null && !passport.isEmpty() && l.getPassport() != null && !l.getPassport().isEmpty() &&
                                passport.replace(" ", "")
                                        .equalsIgnoreCase(l.getPassport().replace(" ", "")))){
                    request.setAccountNumber(accountNumber);
                    request.setStatus(RequestStatus.ACCOUNT_NUMBER_RESOLVED);

                    return;
                }
            }

            if (cursor.getData().size() == 1){
                Lodger d = cursor.getData().get(0);

                warningBean.save(request.getRequestFileType(), request.getId(), RequestWarningStatus.BENEFIT_OWNER_NOT_ASSOCIATED,
                        new RequestWarningParameter(0, d.getIdCode()),
                        new RequestWarningParameter(1, d.getPassport()),
                        new RequestWarningParameter(2, ""));
            }
        }

        log.info("checkFacilityPerson BENEFIT_OWNER_NOT_ASSOCIATED accountNumber={}, inn='{}', passport='{}', data={}",
                accountNumber, inn, passport, cursor.getData());

        request.setStatus(RequestStatus.BENEFIT_OWNER_NOT_ASSOCIATED);
    }

    public void checkFacilityPerson(AbstractAccountRequest request, String accountNumber, Date date, String inn, String passport){
        Cursor<BenefitData> cursor = getBenefitData(request.getUserOrganizationId(), accountNumber, date);

        if (cursor.isEmpty() && cursor.getResultCode() == 1){
            warningBean.save(request.getRequestFileType(), request.getId(), RequestWarningStatus.EMPTY_BENEFIT_DATA);
        }

        if (cursor.getData() != null) {
            for (BenefitData d : cursor.getData()){
                if ((d.getInn() != null && !d.getInn().isEmpty() && d.getInn().equals(inn)) ||
                        (passport != null && !passport.isEmpty() && passport.matches(d.getPassportSerial() + "\\s*" + d.getPassportNumber()))){
                    request.setAccountNumber(accountNumber);
                    request.setStatus(RequestStatus.ACCOUNT_NUMBER_RESOLVED);

                    return;
                }
            }

            if (cursor.getData().size() == 1){
                BenefitData d = cursor.getData().get(0);

                warningBean.save(request.getRequestFileType(), request.getId(), RequestWarningStatus.BENEFIT_OWNER_NOT_ASSOCIATED,
                        new RequestWarningParameter(0, d.getInn()),
                        new RequestWarningParameter(1, d.getPassportSerial()),
                        new RequestWarningParameter(2, d.getPassportNumber()));
            }
        }

        log.info("checkFacilityPerson BENEFIT_OWNER_NOT_ASSOCIATED accountNumber={}, inn='{}', passport='{}', data={}",
                accountNumber, inn, passport, cursor.getData());

        request.setStatus(RequestStatus.BENEFIT_OWNER_NOT_ASSOCIATED);
    }

    private void updateCursorResultCode(AbstractAccountRequest request, Cursor<AccountDetail> cursor) {
        switch (cursor.getResultCode()){
            case -13:
                request.setStatus(RequestStatus.SERVICING_ORGANIZATION_NOT_FOUND);
                break;
            case -11:
                request.setStatus(RequestStatus.APARTMENT_NOT_FOUND);
                break;
            case -9:
                request.setStatus(RequestStatus.BUILDING_CORP_NOT_FOUND);
                break;
            case -8:
                request.setStatus(RequestStatus.BUILDING_NOT_FOUND);
                break;
            case -7:
                request.setStatus(RequestStatus.STREET_NOT_FOUND);
                break;
            case -6:
                request.setStatus(RequestStatus.STREET_TYPE_NOT_FOUND);
                break;
            case -5:
                request.setStatus(RequestStatus.DISTRICT_NOT_FOUND);
                break;
            case 0:
                request.setStatus(RequestStatus.ACCOUNT_NUMBER_NOT_FOUND);
                break;
        }
    }

    /**
     * Процедура COMP.Z$RUNTIME_SZ_UTL.GETACCATTRS.
     * Используется для уточнения в UI номера л/c, когда больше одного человека в ЦН, имеющие разные номера л/c,
     * привязаны к одному адресу и для поиска номеров л/c в PaymentLookupPanel.
     * См. также PaymentLookupBean.getAccounts().
     *
     * При возникновении ошибок при вызове процедуры проставляется статус RequestStatus.ACCOUNT_NUMBER_NOT_FOUND.
     * Так сделано потому, что проанализировать возвращаемое из процедуры значение не удается если номер л/c не найден
     * в ЦН по причине того что курсор в этом случае закрыт,
     * и драйвер в соответствии со стандартом JDBC рассматривает закрытый курсор как ошибку и выбрасывает исключение.
     *
     * Обработка возвращаемых значений при получении л/с.
     * Реквизиты лицевого счёта.
     * Возвращает: 1 либо код ошибки (INTEGER):
     * -13 Не найдена организация
     * -11 Не найдена квартира
     * -9 Не найден корпус дома
     * -8 Не найден дом
     * -7 Не найдена улица
     * -6 Не найден тип улицы
     * -5 Не найден район
     * 0 Не найдено ни одного лицевого счёта
     *
     * @return AccountDetails
     */
    @SuppressWarnings("unchecked")
    public Cursor<AccountDetail> getAccountDetails(String dataSource, String district, String organizationCode, String streetType,
                                                   String street, String buildingNumber, String buildingCorp,
                                                   String apartment, Date date){
        //^0+(?!$)
        apartment = replaceApartmentSymbol(apartment);

        Map<String, Object> params = newHashMap();

        params.put("pDistrName", district);
        params.put("pOrgCode", organizationCode);
        params.put("pStSortName", streetType);
        params.put("pStreetName", street);
        params.put("pHouseNum", buildingNumber);
        params.put("pHousePart", buildingCorp != null ? buildingCorp : "");
        params.put("pFlatNum", apartment);
        params.put("date", date);

        try {
            sqlSession(dataSource).selectOne(NS + ".getAccAttrs", params);
        } catch (Exception e) {
            throw new DBRuntimeException(e);
        } finally {
            log.info("getAccountDetails getAccAttrs: {}", params);
        }

        return new Cursor<>((Integer) params.get("resultCode"), (List<AccountDetail>) params.get("details"));
    }

    @SuppressWarnings("unchecked")
    public List<AccountDetail> acquireAccountDetailsByAccount(AbstractRequest request, String district,
                                                              String organizationCode, String account, Date date)
            throws UnknownAccountNumberTypeException {
        String dataSource = organizationStrategy.getDataSourceByUserOrganizationId(request.getUserOrganizationId());

        int accountType = determineAccountType(account);

        Map<String, Object> map = newHashMap();
        map.put("pDistrName", district);
        map.put("pOrgCode", organizationCode);
        map.put("pAccCode", account);
        map.put("pAccCodeType", accountType);
        map.put("date", date);

        try {
            sqlSession(dataSource).selectOne(NS + ".getAttrsByAccCode", map);
        }catch (Exception e){
            throw new DBRuntimeException(e);
        }finally {
            log.info("acquireAccountDetailsByAccount getAttrsByAccCode {}", map);
        }

        Integer resultCode = (Integer) map.get("resultCode");
        List<AccountDetail> accountDetails = (List<AccountDetail>) map.get("details");

        if (resultCode == null || (resultCode == 1 && (accountDetails == null || accountDetails.isEmpty()))) {
            request.setStatus(RequestStatus.PROCESSING_INVALID_FORMAT);

            log.error("getAttrsByAccCode error code {} {}", resultCode, map);
        }else if (resultCode <= 0){
            switch (resultCode) {
                case -14:
                    request.setStatus(RequestStatus.PROCESSING_INVALID_FORMAT);
                    break;
                case -13:
                    request.setStatus(RequestStatus.SERVICING_ORGANIZATION_NOT_FOUND);
                    break;
                case -5:
                    request.setStatus(RequestStatus.DISTRICT_NOT_FOUND);
                    break;
                case 0:
                    request.setStatus(RequestStatus.ACCOUNT_NUMBER_NOT_FOUND);
                    break;
                default:
                    request.setStatus(RequestStatus.PROCESSING_INVALID_FORMAT);
            }

            log.error("getAttrsByAccCode error {} {}", resultCode, map);
        }

        return accountDetails;
    }

    public Cursor<AccountDetail> getAccountDetailsByPerson(String dataSource, String districtName, String organizationCode,
                                                         String lastName, String firstName, String middleName,
                                                         String inn, String passport, Date date){
        Map<String, Object> params = new HashMap<>();

        params.put("districtName", districtName);
        params.put("organizationCode", organizationCode);
        params.put("lastName", lastName);
        params.put("firstName", firstName);
        params.put("middleName", middleName);
        params.put("inn", inn);
        params.put("passport", passport);
        params.put("date", date);

        try {
            sqlSession(dataSource).selectOne(NS + ".getAttrsByPerson", params);
        } catch (Exception e) {
            throw new DBRuntimeException(e);
        } finally {
            log.info("getAttrsByPerson GetAttrsByPerson {}", params);
        }

        //noinspection unchecked
        return new Cursor<>((Integer) params.get("resultCode"), (List<AccountDetail>) params.get("accountDetails"));
    }

    /**
     * Обработать payment и заполнить некоторые поля в соответствующих данному payment benefit записях.
     * Процедура COMP.Z$RUNTIME_SZ_UTL.GETCHARGEANDPARAMS.
     *
     * При возникновении ошибок при вызове процедуры проставляется статус RequestStatus.ACCOUNT_NUMBER_NOT_FOUND.
     * Так сделано потому, что проанализировать возвращаемое из процедуры значение не удается если номер л/c не найден
     * в ЦН по причине того что курсор в этом случае закрыт, и драйвер с соотвествии со стандартом JDBC рассматривает
     * закрытый курсор как ошибку и выбрасывает исключение.
     *
     */
    public void processPaymentAndBenefit(Long billingId, Set<Long> serviceIds, Payment payment, List<Benefit> benefits){
        if (payment.getAccountNumber() == null){
            return;
        }

        String dataSource = organizationStrategy.getDataSource(billingId);

        /* Set OPP field */
        char[] opp = new char[8];

        for (int i = 0; i < 8; i++){
            opp[i] = '0';

        }

        for (long spt : serviceIds) {
            if (spt >= 1 && spt <= 8) {
                int i = 8 - (int) spt;
                opp[i] = '1';
            }
        }
        payment.putField(PaymentDBF.OPP, String.valueOf(opp));
//        payment.putField(PaymentDBF.OPP_SERV, String.valueOf(opp));

        Map<String, Object> params = newHashMap();
        params.put("accountNumber", payment.getAccountNumber());
        params.put("dat1", payment.getField(PaymentDBF.DAT1));

        try {
            sqlSession(dataSource).selectOne(NS + ".getChargeAndParams", params);
        } catch (Exception e) {
            throw new DBRuntimeException(e);
        } finally {
            log.info("processPaymentAndBenefit getChargeAndParams {}", params);
        }

        Integer resultCode = (Integer) params.get("resultCode");
        if (resultCode == null) {
            payment.setStatus(RequestStatus.PROCESSING_INVALID_FORMAT);

            log.error("processPaymentAndBenefit. Result code is null. Payment id: {}, calculation center: {}",
                    payment.getId(), dataSource);
        } else {
            switch (resultCode) {
                case 1:
                    @SuppressWarnings("unchecked")
                    List<PaymentAndBenefitData> paymentAndBenefitDatas = (List<PaymentAndBenefitData>) params.get("data");

                    if (paymentAndBenefitDatas != null && !paymentAndBenefitDatas.isEmpty()) {
                        PaymentAndBenefitData data = paymentAndBenefitDatas.get(0);
                        if (paymentAndBenefitDatas.size() > 1) {
                            log.warn("processPaymentAndBenefit. Size of list of paymentAndBenefitData is more than 1. Only first entry will be used."
                                    + "Calculation center: {}", dataSource);
                        }
                        processPaymentAndBenefitData(billingId, serviceIds, payment, benefits, data);
                    } else {
                        payment.setStatus(RequestStatus.PROCESSING_INVALID_FORMAT);

                        log.error("processPaymentAndBenefit. Result code is 1 but paymentAndBenefitData is null or empty. Payment id: {},"
                                + "calculation center: {}", payment.getId(), dataSource);
                    }
                    break;
                case 0:
                    payment.setStatus(RequestStatus.ACCOUNT_NUMBER_NOT_FOUND);
                    break;
                default:
                    payment.setStatus(RequestStatus.PROCESSING_INVALID_FORMAT);

                    log.error("processPaymentAndBenefit. Unexpected result code: {}. Payment id: {}, calculation center: {}",
                            resultCode, payment.getId(), dataSource);
            }
        }
    }

    /**
     * Заполнить payment и benefit записи данными из processPaymentAndBenefit().
     * Для payment:
     * Все поля кроме CODE2_1 проставляются напрямую из data в payment.
     * Поле CODE2_1 заполняются не напрямую, а по таблице тарифов(метод TarifBean.getCODE2_1()).
     * Если в тарифах не нашли, то проставляем статус RequestStatus.TARIF_CODE2_1_NOT_FOUND и значение из ЦН(T11_CS_UNI) в
     * сalculationCenterCode2_1 для дальнейшего отображения в UI деталей. Иначе тариф сохраняется в payment и он считается обработанным.
     *
     * Для benefit:
     * Все поля проставляются напрямую, кроме:
     * поле OWN_FRM проставляется из таблицы коррекций для форм власти(ownership). На данный момент для всех форм власти в ЦН существуют коррекции,
     * поэтому ситуации с не найденной коррекцией нет.
     *
     */
    private void processPaymentAndBenefitData(Long billingId, Set<Long> services, Payment payment,
                                              List<Benefit> benefits, PaymentAndBenefitData data) {
        //payment
        //fields common for all service provider types
        payment.putField(PaymentDBF.FROG, data.getPercent());
        payment.putField(PaymentDBF.FL_PAY, data.getApartmentFeeCharge());
        payment.putField(PaymentDBF.NM_PAY, data.getNormCharge());
        payment.putField(PaymentDBF.DEBT, data.getSaldo());
        payment.putField(PaymentDBF.NUMB, data.getLodgerCount());
        payment.putField(PaymentDBF.MARK, data.getUserCount());
        payment.putField(PaymentDBF.NORM_F_1, data.getReducedArea());
//        payment.putField(PaymentDBF.NORM_F_1, data.getApartmentFeeTarif());

        /*
         * Если модуль начислений предоставляет более одной услуги, то возможна ситуация, когда 
         * по одной услуге тариф обработан успешно, т.е. метод handleTarif(...) вернул true, а по другой 
         * услуге тариф обработан с ошибкой. В этом случае обработка тарифов для оставшихся услуг не происходит,
         * ошибка логируется и программа переходит к обработке benefits записей, соответствующих заданному payment.
         */
        //статус успешности обработки тарифов.
        boolean tarifHandled = true;
        //тариф МН, при обработке которого возникла ошибка.
        BigDecimal errorTarif = null;

        //apartment fee
        if (services.contains(ServiceStrategy.APARTMENT_FEE)) {
            payment.putField(PaymentDBF.NORM_F_1, data.getReducedArea());
            if (!handleSubsidyService(payment, PaymentDBF.CODE2_1, data.getApartmentFeeTarif(), 1) ||
                    !handleSubsidyTarif(payment, PaymentDBF.CODE3_1, data.getApartmentFeeTarif(), 1)) {
                tarifHandled = false;
                errorTarif = data.getApartmentFeeTarif();

//                Integer code = subsidyTarifBean.getCode2Apartment(payment.getOrganizationId(), payment.getUserOrganizationId());
//
//                if (code != null){
//                    payment.putField(PaymentDBF.CODE2_1, code);
//                    payment.putField(PaymentDBF.CODE3_1, 1);
//                }else{
//                    payment.putField(PaymentDBF.CODE2_1, 0);
//                    payment.putField(PaymentDBF.CODE3_1, 0);
//                }
            }
        }
        //heating
        if (tarifHandled
                && services.contains(ServiceStrategy.HEATING)) {
            payment.putField(PaymentDBF.NORM_F_2, data.getHeatingArea());
            if (!handleSubsidyService(payment, PaymentDBF.CODE2_2, data.getHeatingTarif(), 2) ||
                    !handleSubsidyTarif(payment, PaymentDBF.CODE3_2, data.getHeatingTarif(), 2)) {
                tarifHandled = false;
                errorTarif = data.getHeatingTarif();
            }
        }
        //hot water
        if (tarifHandled
                && services.contains(ServiceStrategy.HOT_WATER_SUPPLY)) {
            payment.putField(PaymentDBF.NORM_F_3, data.getChargeHotWater());
            if (!handleSubsidyService(payment, PaymentDBF.CODE2_3, data.getHotWaterTarif(), 3) ||
                    !handleSubsidyTarif(payment, PaymentDBF.CODE3_3, data.getHotWaterTarif(), 3)) {
                tarifHandled = false;
                errorTarif = data.getHotWaterTarif();
            }
        }
        //cold water
        if (tarifHandled
                && services.contains(ServiceStrategy.COLD_WATER_SUPPLY)) {
            payment.putField(PaymentDBF.NORM_F_4, data.getChargeColdWater());
            if (!handleSubsidyService(payment, PaymentDBF.CODE2_4, data.getColdWaterTarif(), 4) ||
                    !handleSubsidyTarif(payment, PaymentDBF.CODE3_4, data.getColdWaterTarif(), 4)) {
                tarifHandled = false;
                errorTarif = data.getColdWaterTarif();
            }
        }
        //gas
        if (tarifHandled
                && services.contains(ServiceStrategy.GAS_SUPPLY)) {
            payment.putField(PaymentDBF.NORM_F_5, data.getChargeGas());
            if (!handleSubsidyService(payment, PaymentDBF.CODE2_5, data.getGasTarif(), 5) ||
                    !handleSubsidyTarif(payment, PaymentDBF.CODE3_5, data.getGasTarif(), 5)) {
                tarifHandled = false;
                errorTarif = data.getGasTarif();
            }
        }
        //power
        if (tarifHandled
                && services.contains(ServiceStrategy.POWER_SUPPLY)) {
            payment.putField(PaymentDBF.NORM_F_6, data.getChargePower());
            if (!handleSubsidyService(payment, PaymentDBF.CODE2_6, data.getPowerTarif(), 6) ||
                    !handleSubsidyTarif(payment, PaymentDBF.CODE3_6, data.getPowerTarif(), 6)) {
                tarifHandled = false;
                errorTarif = data.getPowerTarif();
            }
        }
        //garbage disposal
        if (tarifHandled
                && services.contains(ServiceStrategy.GARBAGE_DISPOSAL)) {
            payment.putField(PaymentDBF.NORM_F_7, data.getChargeGarbageDisposal());
            if (!handleSubsidyService(payment, PaymentDBF.CODE2_7, data.getGarbageDisposalTarif(), 7) ||
                    !handleSubsidyTarif(payment, PaymentDBF.CODE3_7, data.getGarbageDisposalTarif(), 7)) {
                tarifHandled = false;
                errorTarif = data.getGarbageDisposalTarif();
            }
        }
        //drainage
        if (tarifHandled
                && services.contains(ServiceStrategy.DRAINAGE)) {
            payment.putField(PaymentDBF.NORM_F_8, data.getChargeDrainage());
            if (!handleSubsidyService(payment, PaymentDBF.CODE2_8, data.getDrainageTarif(), 8) ||
                    !handleSubsidyTarif(payment, PaymentDBF.CODE3_8, data.getDrainageTarif(), 8)) {
                tarifHandled = false;
                errorTarif = data.getDrainageTarif();
            }
        }

        /*
         * Логирование ошибки обработки тарифа.
         */
        if (!tarifHandled) {
            payment.setStatus(RequestStatus.SUBSIDY_TARIF_CODE_NOT_FOUND);

            log.error("Couldn't find subsidy tarif code by calculation center's tarif: '{}', "
                            + " and user organization id: {}",
                    new Object[]{
                            errorTarif,
                            payment.getUserOrganizationId()
                    });

            RequestWarning warning = new RequestWarning(payment.getId(), RequestFileType.PAYMENT, RequestWarningStatus.SUBSIDY_TARIF_NOT_FOUND);
            warning.addParameter(new RequestWarningParameter(0, errorTarif));
            warning.addParameter(new RequestWarningParameter(1, "organization", billingId));
            warningBean.save(warning);
        }

        //benefits
        if (benefits != null && !benefits.isEmpty()) {
            String calcCenterOwnershipCode = data.getOwnership();

            Long internalOwnershipId = findInternalOwnership(calcCenterOwnershipCode, billingId);

            if (internalOwnershipId == null) {
                if (calcCenterOwnershipCode != null) {
                    log.error("Couldn't find in corrections internal ownership object by calculation center's ownership code: '{}' "
                            + "and calculation center id: {}", calcCenterOwnershipCode, billingId);

                    for (Benefit benefit : benefits) {
                        benefit.setStatus(RequestStatus.OWNERSHIP_NOT_FOUND);

                        RequestWarning warning = new RequestWarning(benefit.getId(), RequestFileType.BENEFIT,
                                RequestWarningStatus.OWNERSHIP_OBJECT_NOT_FOUND);
                        warning.addParameter(new RequestWarningParameter(0, calcCenterOwnershipCode));
                        warning.addParameter(new RequestWarningParameter(1, "organization", billingId));
                        warningBean.save(warning);
                    }
                }
            } else {
                final long osznId = payment.getOrganizationId();
                String osznOwnershipCode = findOSZNOwnershipCode(internalOwnershipId, osznId, payment.getUserOrganizationId());
                if (osznOwnershipCode == null) {
                    log.error("Couldn't find in corrections oszn's ownership code by internal ownership object id: {}"
                                    + ", oszn id: {} and user organization id: {}",
                            internalOwnershipId, osznId, payment.getUserOrganizationId());

                    for (Benefit benefit : benefits) {
                        benefit.setStatus(RequestStatus.OWNERSHIP_NOT_FOUND);

                        RequestWarning warning = new RequestWarning(benefit.getId(), RequestFileType.BENEFIT,
                                RequestWarningStatus.OWNERSHIP_CODE_NOT_FOUND);
                        warning.addParameter(new RequestWarningParameter(0, "ownership", internalOwnershipId));
                        warning.addParameter(new RequestWarningParameter(1, "organization", osznId));
                        warningBean.save(warning);
                    }
                } else {
                    Integer ownershipCodeAsInt = null;
                    try {
                        ownershipCodeAsInt = Integer.valueOf(osznOwnershipCode);
                    } catch (NumberFormatException e) {
                        log.error("Couldn't transform OWN_FRM value '{}' from correction to integer value. Oszn id: {}, internal ownership id: {}",
                                osznOwnershipCode, osznId, internalOwnershipId);

                        for (Benefit benefit : benefits) {
                            benefit.setStatus(RequestStatus.OWNERSHIP_NOT_FOUND);

                            RequestWarning warning = new RequestWarning(benefit.getId(), RequestFileType.BENEFIT,
                                    RequestWarningStatus.OWNERSHIP_CODE_INVALID);
                            warning.addParameter(new RequestWarningParameter(0, osznOwnershipCode));
                            warning.addParameter(new RequestWarningParameter(1, "organization", osznId));
                            warning.addParameter(new RequestWarningParameter(2, "ownership", internalOwnershipId));
                            warningBean.save(warning);
                        }
                    }

                    if (ownershipCodeAsInt != null) {
                        for (Benefit benefit : benefits) {
                            benefit.putField(BenefitDBF.OWN_FRM, ownershipCodeAsInt);
                        }
                    }
                }
            }
            for (Benefit benefit : benefits) {
                benefit.putField(BenefitDBF.CM_AREA, payment.getStringField(PaymentDBF.NORM_F_1));
                benefit.putField(BenefitDBF.HOSTEL, data.getRoomCount());
            }
        }
    }

    private boolean handleSubsidyService(Payment payment, PaymentDBF field, BigDecimal rawTarif, int service) {
        String serviceCode = getSubsidyServiceCode(rawTarif, payment.getOrganizationId(), payment.getUserOrganizationId(),
                service, (Date) payment.getField(PaymentDBF.DAT1));

        if (serviceCode == null) {
            return false;
        } else {
            payment.putField(field, serviceCode);
            payment.setStatus(RequestStatus.PROCESSED);
            return true;
        }
    }

    private boolean handleSubsidyTarif(Payment payment, PaymentDBF field, BigDecimal rawTarif, int service) {
        String tarifCode = getSubsidyTarifCode(rawTarif, payment.getOrganizationId(), payment.getUserOrganizationId(),
                service, (Date) payment.getField(PaymentDBF.DAT1));

        if (tarifCode == null) {
            return false;
        } else {
            payment.putField(field, tarifCode);
            payment.setStatus(RequestStatus.PROCESSED);
            return true;
        }
    }

    protected Long findInternalOwnership(String calculationCenterOwnership, long billingId) {
        return ownershipCorrectionBean.findInternalOwnership(calculationCenterOwnership, billingId);
    }

    protected String findOSZNOwnershipCode(Long internalOwnership, long osznId, long userOrganizationId) {
        return ownershipCorrectionBean.findOwnershipCode(internalOwnership, osznId, userOrganizationId);
    }

    /**
     * Получить вид тарифа.
     * @param T11_CS_UNI
     * @return
     */
    private String getSubsidyServiceCode(BigDecimal T11_CS_UNI, long osznId, long userOrganizationId, int service, Date date) {
        return subsidyTarifBean.getCode2(T11_CS_UNI, osznId, userOrganizationId, service, date);
    }

    /**
     * Получить тариф.
     * @param T11_CS_UNI
     * @return
     */
    private String getSubsidyTarifCode(BigDecimal T11_CS_UNI, long osznId, long userOrganizationId, int service, Date date) {
        return subsidyTarifBean.getCode3(T11_CS_UNI, osznId, userOrganizationId, service, date);
    }

    public Collection<BenefitData> getBenefitData(Benefit benefit, Date dat1){
        String dataSource = organizationStrategy.getDataSourceByUserOrganizationId(benefit.getUserOrganizationId());

        Map<String, Object> params = newHashMap();
        params.put("accountNumber", benefit.getAccountNumber());
        params.put("dat1", dat1);

        try {
            sqlSession(dataSource).selectOne(NS + ".getPrivs", params);
        }catch (Exception e){
            throw new DBRuntimeException(e);
        } finally {
            log.info("getPrivs. Calculation center: {}, parameters : {}", dataSource, params);
        }

        Integer resultCode = (Integer) params.get("resultCode");
        if (resultCode == null) {
            benefit.setStatus(RequestStatus.PROCESSING_INVALID_FORMAT);

            log.error("getPrivs. Result code is null. Benefit id: {}, dat1: {}, calculation center: {}",
                    benefit.getId(), dat1, dataSource);
        } else {
            switch (resultCode) {
                case 1:
                    @SuppressWarnings("unchecked")
                    List<BenefitData> benefitData = (List<BenefitData>) params.get("data");
                    if (benefitData != null && !benefitData.isEmpty()) {
                        if (checkOrderFam(dataSource, "getPrivs", benefitData, newArrayList(benefit), dat1)
                                && checkBenefitCode(dataSource, "getPrivs", benefitData, newArrayList(benefit), dat1)) {
                            Collection<BenefitData> emptyList = getEmptyBenefitData(benefitData);
                            if (emptyList != null && !emptyList.isEmpty()) {
                                logEmptyBenefitData(dataSource, "getPrivs", newArrayList(benefit), dat1);
                            }

                            Collection<BenefitData> finalBenefitData = getBenefitDataWithMinPriv("getPrivs", benefitData);
                            finalBenefitData.addAll(emptyList);
                            return finalBenefitData;
                        }
                    } else {
                        benefit.setStatus(RequestStatus.PROCESSING_INVALID_FORMAT);

                        log.error("getPrivs. Result code is 1 but benefit data is null or empty. Benefit id: {}, dat1: {}, "
                                        + "calculation center: {}",
                                benefit.getId(), dat1, dataSource);
                    }
                    break;
                case 0:
                    benefit.setStatus(RequestStatus.ACCOUNT_NUMBER_NOT_FOUND);
                    break;
                default:
                    benefit.setStatus(RequestStatus.PROCESSING_INVALID_FORMAT);

                    log.error("getBenefitData. Unexpected result code: {}. Benefit id: {}, dat1: {}, calculation center: {}",
                            resultCode, benefit.getId(), dat1, dataSource);
            }
        }
        return null;
    }

    public Cursor<BenefitData> getBenefitData(Long userOrganizationId, String accountNumber, Date date){
        Map<String, Object> params = newHashMap();
        params.put("accountNumber", accountNumber);
        params.put("dat1", date);

        try {
            sqlSession(getDataSource(userOrganizationId)).selectOne(NS + ".getPrivs", params);
        }catch (Exception e){
            throw new DBRuntimeException(e);
        }finally {
            log.info("getBenefitData getPrivs {}", params);
        }

        return new Cursor<>((Integer) params.get("resultCode"), (List) params.get("data"));
    }

    protected static class BenefitDataId implements Serializable {

        private String inn;
        private String name;
        private String passport;

        BenefitDataId(String inn, String name, String passport) {
            this.inn = inn;
            this.name = name;
            this.passport = passport;
        }

        public String getInn() {
            return inn;
        }

        public void setInn(String inn) {
            this.inn = inn;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassport() {
            return passport;
        }

        public void setPassport(String passport) {
            this.passport = passport;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final BenefitDataId other = (BenefitDataId) obj;
            return Strings.isEqual(this.inn, other.inn) && Strings.isEqual(this.name, other.name) && Strings.isEqual(this.passport, other.passport);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 43 * hash + (!Strings.isEmpty(this.inn) ? this.inn.hashCode() : 0);
            hash = 43 * hash + (!Strings.isEmpty(this.name) ? this.name.hashCode() : 0);
            hash = 43 * hash + (!Strings.isEmpty(this.passport) ? this.passport.hashCode() : 0);
            return hash;
        }
    }

    private boolean checkOrderFam(String dataSource, String method, List<BenefitData> benefitData,
                                  List<Benefit> benefits, Date dat1) {
        String accountNumber = benefits.get(0).getAccountNumber();
        for (BenefitData data : benefitData) {
            if (Strings.isEmpty(data.getOrderFamily())) {
                log.error(method + ". Order fam is null. Account number: {}, dat1: {}, calculation center: {}",
                        accountNumber, dat1, dataSource);
                for (Benefit benefit : benefits) {
                    benefit.setStatus(RequestStatus.PROCESSING_INVALID_FORMAT);
                }
                return false;
            }
        }

        Map<String, BenefitData> orderFams = newHashMap();
        for (BenefitData data : benefitData) {
            String orderFamBudgedKey = data.getOrderFamily() + data.getBudget();

            BenefitData duplicate = orderFams.get(orderFamBudgedKey);

            if (duplicate != null) {
                log.error(method + ". Order fam is not unique. At least two benefit data have the same order fam. First: {}, second {}. "
                        + "Account number: {}, dat1: {}, calculation center: {}", data, duplicate, accountNumber, dat1, dataSource);
                for (Benefit benefit : benefits) {
                    benefit.setStatus(RequestStatus.PROCESSING_INVALID_FORMAT);
                }
                return false;
            } else {
                orderFams.put(orderFamBudgedKey, data);
            }
        }
        return true;
    }

    private boolean checkBenefitCode(String dataSource, String method, List<BenefitData> benefitData,
                                     List<Benefit> benefits, Date dat1) {
        String accountNumber = benefits.get(0).getAccountNumber();
        for (BenefitData data : benefitData) {
            if (Strings.isEmpty(data.getCode())) {
                for (Benefit benefit : benefits) {
                    benefit.setStatus(RequestStatus.PROCESSING_INVALID_FORMAT);
                }

                log.error(method + ". BenefitData's code is null. Account number: {}, dat1: {}, calculation center: {}",
                        accountNumber, dat1, dataSource);

                return false;
            }
        }
        return true;
    }

    private void logEmptyBenefitData(String dataSource, String method, List<Benefit> benefits, Date dat1) {
        String accountNumber = benefits.get(0).getAccountNumber();

        log.error(method + ". Inn, name and passport of benefit data are null. Account number: {}, dat1: {}, " +
                        "calculation center: {}", accountNumber, dat1, dataSource);
    }

    private Map<BenefitDataId, Collection<BenefitData>> groupBenefitData(String method, Collection<BenefitData> benefitData) {
        Map<BenefitDataId, Collection<BenefitData>> groupMap = newHashMap();
        for (BenefitData data : benefitData) {
            BenefitDataId id = new BenefitDataId(data.getInn(), data.getFirstName() + data.getMiddleName() + data.getLastName(),
                    data.getPassportSerial() + data.getPassportNumber());
            Collection<BenefitData> list = groupMap.computeIfAbsent(id, k -> newArrayList());
            list.add(data);
        }
        return groupMap;
    }

    private Collection<BenefitData> getBenefitDataWithMinPriv(String method, Collection<BenefitData> benefitData) {
        Collection<BenefitData> nonEmptyList = getNonEmptyBenefitData(benefitData);
        Map<BenefitDataId, Collection<BenefitData>> groupMap = groupBenefitData(method, nonEmptyList);
        Collection<BenefitData> benefitDataWithMinPriv = newArrayList();

        for (Map.Entry<BenefitDataId, Collection<BenefitData>> group : groupMap.entrySet()) {
            BenefitData min = Collections.min(group.getValue(), BENEFIT_DATA_COMPARATOR);
            benefitDataWithMinPriv.add(min);
        }

        return benefitDataWithMinPriv;
    }

    private Collection<BenefitData> getEmptyBenefitData(Collection<BenefitData> benefitData) {
        return newArrayList(benefitData.stream().filter(BenefitData::isEmpty).collect(Collectors.toList()));
    }

    private Collection<BenefitData> getNonEmptyBenefitData(Collection<BenefitData> benefitData) {
        Collection<BenefitData> nonEmptyList = newArrayList(benefitData);
        nonEmptyList.removeAll(getEmptyBenefitData(benefitData));
        return nonEmptyList;
    }

    protected List<BenefitData> getBenefitDataByINN(List<BenefitData> benefitDatas, final String inn) {
        return newArrayList(benefitDatas.stream().filter(benefitData -> benefitData.getInn().equals(inn)).collect(Collectors.toList()));
    }

    private static class BenefitDataComparator implements Comparator<BenefitData> {

        @Override
        public int compare(BenefitData o1, BenefitData o2) {
            String benefitCode1 = o1.getCode();
            Integer i1 = null;
            try {
                i1 = Integer.parseInt(benefitCode1);
            } catch (NumberFormatException e) {
            }

            String benefitCode2 = o2.getCode();
            Integer i2 = null;
            try {
                i2 = Integer.parseInt(benefitCode2);
            } catch (NumberFormatException e) {
            }

            if (i1 != null && i2 != null) {
                return i1.compareTo(i2);
            } else {
                return benefitCode1.compareTo(benefitCode2);
            }
        }
    }
    private static final BenefitDataComparator BENEFIT_DATA_COMPARATOR = new BenefitDataComparator();

    /**
     * Обработать группу benefit записей с одинаковым account number.
     * Процедура COMP.Z$RUNTIME_SZ_UTL.GETPRIVS.
     *
     * При возникновении ошибок при вызове процедуры проставляется статус RequestStatus.ACCOUNT_NUMBER_NOT_FOUND.
     * Так сделано потому, что проанализировать возвращаемое из процедуры значение не удается если номер л/c не найден
     * в ЦН по причине того что курсор в этом случае закрыт, и драйвер с соответствии со стандартом JDBC рассматривает
     * закрытый курсор как ошибку и выбрасывает исключение.
     *
     * @param dat1 дата из поля DAT1 payment записи, соответствующей группе benefits записей со значением в поле FROG большим 0
     * @param benefits группа benefit записей
     */
    public void processBenefit(Date dat1, List<Benefit> benefits){
        String accountNumber = benefits.get(0).getAccountNumber();
        String dataSource = organizationStrategy.getDataSourceByUserOrganizationId(benefits.get(0).getUserOrganizationId());

        Map<String, Object> map = newHashMap();
        map.put("accountNumber", accountNumber);
        map.put("dat1", dat1);

        try {
            sqlSession(dataSource).selectOne(NS + ".getPrivs", map);
        }catch (Exception e){
            throw new DBRuntimeException(e);
        }finally {
            log.info("processBenefit getPrivs {}", map);
        }

        Integer resultCode = (Integer) map.get("resultCode");

        if (resultCode == null) {
            for (Benefit benefit : benefits) {
                benefit.setStatus(RequestStatus.PROCESSING_INVALID_FORMAT);
            }

            log.error("processBenefit. Result code is null. Account number: {}, dat1: {}, calculation center: {}",
                    accountNumber, dat1, dataSource);
        } else {
            switch (resultCode) {
                case 1:
                    //noinspection unchecked
                    List<BenefitData> benefitData = (List<BenefitData>) map.get("data");

                    if (benefitData != null && !benefitData.isEmpty()) {
                        if (checkOrderFam(dataSource, "processBenefit", benefitData, benefits, dat1)
                                && checkBenefitCode(dataSource, "processBenefit", benefitData, benefits, dat1)) {
                            processBenefitData(dataSource, benefits, benefitData, dat1);
                        }
                    } else {
                        for (Benefit benefit : benefits) {
                            benefit.putField(BenefitDBF.OZN_ABS, 1);
                        }

                        log.error("processBenefit. Result code is 1 but benefit data is null or empty. Account number: {}, dat1: {},"
                                + " calculation center: {}", accountNumber, dat1, dataSource);
                    }
                    break;
                case 0:
                    setStatus(benefits, RequestStatus.ACCOUNT_NUMBER_NOT_FOUND);

                    break;
                default:
                    for (Benefit benefit : benefits) {
                        benefit.setStatus(RequestStatus.PROCESSING_INVALID_FORMAT);
                    }

                    log.error("processBenefit. Unexpected result code: {}. Account number: {}, dat1: {}, calculation center: {}",
                            resultCode, accountNumber, dat1, dataSource);
            }
        }
    }

    /**
     * Из требований заказчика: "Среди жильцов на обрабатываемом л/с по идентификационному коду (IND_COD) пытаемся
     * найти носителя льготы, если не нашли, то ищем по номеру паспорта (без серии). Если нашли, проставляем категорию
     * льготы, если не нашли - отмечаем все записи, относящиеся к данному л/с как ошибку связывания. Надо иметь ввиду,
     * что на одной персоне может быть более одной льготы. В этом случае надо брать льготу с меньшим номером категории."
     *
     * Алгоритм:
     * Для каждой записи из data выделяем текущий ИНН(обязательно не NULL) и текущий номер паспорта.
     * Если этот ИНН еще не обрабатывался, то ищем среди benefits с таким же ИНН.
     * Если не нашли, то ищем с текущим номером паспорта.
     * В итоге получим некоторое подмножество benefits - theSameBenefits.
     * Если theSameBenefits не пусто, то выделим из data записи с текущим ИНН - список theSameMan.
     * Если же theSameBenefits пусто, то помечаем все записи benefits статусом RequestStatus.WRONG_ACCOUNT_NUMBER
     * и выходим.
     * В theSameMan найдем запись с наименьшим кодом привилегии  - min. Порядок сравнения: пытаемся преобразовать
     * строковые значения кодов привилегий в числа и сравнить как числа, иначе сравниваем как строки.
     * По полученному наименьшему коду привилегии(cmBenefitCode) ищем методом getOSZNPrivilegeCode код привилегии
     * для ОСЗН(osznBenefitCode) в таблице коррекций привилегий.
     * Если нашли код привилегии(osznBenefitCode != null), то проставляем во все записи в benefits: в поле PRIV_CAT
     * - osznBenefitCode, в поле ORD_FAM - порядок льготы из min(min.get("ORD_FAM"))
     * Если не нашли код привилегии, то все записи в theSameBenefits помечаются статусом RequestStatus.BENEFIT_NOT_FOUND.
     * Наконец все записи benefits, для которых код не был проставлен в RequestStatus.BENEFIT_NOT_FOUND помечаются
     * статусом RequestStatus.PROCESSED.
     *
     * @param benefits Список benefit записей с одинаковым номером л/c
     * @param benefitData Список записей данных из ЦН
     */
    private void processBenefitData(String dataSource, List<Benefit> benefits,
                                    List<BenefitData> benefitData, Date dat1) {

        final long osznId = benefits.get(0).getOrganizationId();
        final long userOrganizationId = benefits.get(0).getUserOrganizationId();
        final long billingId = organizationStrategy.getBillingId(userOrganizationId);

        Collection<BenefitData> emptyList = getEmptyBenefitData(benefitData);
        if (emptyList != null && !emptyList.isEmpty()) {
            logEmptyBenefitData(dataSource, "processBenefit", benefits, dat1);
            setStatus(benefits, RequestStatus.BENEFIT_OWNER_NOT_ASSOCIATED);
            return;
        }

        Collection<BenefitData> benefitDataWithMinPriv = getBenefitDataWithMinPriv("processBenefit", benefitData);

        for (BenefitData data : benefitDataWithMinPriv) {
            String inn = data.getInn();
            String passport = data.getPassportNumber();
            Collection<Benefit> foundBenefits = null;

            if (!Strings.isEmpty(inn)) {
                foundBenefits = findByINN(benefits, inn);
            }
            if ((foundBenefits == null || foundBenefits.isEmpty()) && !Strings.isEmpty(passport)) {
                foundBenefits = findByPassportNumber(benefits, passport);
            }

            if (foundBenefits == null || foundBenefits.isEmpty()) {
                setStatus(benefits, RequestStatus.BENEFIT_OWNER_NOT_ASSOCIATED);
                return;
            }

            //set benefit code
            try {
                Integer benefitCodeAsInt = Integer.valueOf(data.getCode());

                for (Benefit benefit : foundBenefits) {
                    benefit.putField(BenefitDBF.PRIV_CAT, benefitCodeAsInt);
                }
            } catch (NumberFormatException e) {
                setStatus(foundBenefits, RequestStatus.PROCESSING_INVALID_FORMAT);

                log.error("Couldn't transform privilege code '{}' from correction to integer value. Oszn id: {}",
                        data.getCode(), osznId);

                for (Benefit benefit : foundBenefits) {
                    RequestWarning warning = new RequestWarning(benefit.getId(), RequestFileType.BENEFIT,
                            RequestWarningStatus.PRIVILEGE_CODE_INVALID);
                    warning.addParameter(new RequestWarningParameter(0, data.getCode()));
                    warning.addParameter(new RequestWarningParameter(1, "organization", osznId));
                    warningBean.save(warning);
                }
            }

            //set ord fam
            try {
                Integer ordFamAsInt = Integer.valueOf(data.getOrderFamily());

                for (Benefit benefit : foundBenefits) {
                    benefit.putField(BenefitDBF.ORD_FAM, ordFamAsInt);
                }
            } catch (NumberFormatException e) {
                setStatus(foundBenefits, RequestStatus.PROCESSING_INVALID_FORMAT);

                log.error("Couldn't transform ord fam value '{}' from calculation center to integer value.", data.getOrderFamily());

                for (Benefit benefit : foundBenefits) {
                    RequestWarning warning = new RequestWarning(RequestFileType.BENEFIT, RequestWarningStatus.ORD_FAM_INVALID);
                    warning.addParameter(new RequestWarningParameter(0, data.getOrderFamily()));
                    warning.addParameter(new RequestWarningParameter(1, "organization", billingId));
                    warningBean.save(warning);
                }
            }
        }

        for (Benefit benefit : benefits) {
            if (benefit.getStatus() != RequestStatus.BENEFIT_NOT_FOUND && benefit.getStatus() != RequestStatus.PROCESSING_INVALID_FORMAT) {
                benefit.setStatus(RequestStatus.PROCESSED);
            }
        }
    }

    protected void setStatus(Collection<Benefit> benefits, RequestStatus status) {
        for (Benefit benefit : benefits) {
            benefit.setStatus(status);
        }
    }

    private List<Benefit> findByPassportNumber(List<Benefit> benefits, final String passportNumber) {
        return benefits.stream()
                .filter(benefit -> passportNumber.equals(benefit.getStringField(BenefitDBF.PSP_NUM)))
                .collect(Collectors.toList());
    }

    private List<Benefit> findByINN(List<Benefit> benefits, final String inn) {
        return benefits.stream()
                .filter(benefit -> inn.equals(benefit.getStringField(BenefitDBF.IND_COD)))
                .collect(Collectors.toList());
    }
    private static final int OSZN_ACCOUNT_TYPE = 0;
    private static final int MEGABANK_ACCOUNT_TYPE = 1;
    private static final int CALCULATION_CENTER_ACCOUNT_TYPE = 2;

    private int determineAccountType(String accountNumber) throws UnknownAccountNumberTypeException {
        if (Strings.isEmpty(accountNumber)) {
            throw new UnknownAccountNumberTypeException();
        }

        if (accountNumber.length() == 10 && accountNumber.startsWith("100")) {
            return CALCULATION_CENTER_ACCOUNT_TYPE;
        }
        if (accountNumber.length() == 9 && accountNumber.startsWith("1")) {
            return MEGABANK_ACCOUNT_TYPE;
        }
        if (accountNumber.length() < 9) {
            return OSZN_ACCOUNT_TYPE;
        }

        throw new UnknownAccountNumberTypeException();
    }

    public void processActualPayment(String dataSource, Set<Long> serviceIds, ActualPayment actualPayment, Date date){
        Map<String, Object> params = newHashMap();
        params.put("accountNumber", actualPayment.getAccountNumber());
        params.put("date", date);

        try {
            sqlSession(dataSource).selectOne(NS + ".getFactChargeAndTarif", params);
        }catch (Exception e){
            throw new DBRuntimeException(e);
        } finally {
            log.info("processActualPayment. Calculation center: {}, parameters : {}", dataSource, params);
        }

        Integer resultCode = (Integer) params.get("resultCode");
        if (resultCode == null) {
            actualPayment.setStatus(RequestStatus.PROCESSING_INVALID_FORMAT);

            log.error("processActualPayment. Result code is null. ActualPayment id: {}, calculation center: {}",
                    actualPayment.getId(), dataSource);
        } else {
            switch (resultCode) {
                case 1:
                    //noinspection unchecked
                    List<ActualPaymentData> actualPaymentDatas = (List<ActualPaymentData>) params.get("data");
                    if (actualPaymentDatas != null && !actualPaymentDatas.isEmpty()) {
                        ActualPaymentData data = actualPaymentDatas.get(0);
                        if (actualPaymentDatas.size() > 1) {
                            log.warn("processActualPayment. Size of list of actualPaymentData is more than 1. Only first entry will be used."
                                    + "Calculation center: {}", dataSource);
                        }
                        processActualPaymentData(actualPayment, data, serviceIds);
                    } else {
                        actualPayment.setStatus(RequestStatus.PROCESSING_INVALID_FORMAT);

                        log.error("processActualPayment. Result code is 1 but actualPaymentData is null or empty. ActualPayment id: {}"
                                + ", calculation center: {}", actualPayment.getId(), dataSource);
                    }
                    break;
                case 0:
                    actualPayment.setStatus(RequestStatus.ACCOUNT_NUMBER_NOT_FOUND);
                    break;
                default:
                    actualPayment.setStatus(RequestStatus.PROCESSING_INVALID_FORMAT);

                    log.error("processActualPayment. Unexpected result code: {}. ActualPayment id: {}, calculation center: {}",
                            resultCode, actualPayment.getId(), dataSource);
            }
        }
    }

    private void processActualPaymentData(ActualPayment actualPayment, ActualPaymentData data, Set<Long> serviceProviderTypeIds) {
        if (serviceProviderTypeIds.contains(ServiceStrategy.APARTMENT_FEE)) {
            actualPayment.putField(ActualPaymentDBF.P1, data.getApartmentFeeCharge());
            actualPayment.putField(ActualPaymentDBF.N1, data.getApartmentFeeTarif());
        }
        if (serviceProviderTypeIds.contains(ServiceStrategy.HEATING)) {
            actualPayment.putField(ActualPaymentDBF.P2, data.getHeatingCharge());
            actualPayment.putField(ActualPaymentDBF.N2, data.getHeatingTarif());
        }
        if (serviceProviderTypeIds.contains(ServiceStrategy.HOT_WATER_SUPPLY)) {
            actualPayment.putField(ActualPaymentDBF.P3, data.getHotWaterCharge());
            actualPayment.putField(ActualPaymentDBF.N3, data.getHotWaterTarif());
        }
        if (serviceProviderTypeIds.contains(ServiceStrategy.COLD_WATER_SUPPLY)) {
            actualPayment.putField(ActualPaymentDBF.P4, data.getColdWaterCharge());
            actualPayment.putField(ActualPaymentDBF.N4, data.getColdWaterCharge());
        }
        if (serviceProviderTypeIds.contains(ServiceStrategy.GAS_SUPPLY)) {
            actualPayment.putField(ActualPaymentDBF.P5, data.getGasCharge());
            actualPayment.putField(ActualPaymentDBF.N5, data.getGasTarif());
        }
        if (serviceProviderTypeIds.contains(ServiceStrategy.POWER_SUPPLY)) {
            actualPayment.putField(ActualPaymentDBF.P6, data.getPowerCharge());
            actualPayment.putField(ActualPaymentDBF.N6, data.getPowerTarif());
        }
        if (serviceProviderTypeIds.contains(ServiceStrategy.GARBAGE_DISPOSAL)) {
            actualPayment.putField(ActualPaymentDBF.P7, data.getGarbageDisposalCharge());
            actualPayment.putField(ActualPaymentDBF.N7, data.getGarbageDisposalTarif());
        }
        if (serviceProviderTypeIds.contains(ServiceStrategy.DRAINAGE)) {
            actualPayment.putField(ActualPaymentDBF.P8, data.getDrainageCharge());
            actualPayment.putField(ActualPaymentDBF.N8, data.getDrainageTarif());
        }
        actualPayment.setStatus(RequestStatus.PROCESSED);
    }

    private boolean isMegabankAccount(String realPuAccountNumber, String megabankAccount) {
        return (realPuAccountNumber.length() == 9) && realPuAccountNumber.equals(megabankAccount);
    }

    private boolean isCalcCenterAccount(String realPuAccountNumber, String calcCenterAccount) {
        return (realPuAccountNumber.length() == 10) && realPuAccountNumber.equals(calcCenterAccount);
    }

    public Cursor<PaymentAndBenefitData> getPaymentAndBenefit(Long userOrganizationId, String accountNumber, Date date){
        Map<String, Object> params = newHashMap();
        params.put("accountNumber", accountNumber);
        params.put("dat1", date);

        try {
            sqlSession(getDataSource(userOrganizationId)).selectOne(NS + ".getChargeAndParams", params);
        } catch (Exception e) {
            throw new DBRuntimeException(e);
        } finally {
            log.info("getPaymentAndBenefit. GetChargeAndParams{}", params);
        }

        return new Cursor<>((Integer) params.get("resultCode"), (List) params.get("data"));
    }

    public Long createPrivilegeProlongationHeader(Long userOrganizationId, String district, String zheuCode, Date date, String fileName,
                                                  Integer recordsCount, boolean profit){
        Map<String, Object> map = new HashMap<>();
        map.put("pDistrName", district);
        map.put("pZheuCode", zheuCode);
        map.put("pDate", date);
        map.put("pFile", fileName);
        map.put("pCnt", recordsCount);
        map.put("pProfit", profit ? 1 : 0);

        try {
            sqlSession(getDataSource(userOrganizationId)).selectOne(NS + ".createPrivHeader", map);
        } catch (Exception e) {
            throw new DBRuntimeException(e);
        } finally {
            log.info("createPrivilegeProlongationHeader createPrivHeader {}", map);
        }

        return (Long) map.get("collectionId");
    }

    public void exportPrivilegeProlongation(Long userOrganizationId, List<PrivilegeProlongation> privilegeProlongations){
        try (SqlSession sqlSessionManager = sqlSessionManager(getDataSource(userOrganizationId)).openSession(ExecutorType.BATCH)) {
            privilegeProlongations.forEach(p -> {
                sqlSessionManager.insert(NS + ".insertPriv", Collections.singleton(p));
            });

            sqlSessionManager.commit();
        }
    }

    @SuppressWarnings("unchecked")
    public Cursor<Lodger> getLodgers(Long userOrganizationId, String accountNumber, Date date){
        Map<String, Object> map = new HashMap<>();
        map.put("pAccCode", accountNumber);
        map.put("pDate", date);

        try {
            sqlSession(getDataSource(userOrganizationId)).selectOne(NS + ".getLodgers", map);
        } catch (Exception e) {
            throw new DBRuntimeException(e);
        } finally {
            log.info("getLodgers {}", map);
        }

        return new Cursor<>((Integer) map.get("resultCode"), (List<Lodger>) map.get("data"));
    }

    public Long createSubsHeader(Long userOrganizationId, String district, String zheuCode, Date date, String fileName,
                                 Integer recordsCount){
        Map<String, Object> map = new HashMap<>();
        map.put("pDistrName", district);
        map.put("pZheuCode", zheuCode);
        map.put("pDate", date);
        map.put("pFile", fileName);
        map.put("pCnt", recordsCount);

        try {
            sqlSession(getDataSource(userOrganizationId)).selectOne(NS + ".createSubsHeader", map);
        } catch (Exception e) {
            throw new DBRuntimeException(e);
        } finally {
            log.info("createSubsHeader {}", map);
        }

        return (Long) map.get("collectionId");
    }

    public void exportSubsidy(Long userOrganizationId, List<Subsidy> subsidies){
        Long serviceId = organizationStrategy.getDomainObject(userOrganizationId).getAttribute(OrganizationStrategy.SERVICE).getValueId();

        try (SqlSession sqlSessionManager = sqlSessionManager(getDataSource(userOrganizationId)).openSession(ExecutorType.BATCH)) {
            subsidies.forEach(s -> {
                if (s.getAccountNumber() == null){
                    s.setAccountNumber(s.getPuAccountNumber());
                }

                if (serviceId == 1){
                    s.getDbfFields().put("C_SUMMA", s.getField(SubsidyDBF.SM1));
                    s.getDbfFields().put("C_SUBS", s.getField(SubsidyDBF.SB1));
                    s.getDbfFields().put("C_NM_PAY", s.getField(SubsidyDBF.P1));
                    s.getDbfFields().put("C_OBS", s.getField(SubsidyDBF.OB1));
                }else if (serviceId == 7){
                    s.getDbfFields().put("C_SUMMA", s.getField(SubsidyDBF.SM7));
                    s.getDbfFields().put("C_SUBS", s.getField(SubsidyDBF.SB7));
                    s.getDbfFields().put("C_NM_PAY", s.getField(SubsidyDBF.P7));
                    s.getDbfFields().put("C_OBS", s.getField(SubsidyDBF.OB7));
                }

                sqlSessionManager.insert(NS + ".insertBuffSubs", s);
            });

            sqlSessionManager.commit();
        }
    }

    /*Facility*/

    private <T> Cursor<T> getFacility(String function, Long userOrganizationId, String district, String zheuCode, Date date){
        Map<String, Object> map = new HashMap<>();
        map.put("pDistrName", district);
        map.put("pZheuCode", zheuCode);
        map.put("pDate", date);

        try {
            sqlSession(getDataSource(userOrganizationId)).selectOne(NS + "." + function, map);
        } catch (Exception e) {
            throw new DBRuntimeException(e);
        } finally {
            log.info(function + ", size: {}", ((List) map.get("data")).size());
        }

        return new Cursor<>((Integer) map.get("resultCode"), (List) map.get("data"));
    }

    public Cursor<FacilityForm2> getFacilityForm2(Long userOrganizationId, String district, String zheuCode, Date date){
        return getFacility("getPrivsF", userOrganizationId, district, zheuCode, date);
    }

    public Cursor<FacilityLocal> getFacilityLocal(Long userOrganizationId, String district, String zheuCode, Date date){
        return getFacility("getPrivsM", userOrganizationId, district, zheuCode, date);
    }

    public Cursor<FacilityLocal> getFacilityLocalJanitor(Long userOrganizationId, String district, String zheuCode, Date date){
        return getFacility("getPrivsD", userOrganizationId, district, zheuCode, date);
    }

    public Cursor<FacilityLocal> getFacilityLocalCompensation(Long userOrganizationId, String district, String zheuCode, Date date){
        return getFacility("getPrivsK", userOrganizationId, district, zheuCode, date);
    }

    private String getDataSource(Long userOrganizationId){
        return organizationStrategy.getDataSourceByUserOrganizationId(userOrganizationId);
    }

    private <T> Cursor<T> getData(Long userOrganization, String statement, Map<String, Object> param) throws RemoteCallException {
        try {
            sqlSession(getDataSource(userOrganization)).selectOne(NS + "." + statement, param);
        } catch (Exception e) {
            throw new RemoteCallException(e);
        }

        log.info("{}: {}", statement, param);

        return new Cursor<>((Integer)param.get("resultCode"), (List) param.get("data"));
    }

    /*Subsidy*/

    public Cursor<SubsidyData> getSubsidyData(Long userOrganization, String pAcc, Date pDateB, Date pDateE, Date pOpMonth)
            throws RemoteCallException {
        return getData(userOrganization, "getSubsidy", MapUtil.of("pAcc", pAcc, "pDateB", pDateB,
                "pDateE", pDateE, "pOpMonth", pOpMonth));
    }
}
