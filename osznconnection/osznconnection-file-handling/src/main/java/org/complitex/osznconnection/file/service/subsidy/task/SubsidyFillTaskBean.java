package org.complitex.osznconnection.file.service.subsidy.task;

import org.complitex.address.exception.RemoteCallException;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.Log;
import org.complitex.common.exception.CanceledByUserException;
import org.complitex.common.exception.ExecuteException;
import org.complitex.common.service.executor.AbstractTaskBean;
import org.complitex.osznconnection.file.Module;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.RequestStatus;
import org.complitex.osznconnection.file.entity.subsidy.*;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.exception.FillException;
import org.complitex.osznconnection.file.service.subsidy.SubsidyBean;
import org.complitex.osznconnection.file.service.subsidy.SubsidySplitBean;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_EVEN;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 15.01.14 18:36
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class SubsidyFillTaskBean extends AbstractTaskBean<RequestFile> {
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
                onRequest(subsidy);
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

    private void fill(RequestFile requestFile, Subsidy subsidy) {
        LocalDate dat1 = new Date(subsidy.getDateField(SubsidyDBF.DAT1).getTime()).toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate dat2 = new Date(subsidy.getDateField(SubsidyDBF.DAT2).getTime()).toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();

        Integer numm = subsidy.getIntegerField(SubsidyDBF.NUMM);

        subsidySplitBean.clearSubsidySplits(subsidy.getId());

        if (dat1.getDayOfMonth() == 1){
            if (numm > 1){
                if (subsidy.getBigDecimalField(SubsidyDBF.SUBS).multiply(BigDecimal.valueOf(numm))
                        .compareTo(subsidy.getBigDecimalField(SubsidyDBF.SUMMA)) == 0){
                    if (dat2.compareTo(dat1) > 0){
                        LocalDate date;

                        Period dat = dat1.until(dat2.plusDays(1));

                        if (dat.getMonths() == numm){
                            date = dat1;
                        }else if (dat.getMonths() > numm){
                            date = requestFile.getBeginDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                                    .minusMonths(numm);
                        }else{
                            subsidy.setStatus(RequestStatus.PROCESSED_WITH_ERROR);
                            subsidyBean.update(subsidy);

                            log.error("subsidy fill error: dat2 - dat1 < numm {}", subsidy);

                            return;
                        }

                        split(subsidy, numm, date);
                    }else{
                        subsidy.setStatus(RequestStatus.PROCESSED_WITH_ERROR);
                        subsidyBean.update(subsidy);

                        log.error("subsidy fill error: dat1 > dat2 {}", subsidy);
                    }
                }else {
                    subsidy.setStatus(RequestStatus.PROCESSED_WITH_ERROR);
                    subsidyBean.update(subsidy);

                    log.error("subsidy fill error: subs*numm != summa {}", subsidy);
                }
            }else if (numm == 1){
                if (!RequestStatus.unboundStatuses().contains(subsidy.getStatus())) {
                    subsidy.setStatus(RequestStatus.PROCESSED);
                    subsidyBean.update(subsidy);
                }
            } else {
                subsidy.setStatus(RequestStatus.PROCESSED_WITH_ERROR);
                subsidyBean.update(subsidy);

                log.error("subsidy fill error: numm < 1 {}", subsidy);
            }
        }else {
            if (subsidy.getBigDecimalField(SubsidyDBF.SUMMA).compareTo(ZERO) == 0){
                subsidy.setStatus(RequestStatus.PROCESSED);
                subsidyBean.update(subsidy);

                return;
            }

            if (dat1.until(dat2).getMonths() == 1) {
                split(subsidy, 1, dat1.withDayOfMonth(1));

                log.info("subsidy fill: subsidy splits recalculate 1 month {}", subsidy);
            }else{
                recalculation(requestFile, subsidy);
            }
        }
    }

    private void split(Subsidy subsidy, Integer numm, LocalDate date) {
        List<SubsidySplit> subsidySplits = new ArrayList<>();

        for (int i = 0; i < numm; ++i){
            SubsidySplit subsidySplit = new SubsidySplit();

            subsidySplit.setSubsidyId(subsidy.getId());

            subsidySplit.putField(SubsidySplitField.DAT1, Date.from(date.plusMonths(i)
                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));
            subsidySplit.putField(SubsidySplitField.DAT2, Date.from(date.plusMonths(i + 1).minusDays(1)
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

            if (subsidySplit.getBigDecimalField(SubsidySplitField.SUMMA).compareTo(ZERO) > 0) {
                subsidySplitBean.save(subsidySplit);

                subsidySplits.add(subsidySplit);
            }
        }

        if (subsidy.getBigDecimalField(SubsidyDBF.SUMMA).compareTo(subsidySplits.stream()
                .map(s -> s.getBigDecimalField(SubsidySplitField.SUMMA)).reduce(ZERO, BigDecimal::add)) == 0){
            subsidy.setStatus(RequestStatus.SUBSIDY_SPLITTED);

            log.info("subsidy fill: add subsidy splits {}", subsidySplits);
        }else{
            subsidy.setStatus(RequestStatus.SUBSIDY_SPLIT_ERROR);

            log.info("subsidy fill error: subsidy splits error {}", subsidySplits);
        }

        subsidyBean.update(subsidy);
    }

    private void recalculation(RequestFile requestFile, Subsidy subsidy) {
        try {
            Cursor<SubsidyData> subsidyDataCursor = serviceProviderAdapter.getSubsidyData(
                    requestFile.getUserOrganizationId(), subsidy.getAccountNumber(),
                    subsidy.getDateField(SubsidyDBF.DAT1),
                    subsidy.getDateField(SubsidyDBF.DAT2));

            if (!subsidyDataCursor.isEmpty()){
                List<SubsidyData> data = subsidyDataCursor.getData();

                data.sort(Comparator.comparing(SubsidyData::getOpMonth));

                BigDecimal summa = data.stream()
                        .map(d -> d.getSm1()
                                .add(d.getSm2())
                                .add(d.getSm3())
                                .add(d.getSm4())
                                .add(d.getSm5())
                                .add(d.getSm6())
                                .add(d.getSm7())
                                .add(d.getSm8()))
                        .reduce(ZERO, BigDecimal::add);

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

                    LocalDate date = new Date(data.get(i).getOpMonth().getTime()).toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate();

                    subsidySplit.putField(SubsidySplitField.DAT1, Date.from(date
                            .atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    subsidySplit.putField(SubsidySplitField.DAT2, Date.from(date.plusMonths(1).minusDays(1)
                            .atStartOfDay(ZoneId.systemDefault()).toInstant()));

                    if (sm1Sum.compareTo(ZERO) > 0){
                        subsidySplit.putField(SubsidySplitField.SM1, subsidy.getBigDecimalField(SubsidyDBF.SM1)
                                .multiply(subsidyData.getSm1()).divide(sm1Sum, 2, HALF_EVEN));
                    }else{
                        subsidySplit.putField(SubsidySplitField.SM1, ZERO);
                    }

                    if (sm2Sum.compareTo(ZERO) > 0) {
                        subsidySplit.putField(SubsidySplitField.SM2, subsidy.getBigDecimalField(SubsidyDBF.SM2)
                                .multiply(subsidyData.getSm2()).divide(sm2Sum, 2, HALF_EVEN));
                    }else{
                        subsidySplit.putField(SubsidySplitField.SM2, ZERO);
                    }

                    if (sm3Sum.compareTo(ZERO) > 0) {
                        subsidySplit.putField(SubsidySplitField.SM3, subsidy.getBigDecimalField(SubsidyDBF.SM3)
                                .multiply(subsidyData.getSm3()).divide(sm3Sum, 2, HALF_EVEN));
                    }else{
                        subsidySplit.putField(SubsidySplitField.SM3, ZERO);
                    }

                    if (sm4Sum.compareTo(ZERO) > 0) {
                        subsidySplit.putField(SubsidySplitField.SM4, subsidy.getBigDecimalField(SubsidyDBF.SM4)
                                .multiply(subsidyData.getSm4()).divide(sm4Sum, 2, HALF_EVEN));
                    }else{
                        subsidySplit.putField(SubsidySplitField.SM4, ZERO);
                    }

                    if (sm5Sum.compareTo(ZERO) > 0) {
                        subsidySplit.putField(SubsidySplitField.SM5, subsidy.getBigDecimalField(SubsidyDBF.SM5)
                                .multiply(subsidyData.getSm5()).divide(sm5Sum, 2, HALF_EVEN));
                    }else{
                        subsidySplit.putField(SubsidySplitField.SM5, ZERO);
                    }

                    if (sm6Sum.compareTo(ZERO) > 0) {
                        subsidySplit.putField(SubsidySplitField.SM6, subsidy.getBigDecimalField(SubsidyDBF.SM6)
                                .multiply(subsidyData.getSm6()).divide(sm6Sum, 2, HALF_EVEN));
                    }else{
                        subsidySplit.putField(SubsidySplitField.SM6, ZERO);
                    }

                    if (sm7Sum.compareTo(ZERO) > 0) {
                        subsidySplit.putField(SubsidySplitField.SM7, subsidy.getBigDecimalField(SubsidyDBF.SM7)
                                .multiply(subsidyData.getSm7()).divide(sm7Sum, 2, HALF_EVEN));
                    }else{
                        subsidySplit.putField(SubsidySplitField.SM7, ZERO);
                    }

                    if (sm8Sum.compareTo(ZERO) > 0) {
                        subsidySplit.putField(SubsidySplitField.SM8, subsidy.getBigDecimalField(SubsidyDBF.SM8)
                                .multiply(subsidyData.getSm8()).divide(sm8Sum, 2, HALF_EVEN));
                    }else{
                        subsidySplit.putField(SubsidySplitField.SM8, ZERO);
                    }

                    BigDecimal smSum = subsidyData.getSm1()
                            .add(subsidyData.getSm2())
                            .add(subsidyData.getSm3())
                            .add(subsidyData.getSm4())
                            .add(subsidyData.getSm5())
                            .add(subsidyData.getSm6())
                            .add(subsidyData.getSm7())
                            .add(subsidyData.getSm8());

                    subsidySplit.putField(SubsidySplitField.SUMMA, subsidy.getBigDecimalField(SubsidyDBF.SUMMA)
                            .multiply(smSum).divide(summa, 2, HALF_EVEN));

                    subsidySplit.putField(SubsidySplitField.SUBS, subsidy.getBigDecimalField(SubsidyDBF.SUBS));
                    subsidySplit.putField(SubsidySplitField.NUMM, 1);
                }

                {
                    SubsidySplit subsidySplit = new SubsidySplit();

                    subsidySplit.setSubsidyId(subsidy.getId());

                    LocalDate date = new Date(data.get(data.size() - 1).getOpMonth().getTime()).toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate();

                    subsidySplit.putField(SubsidySplitField.DAT1, Date.from(date
                            .atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    subsidySplit.putField(SubsidySplitField.DAT2, Date.from(date.plusMonths(1).minusDays(1)
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
                }

                subsidySplits.forEach(s -> {
                    if (s.getBigDecimalField(SubsidySplitField.SUMMA).compareTo(ZERO) > 0) {
                        subsidySplitBean.save(s);
                    }
                });

                BigDecimal sumSumma = subsidySplits.stream()
                        .map(s -> s.getBigDecimalField(SubsidySplitField.SUMMA)).reduce(ZERO, BigDecimal::add);

                if (subsidy.getBigDecimalField(SubsidyDBF.SUMMA).compareTo(sumSumma) == 0){
                    subsidy.setStatus(RequestStatus.SUBSIDY_RECALCULATED);

                    log.info("subsidy fill: add subsidy split recalculated {}", subsidySplits);
                }else{
                    if (sumSumma.equals(ZERO)) {
                        LocalDate dat1 = new Date(subsidy.getDateField(SubsidyDBF.DAT1).getTime()).toInstant()
                                .atZone(ZoneId.systemDefault()).toLocalDate();
                        LocalDate dat2 = new Date(subsidy.getDateField(SubsidyDBF.DAT2).getTime()).toInstant()
                                .atZone(ZoneId.systemDefault()).toLocalDate();

                        split(subsidy, dat1.until(dat2.plusDays(1)).getMonths(), dat1);

                        log.info("subsidy fill: subsidy split recalculated 0 sum {} {}", subsidy, subsidySplits);
                    }else{
                        subsidy.setStatus(RequestStatus.SUBSIDY_RECALCULATE_ERROR);

                        log.info("subsidy fill error: subsidy split recalculate error {}", subsidySplits);
                    }
                }

                subsidyBean.update(subsidy);
            }else{
                subsidy.setStatus(RequestStatus.PROCESSED_WITH_ERROR);
                subsidyBean.update(subsidy);

                log.error("subsidy fill error: empty subsidy data cursor {} {}", subsidy, subsidyDataCursor);
            }
        } catch (RemoteCallException e) {
            throw new RuntimeException(e);
        }
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
