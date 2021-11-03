package org.complitex.osznconnection.file.service.subsidy.task;

import org.complitex.address.exception.RemoteCallException;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.exception.CanceledByUserException;
import org.complitex.common.exception.ExecuteException;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.RequestStatus;
import org.complitex.osznconnection.file.entity.subsidy.OschadbankRequest;
import org.complitex.osznconnection.file.entity.subsidy.OschadbankRequestField;
import org.complitex.osznconnection.file.entity.subsidy.OschadbankRequestFile;
import org.complitex.osznconnection.file.entity.subsidy.OschadbankRequestFileField;
import org.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.exception.FillException;
import org.complitex.osznconnection.file.service.process.ProcessType;
import org.complitex.osznconnection.file.service.subsidy.OschadbankRequestBean;
import org.complitex.osznconnection.file.service.subsidy.OschadbankRequestFileBean;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.complitex.osznconnection.file.service_provider.entity.ChargeToPay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov
 * 28.03.2019 19:07
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class OschadbankRequestFillTaskBean extends AbstractRequestTaskBean<RequestFile> {
    private final Logger log = LoggerFactory.getLogger(OschadbankRequestFillTaskBean.class);

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private OschadbankRequestBean oschadbankRequestBean;

    @EJB
    private OschadbankRequestFileBean oschadbankRequestFileBean;

    @EJB
    private ServiceProviderAdapter serviceProviderAdapter;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");

    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        try {
            requestFile.setStatus(RequestFileStatus.FILLING);
            requestFileBean.save(requestFile);

            OschadbankRequestFile oschadbankRequestFile = oschadbankRequestFileBean.getOschadbankRequestFile(requestFile.getId());

            Date opMonth = dateFormat.parse(oschadbankRequestFile.getStringField(OschadbankRequestFileField.REPORTING_PERIOD));

            List<OschadbankRequest> oschadbankRequests = oschadbankRequestBean.getOschadbankRequests(FilterWrapper.of(
                    new OschadbankRequest(requestFile.getId())));

            for (OschadbankRequest request : oschadbankRequests){
                if (requestFile.isCanceled()){
                    throw new FillException(new CanceledByUserException(), true, requestFile);
                }

                ChargeToPay chargeToPay = new ChargeToPay();
                chargeToPay.setpAcc(request.getStringField(OschadbankRequestField.SERVICE_ACCOUNT));
                chargeToPay.setpDate(opMonth);

                chargeToPay = serviceProviderAdapter.getChargeToPay(requestFile.getUserOrganizationId(), chargeToPay);

                if (chargeToPay.getResultCode() == 1){
                    request.putField(OschadbankRequestField.MONTH_SUM, chargeToPay.getpCharge());

                    if (chargeToPay.getpToPay() .compareTo(BigDecimal.ZERO) > 0) {
                        request.putField(OschadbankRequestField.SUM, chargeToPay.getpToPay());
                    } else {
                        request.putField(OschadbankRequestField.SUM, BigDecimal.ZERO);
                    }

                    request.setStatus(RequestStatus.PROCESSED);
                    oschadbankRequestBean.save(request);
                }else{
                    if (chargeToPay.getResultCode() == 0){
                        request.setStatus(RequestStatus.ACCOUNT_NUMBER_NOT_FOUND);
                    } else if (chargeToPay.getResultCode() == -12){
                        request.setStatus(RequestStatus.MORE_ONE_ACCOUNTS);
                    } else{
                        request.setStatus(RequestStatus.PROCESSED_WITH_ERROR);
                    }

                    oschadbankRequestBean.save(request);

                    log.error("oschadbank fill error {} {}", request, chargeToPay);
                }

                onRequest(request, ProcessType.FILL_OSCHADBANK_REQUEST);
            }

            if (!oschadbankRequestBean.isOschadbankRequestFileFilled(requestFile.getId())) {
                throw new FillException(true, requestFile);
            }

            requestFile.setStatus(RequestFileStatus.FILLED);
            requestFileBean.save(requestFile);

            return true;
        } catch (Exception e) {
            requestFile.setStatus(RequestFileStatus.FILL_ERROR);
            requestFileBean.save(requestFile);

            try {
                throw e;
            } catch (ParseException | RemoteCallException ex) {
                throw new ExecuteException(ex, false, "");
            }
        }
    }
}
