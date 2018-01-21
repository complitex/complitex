package org.complitex.osznconnection.file.service.subsidy;

import org.complitex.common.entity.DomainObject;
import org.complitex.common.util.DateUtil;
import org.complitex.osznconnection.file.entity.AbstractRequest;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.subsidy.Subsidy;
import org.complitex.osznconnection.file.entity.subsidy.SubsidySum;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.subsidy.task.SubsidyBindTaskBean;
import org.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Locale;

/**
 * @author Anatoly Ivanov java@inheaven.ru
 *         Date: 10.12.13 0:16
 */
@Stateless
public class SubsidyService {
    @EJB
    private OsznOrganizationStrategy organizationStrategy;

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private SubsidyBean subsidyBean;

    @EJB
    private SubsidyBindTaskBean subsidyBindTaskBean;

    public SubsidySum getSubsidySum(AbstractRequest request){
        DomainObject organization = organizationStrategy.getDomainObject(request.getUserOrganizationId(), true);

        BigDecimal nSum = new BigDecimal(0);
        BigDecimal sbSum = new BigDecimal(0);
        BigDecimal smSum = new BigDecimal(0);

        //for (Attribute sa : organization.getAttributes(IOrganizationStrategy.SERVICE)) { todo
        for (int i = 1; i < 9; ++i) {
            BigDecimal p = (BigDecimal) request.getField("P" + i);
            if (p != null) {
                nSum = nSum.add(p);
            }

            BigDecimal sb = (BigDecimal) request.getField("SB" + i);

            if (sb != null) {
                sbSum = sbSum.add(sb);
            }

            BigDecimal sm = (BigDecimal) request.getField("SM" + i);
            if (sm != null) {
                smSum = smSum.add(sm);
            }
        }

        //round Pn
        nSum = nSum.setScale(2, RoundingMode.HALF_UP);

        return new SubsidySum(nSum, sbSum, smSum);
    }

    public boolean validateSum(AbstractRequest request){
        SubsidySum subsidySum = getSubsidySum(request);

        int numm = request.getField("NUMM") != null ? ((Number)request.getField("NUMM")).intValue() : 0;
        BigDecimal summa = (BigDecimal) request.getField("SUMMA");
        BigDecimal subs = (BigDecimal) request.getField("SUBS");
        BigDecimal nmPay = (BigDecimal) request.getField("NM_PAY");

        return nmPay != null && nmPay.compareTo(subsidySum.getNSum()) == 0
                && summa != null && summa.compareTo(subsidySum.getSmSum()) == 0
                && subs != null && subs.compareTo(subsidySum.getSbSum()) == 0
                && (numm <= 0 || summa.compareTo(subs.multiply(new BigDecimal(numm))) == 0);
    }

    public boolean validateDate(AbstractRequest request){
        Date dat1 = (Date) request.getField("DAT1");
        Date dat2 = (Date) request.getField("DAT2");

        return dat1.before(dat2) && DateUtil.getMonthDiff(dat1, dat2) <= 6;
    }


    public String displayServicingOrganization(RequestFile requestFile, Locale locale){
        Long organizationId = organizationStrategy.getServiceProviderId(requestFile.getEdrpou(),
                requestFile.getOrganizationId(), requestFile.getUserOrganizationId());

        if (organizationId != null){
            return organizationStrategy.displayShortNameAndCode(organizationStrategy.getDomainObject(organizationId, true), locale);
        }else {
            return requestFile.getEdrpou();
        }
    }

    public void bind(String serviceProviderCode, Subsidy subsidy) {
        subsidyBindTaskBean.bind(serviceProviderCode, subsidy, false);

        if (subsidyBean.isSubsidyFileBound(subsidy.getRequestFileId())) {
            RequestFile requestFile = requestFileBean.getRequestFile(subsidy.getRequestFileId());
            requestFile.setStatus(RequestFileStatus.BOUND);
            requestFileBean.save(requestFile);
        }
    }
}
