package ru.complitex.osznconnection.file.service.subsidy;

import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.util.DateUtil;
import ru.complitex.osznconnection.file.entity.AbstractRequest;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.RequestFileStatus;
import ru.complitex.osznconnection.file.entity.subsidy.Subsidy;
import ru.complitex.osznconnection.file.entity.subsidy.SubsidyDBF;
import ru.complitex.osznconnection.file.entity.subsidy.SubsidySplitField;
import ru.complitex.osznconnection.file.entity.subsidy.SubsidySum;
import ru.complitex.osznconnection.file.service.RequestFileBean;
import ru.complitex.osznconnection.file.service.subsidy.task.SubsidyBindTaskBean;
import ru.complitex.osznconnection.file.service.subsidy.task.SubsidyFillTaskBean;
import ru.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static ru.complitex.osznconnection.file.entity.RequestStatus.SUBSIDY_RECALCULATED;
import static ru.complitex.osznconnection.file.entity.RequestStatus.SUBSIDY_SPLITTED;

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

    @EJB
    private SubsidyFillTaskBean subsidyFillTaskBean;

    @EJB
    private SubsidySplitBean subsidySplitBean;

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

    public void bind(String serviceProviderCode, Long billingId, Subsidy subsidy) {
        subsidyBindTaskBean.bind(serviceProviderCode, billingId, subsidy);

        if (subsidyBean.isSubsidyFileBound(subsidy.getRequestFileId())) {
            RequestFile requestFile = requestFileBean.getRequestFile(subsidy.getRequestFileId());
            requestFile.setStatus(RequestFileStatus.BOUND);
            requestFileBean.save(requestFile);
        }
    }

    public void fill(RequestFile requestFile, Subsidy subsidy) {
        subsidyFillTaskBean.fill(requestFile, subsidy);

        if (subsidyBean.isSubsidyFileFilled(subsidy.getRequestFileId())) {
            requestFile.setStatus(RequestFileStatus.FILLED);
            requestFileBean.save(requestFile);
        }
    }

    public List<Subsidy> getSubsidyWithSplitList(Long requestFileId) {
        List<Subsidy> subsidies =  subsidyBean.getSubsidies(requestFileId);

        List<Subsidy> list = new ArrayList<>();

        for (Iterator<Subsidy> iterator = subsidies.iterator(); iterator.hasNext(); ) {
            Subsidy s = iterator.next();

            if (SUBSIDY_SPLITTED.equals(s.getStatus()) || SUBSIDY_RECALCULATED.equals(s.getStatus())) {
                subsidySplitBean.getSubsidySplits(s.getId()).forEach(split -> {
                    Subsidy subsidy = subsidyBean.getSubsidy(s.getId());

                    subsidy.putField(SubsidyDBF.DAT1, split.getDateField(SubsidySplitField.DAT1));
                    subsidy.putField(SubsidyDBF.DAT2, split.getDateField(SubsidySplitField.DAT2));

                    subsidy.putField(SubsidyDBF.SM1, split.getBigDecimalField(SubsidySplitField.SM1));
                    subsidy.putField(SubsidyDBF.SM2, split.getBigDecimalField(SubsidySplitField.SM2));
                    subsidy.putField(SubsidyDBF.SM3, split.getBigDecimalField(SubsidySplitField.SM3));
                    subsidy.putField(SubsidyDBF.SM4, split.getBigDecimalField(SubsidySplitField.SM4));
                    subsidy.putField(SubsidyDBF.SM5, split.getBigDecimalField(SubsidySplitField.SM5));
                    subsidy.putField(SubsidyDBF.SM6, split.getBigDecimalField(SubsidySplitField.SM6));
                    subsidy.putField(SubsidyDBF.SM7, split.getBigDecimalField(SubsidySplitField.SM7));
                    subsidy.putField(SubsidyDBF.SM8, split.getBigDecimalField(SubsidySplitField.SM8));

                    subsidy.putField(SubsidyDBF.SUMMA, split.getBigDecimalField(SubsidySplitField.SUMMA));
                    subsidy.putField(SubsidyDBF.SUBS, split.getBigDecimalField(SubsidySplitField.SUBS));
                    subsidy.putField(SubsidyDBF.NUMM, split.getIntegerField(SubsidySplitField.NUMM));

                    list.add(subsidy);
                });

                iterator.remove();
            }
        }

        subsidies.addAll(list);
        subsidies.sort(Comparator.comparing(s -> s.getStringField(SubsidyDBF.RASH)));
        return subsidies;
    }
}
