package org.complitex.osznconnection.file.service.subsidy.task;

import org.complitex.common.entity.Log;
import org.complitex.common.exception.CanceledByUserException;
import org.complitex.common.exception.ExecuteException;
import org.complitex.common.service.executor.AbstractTaskBean;
import org.complitex.osznconnection.file.Module;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.RequestStatus;
import org.complitex.osznconnection.file.entity.subsidy.Subsidy;
import org.complitex.osznconnection.file.entity.subsidy.SubsidyDBF;
import org.complitex.osznconnection.file.entity.subsidy.SubsidySplit;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.exception.AlreadyProcessingException;
import org.complitex.osznconnection.file.service.exception.FillException;
import org.complitex.osznconnection.file.service.subsidy.SubsidyBean;
import org.complitex.osznconnection.file.service.subsidy.SubsidySplitBean;
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
import java.util.List;
import java.util.Map;

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

    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        try {
            //проверяем что не обрабатывается в данный момент
            if (requestFileBean.getRequestFileStatus(requestFile.getId()).isProcessing()) {
                throw new FillException(new AlreadyProcessingException(requestFile.getFullName()), true, requestFile);
            }

            requestFile.setStatus(RequestFileStatus.FILLING);
            requestFileBean.save(requestFile);

            //Обработка
            List<Subsidy> subsidies = subsidyBean.getSubsidies(requestFile.getId());

            for (Subsidy subsidy : subsidies){
                if (requestFile.isCanceled()){
                    throw new FillException(new CanceledByUserException(), true, requestFile);
                }

                fill(subsidy);
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

    private void fill(Subsidy subsidy) {
        LocalDate dat1 = subsidy.getDateField(SubsidyDBF.DAT1).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate dat2 = subsidy.getDateField(SubsidyDBF.DAT2).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Integer numm = subsidy.getIntegerField(SubsidyDBF.NUMM);

        if (dat1.getDayOfMonth() == 1){
            if (numm > 1){
                if (subsidy.getBigDecimalField(SubsidyDBF.SUBS).multiply(BigDecimal.valueOf(numm))
                        .compareTo(subsidy.getBigDecimalField(SubsidyDBF.SUMMA)) == 0){
                    if (dat2.compareTo(dat1) > 0){
                        LocalDate date;

                        Period dat = dat1.until(dat2);

                        if (dat.getMonths() == numm){
                            date = dat1;
                        }else if (dat.getMonths() > numm){
                            date = subsidy.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                                    .withDayOfMonth(1)
                                    .minusMonths(numm);
                        }else{
                            subsidy.setStatus(RequestStatus.PROCESSED_WITH_ERROR);
                            subsidyBean.update(subsidy);

                            log.error("subsidy fill error: dat2 - dat1 < numm {}", subsidy);

                            return;
                        }

                        for (int i = 0; i < numm; ++i){
                            subsidySplitBean.clearSubsidySplits(subsidy.getId());

                            SubsidySplit subsidySplit = new SubsidySplit();

                            //todo insert values
                        }
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
            }else if (numm < 1){
                subsidy.setStatus(RequestStatus.PROCESSED_WITH_ERROR);
                subsidyBean.update(subsidy);

                log.error("subsidy fill error: numm < 1 {}", subsidy);
            }
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
