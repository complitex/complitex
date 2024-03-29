package ru.complitex.osznconnection.file.service.subsidy.task;

import ru.complitex.address.exception.RemoteCallException;
import ru.complitex.common.entity.Cursor;
import ru.complitex.common.entity.Log;
import ru.complitex.common.exception.CanceledByUserException;
import ru.complitex.common.exception.ExecuteException;
import ru.complitex.osznconnection.file.Module;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.RequestFileStatus;
import ru.complitex.osznconnection.file.entity.RequestStatus;
import ru.complitex.osznconnection.file.entity.subsidy.*;
import ru.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import ru.complitex.osznconnection.file.service.RequestFileBean;
import ru.complitex.osznconnection.file.service.exception.FillException;
import ru.complitex.osznconnection.file.service.process.ProcessType;
import ru.complitex.osznconnection.file.service.subsidy.SubsidyBean;
import ru.complitex.osznconnection.file.service.subsidy.SubsidySplitBean;
import ru.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_EVEN;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 15.01.14 18:36
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class SubsidyFillTaskBean extends AbstractRequestTaskBean<RequestFile> {
    private final Logger log = LoggerFactory.getLogger(SubsidyFillTaskBean.class);

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private SubsidyBean subsidyBean;

    @EJB
    private SubsidySplitBean subsidySplitBean;

    @EJB
    private ServiceProviderAdapter serviceProviderAdapter;

    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        try {
            //проверяем что не обрабатывается в данный момент
//            if (requestFileBean.getRequestFileStatus(requestFile.getId()).isProcessing()) {
//                throw new FillException(new AlreadyProcessingException(requestFile.getFullName()), true, requestFile);
//            }

            requestFile.setStatus(RequestFileStatus.FILLING);
            requestFileBean.save(requestFile);

            //Обработка
            List<Subsidy> subsidies = subsidyBean.getSubsidies(requestFile.getId());

            for (Subsidy subsidy : subsidies){
                if (requestFile.isCanceled()){
                    throw new FillException(new CanceledByUserException(), true, requestFile);
                }

                fill(requestFile, subsidy);
                onRequest(subsidy, ProcessType.FILL_SUBSIDY);
            }

            //проверить все ли записи в файле субсидии обработались
            if (!subsidyBean.isSubsidyFileFilled(requestFile.getId())) {
                throw new FillException(true, requestFile);
            }

            requestFile.setStatus(RequestFileStatus.FILLED);
            requestFileBean.save(requestFile);

            return true;
        } catch (Exception e) {
            requestFile.setStatus(RequestFileStatus.FILL_ERROR);
            requestFileBean.save(requestFile);

            throw e;
        }
    }

    public void fill(RequestFile requestFile, Subsidy subsidy) {
        subsidySplitBean.deleteSubsidySplits(subsidy.getId());

        if (RequestStatus.unboundStatuses().contains(subsidy.getStatus())){
            subsidy.setStatus(RequestStatus.PROCESSED_WITH_ERROR);
            subsidyBean.update(subsidy);

            log.error("subsidy fill error: account number is not resolved {}", subsidy);

            return;
        }

        if (subsidy.getDateField(SubsidyDBF.DAT1) == null || subsidy.getDateField(SubsidyDBF.DAT2) == null){
            subsidy.setStatus(RequestStatus.PROCESSED_WITH_ERROR);
            subsidyBean.update(subsidy);

            log.error("subsidy fill error: empty dat1 or dat2 {}", subsidy);

            return;
        }

        Integer numm = subsidy.getIntegerField(SubsidyDBF.NUMM);

        if (numm < 0){
            subsidy.setStatus(RequestStatus.PROCESSED_WITH_ERROR);
            subsidyBean.update(subsidy);

            log.error("subsidy fill error: numm < 0 {}", subsidy);

            return;
        }

        LocalDate dat1 = new Date(subsidy.getDateField(SubsidyDBF.DAT1).getTime()).toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate dat2 = new Date(subsidy.getDateField(SubsidyDBF.DAT2).getTime()).toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();

        if (dat1.compareTo(dat2) > 0) {
            LocalDate minDate = dat2;

            dat2 = dat1;
            dat1 = minDate;

            log.warn("subsidy fill warn: dat1 > dat2 {}", subsidy);
        }

        LocalDate dat2LDoM = dat2.with(TemporalAdjusters.lastDayOfMonth());

        if (!dat2.isEqual(dat2LDoM)){
            dat2 = dat2LDoM;

            log.warn("subsidy fill warn: dat2 is not last day of month {}", subsidy);
        }

        if (numm > 1 && dat1.getDayOfMonth() == 1 && dat1.until(dat2).toTotalMonths() + 1 >= numm){
            split(subsidy, dat1, dat2, numm);
        }else if (numm == 0 && dat1.getDayOfMonth() == 1){
            numm = Integer.max(subsidy.getBigDecimalField(SubsidyDBF.SUMMA)
                    .divide(subsidy.getBigDecimalField(SubsidyDBF.SUBS), 0, HALF_EVEN).intValue(), 0);

            LocalDate dat1PlusNumm = dat1.plusMonths(numm).with(TemporalAdjusters.lastDayOfMonth());

            if (dat2.isBefore(dat1PlusNumm)){
                dat2 = dat1PlusNumm;

                log.warn("subsidy fill warn: dat2 < dat1 + numm {}", subsidy);
            }

            split(subsidy, dat1, dat2, numm);
        }else if (numm > 0 && dat1.getDayOfMonth() == 1 && dat1.until(dat2).toTotalMonths() + 1 < numm){
            numm = 0;

            LocalDate dat1PlusNumm = dat1.with(TemporalAdjusters.lastDayOfMonth());

            if (dat2.isBefore(dat1PlusNumm)){
                dat2 = dat1PlusNumm;

                log.warn("subsidy fill warn: dat2 < dat1 + numm {}", subsidy);
            }

            split(subsidy, dat1, dat2, numm);
        }else if (dat1.getDayOfMonth() != 1 && dat1.until(dat2).toTotalMonths() + 1 > 1){
            if (numm == 1 && dat1.until(dat2).toTotalMonths() + 1 > 1){
                dat2 = dat1.with(TemporalAdjusters.lastDayOfMonth());

                log.warn("subsidy fill warn: numm = 1 and dat1 - dat2 > 1 {}", subsidy);
            }

            recalculate(requestFile.getUserOrganizationId(), requestFile.getBeginDate(), subsidy, dat1, dat2);
        }else {
            subsidy.setStatus(RequestStatus.PROCESSED);
            subsidyBean.update(subsidy);
        }
    }

    @SuppressWarnings("Duplicates")
    private void split(Subsidy subsidy, LocalDate d1, LocalDate d2, Integer numm) {
        List<SubsidySplit> subsidySplits = new ArrayList<>();

        for (int i = 0; i < numm - 1; ++i){
            SubsidySplit subsidySplit = new SubsidySplit();

            subsidySplit.setSubsidyId(subsidy.getId());

            subsidySplit.putField(SubsidySplitField.DAT1, Date.from(d1.plusMonths(i)
                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));
            subsidySplit.putField(SubsidySplitField.DAT2, Date.from(d1.plusMonths(i).with(TemporalAdjusters.lastDayOfMonth())
                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));

            subsidySplit.putField(SubsidySplitField.SM1, subsidy.getBigDecimalField(SubsidyDBF.SM1)
                    .divide(BigDecimal.valueOf(numm), 2, HALF_EVEN));
            subsidySplit.putField(SubsidySplitField.SM2, subsidy.getBigDecimalField(SubsidyDBF.SM2)
                    .divide(BigDecimal.valueOf(numm), 2, HALF_EVEN));
            subsidySplit.putField(SubsidySplitField.SM3, subsidy.getBigDecimalField(SubsidyDBF.SM3)
                    .divide(BigDecimal.valueOf(numm), 2, HALF_EVEN));
            subsidySplit.putField(SubsidySplitField.SM4, subsidy.getBigDecimalField(SubsidyDBF.SM4)
                    .divide(BigDecimal.valueOf(numm), 2, HALF_EVEN));
            subsidySplit.putField(SubsidySplitField.SM5, subsidy.getBigDecimalField(SubsidyDBF.SM5)
                    .divide(BigDecimal.valueOf(numm), 2, HALF_EVEN));
            subsidySplit.putField(SubsidySplitField.SM6, subsidy.getBigDecimalField(SubsidyDBF.SM6)
                    .divide(BigDecimal.valueOf(numm), 2, HALF_EVEN));
            subsidySplit.putField(SubsidySplitField.SM7, subsidy.getBigDecimalField(SubsidyDBF.SM7)
                    .divide(BigDecimal.valueOf(numm), 2, HALF_EVEN));
            subsidySplit.putField(SubsidySplitField.SM8, subsidy.getBigDecimalField(SubsidyDBF.SM8)
                    .divide(BigDecimal.valueOf(numm), 2, HALF_EVEN));

            subsidySplit.putField(SubsidySplitField.SUMMA, subsidy.getBigDecimalField(SubsidyDBF.SUMMA)
                    .divide(BigDecimal.valueOf(numm), 2, HALF_EVEN));

            subsidySplit.putField(SubsidySplitField.SUBS, subsidy.getBigDecimalField(SubsidyDBF.SUBS));
            subsidySplit.putField(SubsidySplitField.NUMM, 1);

            if (numm > 1 || subsidySplit.getBigDecimalField(SubsidySplitField.SUMMA).compareTo(ZERO) != 0) {
                subsidySplits.add(subsidySplit);
            }
        }

        SubsidySplit subsidySplit = new SubsidySplit();

        subsidySplit.setSubsidyId(subsidy.getId());

        subsidySplit.putField(SubsidySplitField.DAT1, Date.from(d1.plusMonths(numm)
                .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        subsidySplit.putField(SubsidySplitField.DAT2, Date.from(d1.plusMonths(numm).with(TemporalAdjusters.lastDayOfMonth())
                .atStartOfDay(ZoneId.systemDefault()).toInstant()));

        subsidySplit.putField(SubsidySplitField.SM1, subsidy.getBigDecimalField(SubsidyDBF.SM1)
                .subtract(sum(subsidySplits, SubsidySplitField.SM1)));
        subsidySplit.putField(SubsidySplitField.SM2, subsidy.getBigDecimalField(SubsidyDBF.SM2)
                .subtract(sum(subsidySplits, SubsidySplitField.SM2)));
        subsidySplit.putField(SubsidySplitField.SM3, subsidy.getBigDecimalField(SubsidyDBF.SM3)
                .subtract(sum(subsidySplits, SubsidySplitField.SM3)));
        subsidySplit.putField(SubsidySplitField.SM4, subsidy.getBigDecimalField(SubsidyDBF.SM4)
                .subtract(sum(subsidySplits, SubsidySplitField.SM4)));
        subsidySplit.putField(SubsidySplitField.SM5, subsidy.getBigDecimalField(SubsidyDBF.SM5)
                .subtract(sum(subsidySplits, SubsidySplitField.SM5)));
        subsidySplit.putField(SubsidySplitField.SM6, subsidy.getBigDecimalField(SubsidyDBF.SM6)
                .subtract(sum(subsidySplits, SubsidySplitField.SM6)));
        subsidySplit.putField(SubsidySplitField.SM7, subsidy.getBigDecimalField(SubsidyDBF.SM7)
                .subtract(sum(subsidySplits, SubsidySplitField.SM7)));
        subsidySplit.putField(SubsidySplitField.SM8, subsidy.getBigDecimalField(SubsidyDBF.SM8)
                .subtract(sum(subsidySplits, SubsidySplitField.SM8)));

        subsidySplit.putField(SubsidySplitField.SUMMA, subsidy.getBigDecimalField(SubsidyDBF.SUMMA)
                .subtract(sum(subsidySplits, SubsidySplitField.SUMMA)));

        subsidySplit.putField(SubsidySplitField.SUBS, subsidy.getBigDecimalField(SubsidyDBF.SUBS));
        subsidySplit.putField(SubsidySplitField.NUMM, 1);

        if (numm > 1 || subsidySplit.getBigDecimalField(SubsidySplitField.SUMMA).compareTo(ZERO) != 0) {
            subsidySplits.add(subsidySplit);
        }

        if (subsidy.getBigDecimalField(SubsidyDBF.SUMMA).compareTo(sum(subsidySplits, SubsidySplitField.SUMMA)) == 0){
            subsidySplits.forEach(s -> subsidySplitBean.save(s));

            subsidy.setStatus(RequestStatus.SUBSIDY_SPLITTED);

            log.info("subsidy fill: add subsidy splits {}", subsidySplits);
        }else{
            subsidy.setStatus(RequestStatus.SUBSIDY_SPLIT_ERROR);

            log.info("subsidy fill error: subsidy splits error {}", subsidySplits);
        }

        subsidyBean.update(subsidy);
    }

    private BigDecimal sum(List<SubsidySplit> splits, SubsidySplitField field){
        return splits.stream().map(s -> s.getBigDecimalField(field)).reduce(ZERO, BigDecimal::add);
    }

    private void recalculate(Long userOrganizationId, Date opMonth, Subsidy subsidy, LocalDate d1, LocalDate d2) {
        try {
            Cursor<SubsidyData> subsidyDataCursor = serviceProviderAdapter.getSubsidyData(
                    userOrganizationId, subsidy.getAccountNumber(),
                    Date.from(d1.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    Date.from(d2.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    opMonth);

            if (subsidyDataCursor.getResultCode() == 1){
                List<SubsidyData> data = subsidyDataCursor.getData();

                if (data != null && !data.isEmpty()){
                    data = data.stream()
                            .collect(Collectors.groupingBy(SubsidyData::getSubsMonth))
                            .values().stream()
                            .map(l -> l.stream().reduce(new SubsidyData(), (s1, s2) -> {
                                SubsidyData s = new SubsidyData();

                                s.setSm1((s1.getSm1() != null ? s1.getSm1() : ZERO).add(s2.getSm1()));
                                s.setSm2((s1.getSm2() != null ? s1.getSm2() : ZERO).add(s2.getSm2()));
                                s.setSm3((s1.getSm3() != null ? s1.getSm3() : ZERO).add(s2.getSm3()));
                                s.setSm4((s1.getSm4() != null ? s1.getSm4() : ZERO).add(s2.getSm4()));
                                s.setSm5((s1.getSm5() != null ? s1.getSm5() : ZERO).add(s2.getSm5()));
                                s.setSm6((s1.getSm6() != null ? s1.getSm6() : ZERO).add(s2.getSm6()));
                                s.setSm7((s1.getSm7() != null ? s1.getSm7() : ZERO).add(s2.getSm7()));
                                s.setSm8((s1.getSm8() != null ? s1.getSm8() : ZERO).add(s2.getSm8()));

                                s.setSubsMonth(s2.getSubsMonth());

                                return s;
                            }))
                            .collect(Collectors.toList());

                    data.sort(Comparator.comparing(SubsidyData::getSubsMonth));

                    SubsidyData d0 = data.get(0);

                    for (int i = 1; i < data.size(); ++i){
                        SubsidyData d = data.get(i);

                        if (isDifferentSign(d.getSm1(), d0.getSm1()) ||
                                isDifferentSign(d.getSm2(), d0.getSm2()) ||
                                isDifferentSign(d.getSm3(), d0.getSm3()) ||
                                isDifferentSign(d.getSm4(), d0.getSm4()) ||
                                isDifferentSign(d.getSm5(), d0.getSm5()) ||
                                isDifferentSign(d.getSm6(), d0.getSm6()) ||
                                isDifferentSign(d.getSm7(), d0.getSm7()) ||
                                isDifferentSign(d.getSm8(), d0.getSm8())){
                            subsidy.setStatus(RequestStatus.SUBSIDY_RECALCULATE_ERROR);
                            subsidyBean.update(subsidy);

                            log.info("subsidy fill error: subsidy split recalculate error: not same sign {}", data);

                            return;
                        }
                    }

                    if (isDifferentSign(d0.getSm1(), subsidy.getBigDecimalField(SubsidyDBF.SM1)) ||
                            isDifferentSign(d0.getSm2(), subsidy.getBigDecimalField(SubsidyDBF.SM2)) ||
                            isDifferentSign(d0.getSm3(), subsidy.getBigDecimalField(SubsidyDBF.SM3)) ||
                            isDifferentSign(d0.getSm4(), subsidy.getBigDecimalField(SubsidyDBF.SM4)) ||
                            isDifferentSign(d0.getSm5(), subsidy.getBigDecimalField(SubsidyDBF.SM5)) ||
                            isDifferentSign(d0.getSm6(), subsidy.getBigDecimalField(SubsidyDBF.SM6)) ||
                            isDifferentSign(d0.getSm7(), subsidy.getBigDecimalField(SubsidyDBF.SM7)) ||
                            isDifferentSign(d0.getSm8(), subsidy.getBigDecimalField(SubsidyDBF.SM8))) {
                        recalculateRatio(subsidy, d1, d2, data);
                    }else{
                        recalculatePeriod(subsidy, d1.withDayOfMonth(1), d2, data);
                    }
                }else{
                    recalculatePeriod(subsidy, d1.withDayOfMonth(1), d2, new ArrayList<>());
                }
            }else{
                subsidy.setStatus(RequestStatus.PROCESSED_WITH_ERROR);
                subsidyBean.update(subsidy);

                log.error("subsidy fill error: empty subsidy data cursor {} {}", subsidy, subsidyDataCursor);
            }
        } catch (RemoteCallException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("Duplicates")
    private void recalculatePeriod(Subsidy subsidy, LocalDate d1, LocalDate d2, List<SubsidyData> data) {
        int n = (int) d1.until(d2.plusDays(1)).toTotalMonths();

        BigDecimal sm1 = data.stream().map(SubsidyData::getSm1).reduce(ZERO, BigDecimal::add)
                .add(subsidy.getBigDecimalField(SubsidyDBF.SM1)).divide(new BigDecimal(n), 2, HALF_EVEN);
        BigDecimal sm2 = data.stream().map(SubsidyData::getSm2).reduce(ZERO, BigDecimal::add)
                .add(subsidy.getBigDecimalField(SubsidyDBF.SM2)).divide(new BigDecimal(n), 2, HALF_EVEN);
        BigDecimal sm3 = data.stream().map(SubsidyData::getSm3).reduce(ZERO, BigDecimal::add)
                .add(subsidy.getBigDecimalField(SubsidyDBF.SM3)).divide(new BigDecimal(n), 2, HALF_EVEN);
        BigDecimal sm4 = data.stream().map(SubsidyData::getSm4).reduce(ZERO, BigDecimal::add)
                .add(subsidy.getBigDecimalField(SubsidyDBF.SM4)).divide(new BigDecimal(n), 2, HALF_EVEN);
        BigDecimal sm5 = data.stream().map(SubsidyData::getSm5).reduce(ZERO, BigDecimal::add)
                .add(subsidy.getBigDecimalField(SubsidyDBF.SM5)).divide(new BigDecimal(n), 2, HALF_EVEN);
        BigDecimal sm6 = data.stream().map(SubsidyData::getSm6).reduce(ZERO, BigDecimal::add)
                .add(subsidy.getBigDecimalField(SubsidyDBF.SM6)).divide(new BigDecimal(n), 2, HALF_EVEN);
        BigDecimal sm7 = data.stream().map(SubsidyData::getSm7).reduce(ZERO, BigDecimal::add)
                .add(subsidy.getBigDecimalField(SubsidyDBF.SM7)).divide(new BigDecimal(n), 2, HALF_EVEN);
        BigDecimal sm8 = data.stream().map(SubsidyData::getSm8).reduce(ZERO, BigDecimal::add)
                .add(subsidy.getBigDecimalField(SubsidyDBF.SM8)).divide(new BigDecimal(n), 2, HALF_EVEN);

        List<SubsidySplit> subsidySplits = new ArrayList<>();

        for (int i = 0; i < n - 1; ++i){
            SubsidySplit subsidySplit = new SubsidySplit();

            subsidySplit.setSubsidyId(subsidy.getId());

            subsidySplit.putField(SubsidySplitField.DAT1, Date.from(d1.plusMonths(i)
                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));
            subsidySplit.putField(SubsidySplitField.DAT2, Date.from(d1.plusMonths(i).with(TemporalAdjusters.lastDayOfMonth())
                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));

            SubsidyData subsidyData = data.stream()
                    .filter(d -> d.getSubsMonth().equals(subsidySplit.getDateField(SubsidySplitField.DAT1)))
                    .findAny()
                    .orElse(null);

            subsidySplit.putField(SubsidySplitField.SM1, subsidyData != null ? sm1.subtract(subsidyData.getSm1()) : sm1);
            subsidySplit.putField(SubsidySplitField.SM2, subsidyData != null ? sm2.subtract(subsidyData.getSm2()) : sm2);
            subsidySplit.putField(SubsidySplitField.SM3, subsidyData != null ? sm3.subtract(subsidyData.getSm3()) : sm3);
            subsidySplit.putField(SubsidySplitField.SM4, subsidyData != null ? sm4.subtract(subsidyData.getSm4()) : sm4);
            subsidySplit.putField(SubsidySplitField.SM5, subsidyData != null ? sm5.subtract(subsidyData.getSm5()) : sm5);
            subsidySplit.putField(SubsidySplitField.SM6, subsidyData != null ? sm6.subtract(subsidyData.getSm6()) : sm6);
            subsidySplit.putField(SubsidySplitField.SM7, subsidyData != null ? sm7.subtract(subsidyData.getSm7()) : sm7);
            subsidySplit.putField(SubsidySplitField.SM8, subsidyData != null ? sm8.subtract(subsidyData.getSm8()) : sm8);

            subsidySplit.putField(SubsidySplitField.SUMMA, subsidySplit.getBigDecimalField(SubsidySplitField.SM1)
                    .add(subsidySplit.getBigDecimalField(SubsidySplitField.SM2))
                    .add(subsidySplit.getBigDecimalField(SubsidySplitField.SM3))
                    .add(subsidySplit.getBigDecimalField(SubsidySplitField.SM4))
                    .add(subsidySplit.getBigDecimalField(SubsidySplitField.SM5))
                    .add(subsidySplit.getBigDecimalField(SubsidySplitField.SM6))
                    .add(subsidySplit.getBigDecimalField(SubsidySplitField.SM7))
                    .add(subsidySplit.getBigDecimalField(SubsidySplitField.SM8))
            );

            subsidySplit.putField(SubsidySplitField.SUBS, subsidy.getBigDecimalField(SubsidyDBF.SUBS));
            subsidySplit.putField(SubsidySplitField.NUMM, 1);

            if (subsidySplit.getBigDecimalField(SubsidySplitField.SUMMA).compareTo(ZERO) != 0) {
                subsidySplits.add(subsidySplit);
            }
        }


        SubsidySplit subsidySplit = new SubsidySplit();

        subsidySplit.setSubsidyId(subsidy.getId());

        subsidySplit.putField(SubsidySplitField.DAT1, Date.from(d1.plusMonths(n - 1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        subsidySplit.putField(SubsidySplitField.DAT2, Date.from(d1.plusMonths(n - 1).with(TemporalAdjusters.lastDayOfMonth())
                .atStartOfDay(ZoneId.systemDefault()).toInstant()));

        subsidySplit.putField(SubsidySplitField.SM1, subsidy.getBigDecimalField(SubsidyDBF.SM1)
                .subtract(sum(subsidySplits, SubsidySplitField.SM1)));
        subsidySplit.putField(SubsidySplitField.SM2, subsidy.getBigDecimalField(SubsidyDBF.SM2)
                .subtract(sum(subsidySplits, SubsidySplitField.SM2)));
        subsidySplit.putField(SubsidySplitField.SM3, subsidy.getBigDecimalField(SubsidyDBF.SM3)
                .subtract(sum(subsidySplits, SubsidySplitField.SM3)));
        subsidySplit.putField(SubsidySplitField.SM4, subsidy.getBigDecimalField(SubsidyDBF.SM4)
                .subtract(sum(subsidySplits, SubsidySplitField.SM4)));
        subsidySplit.putField(SubsidySplitField.SM5, subsidy.getBigDecimalField(SubsidyDBF.SM5)
                .subtract(sum(subsidySplits, SubsidySplitField.SM5)));
        subsidySplit.putField(SubsidySplitField.SM6, subsidy.getBigDecimalField(SubsidyDBF.SM6)
                .subtract(sum(subsidySplits, SubsidySplitField.SM6)));
        subsidySplit.putField(SubsidySplitField.SM7, subsidy.getBigDecimalField(SubsidyDBF.SM7)
                .subtract(sum(subsidySplits, SubsidySplitField.SM7)));
        subsidySplit.putField(SubsidySplitField.SM8, subsidy.getBigDecimalField(SubsidyDBF.SM8)
                .subtract(sum(subsidySplits, SubsidySplitField.SM8)));

        subsidySplit.putField(SubsidySplitField.SUMMA, subsidy.getBigDecimalField(SubsidyDBF.SUMMA)
                .subtract(sum(subsidySplits, SubsidySplitField.SUMMA)));

        subsidySplit.putField(SubsidySplitField.SUBS, subsidy.getBigDecimalField(SubsidyDBF.SUBS));
        subsidySplit.putField(SubsidySplitField.NUMM, 1);

        if (subsidySplit.getBigDecimalField(SubsidySplitField.SUMMA).compareTo(ZERO) != 0) {
            subsidySplits.add(subsidySplit);
        }


        if (subsidy.getBigDecimalField(SubsidyDBF.SUMMA).compareTo(subsidySplits.stream()
                .map(s -> s.getBigDecimalField(SubsidySplitField.SUMMA)).reduce(ZERO, BigDecimal::add)) == 0){
            subsidySplits.forEach(s -> subsidySplitBean.save(s));

            subsidy.setStatus(RequestStatus.SUBSIDY_RECALCULATED);

            log.info("subsidy fill: add subsidy recalculate period {}", subsidySplits);
        }else{
            subsidy.setStatus(RequestStatus.SUBSIDY_RECALCULATE_ERROR);

            log.info("subsidy fill error: subsidy recalculate period error: sum not equal {}", subsidySplits);
        }

        subsidyBean.update(subsidy);
    }

    @SuppressWarnings("Duplicates")
    private void recalculateRatio(Subsidy subsidy, LocalDate d1, LocalDate d2, List<SubsidyData> data) {
        BigDecimal sm1Sum = data.stream().map(SubsidyData::getSm1).reduce(ZERO, BigDecimal::add);
        BigDecimal sm2Sum = data.stream().map(SubsidyData::getSm2).reduce(ZERO, BigDecimal::add);
        BigDecimal sm3Sum = data.stream().map(SubsidyData::getSm3).reduce(ZERO, BigDecimal::add);
        BigDecimal sm4Sum = data.stream().map(SubsidyData::getSm4).reduce(ZERO, BigDecimal::add);
        BigDecimal sm5Sum = data.stream().map(SubsidyData::getSm5).reduce(ZERO, BigDecimal::add);
        BigDecimal sm6Sum = data.stream().map(SubsidyData::getSm6).reduce(ZERO, BigDecimal::add);
        BigDecimal sm7Sum = data.stream().map(SubsidyData::getSm7).reduce(ZERO, BigDecimal::add);
        BigDecimal sm8Sum = data.stream().map(SubsidyData::getSm8).reduce(ZERO, BigDecimal::add);

        List<SubsidySplit> subsidySplits = new ArrayList<>();

        for (int i = 0; i < data.size() - 1; ++i){
            SubsidyData subsidyData = data.get(i);

            SubsidySplit subsidySplit = new SubsidySplit();

            subsidySplit.setSubsidyId(subsidy.getId());

            LocalDate date = new Date(data.get(i).getSubsMonth().getTime()).toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();

            subsidySplit.putField(SubsidySplitField.DAT1, Date.from(date
                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));
            subsidySplit.putField(SubsidySplitField.DAT2, Date.from(date.with(TemporalAdjusters.lastDayOfMonth())
                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));

            if (sm1Sum.compareTo(ZERO) != 0){
                subsidySplit.putField(SubsidySplitField.SM1, subsidy.getBigDecimalField(SubsidyDBF.SM1)
                        .multiply(subsidyData.getSm1()).divide(sm1Sum, 2, HALF_EVEN));
            }else{
                subsidySplit.putField(SubsidySplitField.SM1, ZERO);
            }

            if (sm2Sum.compareTo(ZERO) != 0) {
                subsidySplit.putField(SubsidySplitField.SM2, subsidy.getBigDecimalField(SubsidyDBF.SM2)
                        .multiply(subsidyData.getSm2()).divide(sm2Sum, 2, HALF_EVEN));
            }else{
                subsidySplit.putField(SubsidySplitField.SM2, ZERO);
            }

            if (sm3Sum.compareTo(ZERO) != 0) {
                subsidySplit.putField(SubsidySplitField.SM3, subsidy.getBigDecimalField(SubsidyDBF.SM3)
                        .multiply(subsidyData.getSm3()).divide(sm3Sum, 2, HALF_EVEN));
            }else{
                subsidySplit.putField(SubsidySplitField.SM3, ZERO);
            }

            if (sm4Sum.compareTo(ZERO) != 0) {
                subsidySplit.putField(SubsidySplitField.SM4, subsidy.getBigDecimalField(SubsidyDBF.SM4)
                        .multiply(subsidyData.getSm4()).divide(sm4Sum, 2, HALF_EVEN));
            }else{
                subsidySplit.putField(SubsidySplitField.SM4, ZERO);
            }

            if (sm5Sum.compareTo(ZERO) != 0) {
                subsidySplit.putField(SubsidySplitField.SM5, subsidy.getBigDecimalField(SubsidyDBF.SM5)
                        .multiply(subsidyData.getSm5()).divide(sm5Sum, 2, HALF_EVEN));
            }else{
                subsidySplit.putField(SubsidySplitField.SM5, ZERO);
            }

            if (sm6Sum.compareTo(ZERO) != 0) {
                subsidySplit.putField(SubsidySplitField.SM6, subsidy.getBigDecimalField(SubsidyDBF.SM6)
                        .multiply(subsidyData.getSm6()).divide(sm6Sum, 2, HALF_EVEN));
            }else{
                subsidySplit.putField(SubsidySplitField.SM6, ZERO);
            }

            if (sm7Sum.compareTo(ZERO) != 0) {
                subsidySplit.putField(SubsidySplitField.SM7, subsidy.getBigDecimalField(SubsidyDBF.SM7)
                        .multiply(subsidyData.getSm7()).divide(sm7Sum, 2, HALF_EVEN));
            }else{
                subsidySplit.putField(SubsidySplitField.SM7, ZERO);
            }

            if (sm8Sum.compareTo(ZERO) != 0) {
                subsidySplit.putField(SubsidySplitField.SM8, subsidy.getBigDecimalField(SubsidyDBF.SM8)
                        .multiply(subsidyData.getSm8()).divide(sm8Sum, 2, HALF_EVEN));
            }else{
                subsidySplit.putField(SubsidySplitField.SM8, ZERO);
            }

            BigDecimal smSum = subsidySplit.getBigDecimalField(SubsidySplitField.SM1)
                    .add(subsidySplit.getBigDecimalField(SubsidySplitField.SM2))
                    .add(subsidySplit.getBigDecimalField(SubsidySplitField.SM3))
                    .add(subsidySplit.getBigDecimalField(SubsidySplitField.SM4))
                    .add(subsidySplit.getBigDecimalField(SubsidySplitField.SM5))
                    .add(subsidySplit.getBigDecimalField(SubsidySplitField.SM6))
                    .add(subsidySplit.getBigDecimalField(SubsidySplitField.SM7))
                    .add(subsidySplit.getBigDecimalField(SubsidySplitField.SM8));

            subsidySplit.putField(SubsidySplitField.SUMMA, smSum);

            subsidySplit.putField(SubsidySplitField.SUBS, subsidy.getBigDecimalField(SubsidyDBF.SUBS));
            subsidySplit.putField(SubsidySplitField.NUMM, 1);

            subsidySplits.add(subsidySplit);
        }


        SubsidySplit subsidySplit = new SubsidySplit();

        subsidySplit.setSubsidyId(subsidy.getId());

        LocalDate date = new Date(data.get(data.size() - 1).getSubsMonth().getTime()).toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();

        subsidySplit.putField(SubsidySplitField.DAT1, Date.from(date
                .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        subsidySplit.putField(SubsidySplitField.DAT2, Date.from(date.with(TemporalAdjusters.lastDayOfMonth())
                .atStartOfDay(ZoneId.systemDefault()).toInstant()));

        subsidySplit.putField(SubsidySplitField.SM1, subsidy.getBigDecimalField(SubsidyDBF.SM1)
                .subtract(subsidySplits.stream() .map(s -> s.getBigDecimalField(SubsidySplitField.SM1))
                        .reduce(ZERO, BigDecimal::add)));
        subsidySplit.putField(SubsidySplitField.SM2, subsidy.getBigDecimalField(SubsidyDBF.SM2)
                .subtract(subsidySplits.stream() .map(s -> s.getBigDecimalField(SubsidySplitField.SM2))
                        .reduce(ZERO, BigDecimal::add)));
        subsidySplit.putField(SubsidySplitField.SM3, subsidy.getBigDecimalField(SubsidyDBF.SM3)
                .subtract(subsidySplits.stream() .map(s -> s.getBigDecimalField(SubsidySplitField.SM3))
                        .reduce(ZERO, BigDecimal::add)));
        subsidySplit.putField(SubsidySplitField.SM4, subsidy.getBigDecimalField(SubsidyDBF.SM4)
                .subtract(subsidySplits.stream() .map(s -> s.getBigDecimalField(SubsidySplitField.SM4))
                        .reduce(ZERO, BigDecimal::add)));
        subsidySplit.putField(SubsidySplitField.SM5, subsidy.getBigDecimalField(SubsidyDBF.SM5)
                .subtract(subsidySplits.stream() .map(s -> s.getBigDecimalField(SubsidySplitField.SM5))
                        .reduce(ZERO, BigDecimal::add)));
        subsidySplit.putField(SubsidySplitField.SM6, subsidy.getBigDecimalField(SubsidyDBF.SM6)
                .subtract(subsidySplits.stream() .map(s -> s.getBigDecimalField(SubsidySplitField.SM6))
                        .reduce(ZERO, BigDecimal::add)));
        subsidySplit.putField(SubsidySplitField.SM7, subsidy.getBigDecimalField(SubsidyDBF.SM7)
                .subtract(subsidySplits.stream() .map(s -> s.getBigDecimalField(SubsidySplitField.SM7))
                        .reduce(ZERO, BigDecimal::add)));
        subsidySplit.putField(SubsidySplitField.SM8, subsidy.getBigDecimalField(SubsidyDBF.SM8)
                .subtract(subsidySplits.stream().map(s -> s.getBigDecimalField(SubsidySplitField.SM8))
                        .reduce(ZERO, BigDecimal::add)));

        subsidySplit.putField(SubsidySplitField.SUMMA, subsidy.getBigDecimalField(SubsidyDBF.SUMMA)
                .subtract(subsidySplits.stream().map(s -> s.getBigDecimalField(SubsidySplitField.SUMMA))
                        .reduce(ZERO, BigDecimal::add)));

        subsidySplit.putField(SubsidySplitField.SUBS, subsidy.getBigDecimalField(SubsidyDBF.SUBS));
        subsidySplit.putField(SubsidySplitField.NUMM, 1);

        subsidySplits.add(subsidySplit);


        subsidySplits.forEach(s -> {
            if (s.getBigDecimalField(SubsidySplitField.SUMMA).compareTo(ZERO) != 0) {
                subsidySplitBean.save(s);
            }
        });

        BigDecimal sumSumma = subsidySplits.stream()
                .map(s -> s.getBigDecimalField(SubsidySplitField.SUMMA)).reduce(ZERO, BigDecimal::add);

        if (subsidy.getBigDecimalField(SubsidyDBF.SUMMA).compareTo(sumSumma) == 0){
            subsidy.setStatus(RequestStatus.SUBSIDY_RECALCULATED);
            subsidyBean.update(subsidy);

            log.info("subsidy fill: add subsidy split recalculated {}", subsidySplits);
        }else{
            if (sumSumma.equals(ZERO) && subsidy.getBigDecimalField(SubsidyDBF.SUMMA).compareTo(ZERO) != 0) {
                split(subsidy, d1, d2, (int) d1.until(d2).toTotalMonths() + 1);

                log.info("subsidy fill: subsidy split recalculated 0 sum {} {}", subsidy, subsidySplits);
            }else{
                subsidy.setStatus(RequestStatus.SUBSIDY_RECALCULATE_ERROR);
                subsidyBean.update(subsidy);

                log.info("subsidy fill error: subsidy split recalculate error {}", subsidySplits);
            }
        }
    }

    private boolean isDifferentSign(BigDecimal d1, BigDecimal d2){
        return d1 != null && d1.signum() != 0 && d2 != null && d2.signum() != 0 && d1.signum() != d2.signum();
    }


    @Override
    public String getModuleName() {
        return Module.NAME;
    }

    @Override
    public Log.EVENT getEvent() {
        return Log.EVENT.EDIT;
    }
}
