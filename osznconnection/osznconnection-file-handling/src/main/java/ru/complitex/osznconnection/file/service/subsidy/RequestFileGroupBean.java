package ru.complitex.osznconnection.file.service.subsidy;

import ru.complitex.common.service.AbstractBean;
import ru.complitex.common.service.SessionBean;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.RequestFileStatus;
import ru.complitex.osznconnection.file.entity.subsidy.RequestFileGroup;
import ru.complitex.osznconnection.file.entity.subsidy.RequestFileGroupFilter;
import ru.complitex.osznconnection.file.service.RequestFileBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;

import static ru.complitex.osznconnection.file.entity.RequestFileStatus.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 29.09.2010 14:32:05
 */
@Stateless(name = "RequestFileGroupBean")
public class RequestFileGroupBean extends AbstractBean {

    public static final String NS = RequestFileGroupBean.class.getName();

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private SessionBean sessionBean;

    @EJB
    private PaymentBean paymentBean;

    @EJB
    private BenefitBean benefitBean;

    @SuppressWarnings({"unchecked"})
    public List<RequestFileGroup> getRequestFileGroups(RequestFileGroupFilter filter) {
        sessionBean.authorize(filter);

        return sqlSession().selectList(NS + ".selectRequestFilesGroups", filter);
    }

    public Long getRequestFileGroupsCount(RequestFileGroupFilter filter) {
        sessionBean.authorize(filter);

        return sqlSession().selectOne(NS + ".selectRequestFilesGroupsCount", filter);
    }

    public RequestFileGroup getRequestFileGroup(Long id) {
        return (RequestFileGroup) sqlSession().selectOne(NS + ".selectRequestFilesGroup", id);
    }

    public void delete(RequestFileGroup requestFileGroup) {
        if (requestFileGroup.getBenefitFile() != null) {
            requestFileBean.delete(requestFileGroup.getBenefitFile());
        }

        if (requestFileGroup.getPaymentFile() != null) {
            requestFileBean.delete(requestFileGroup.getPaymentFile());
        }

        sqlSession().delete(NS + ".deleteRequestFileGroup", requestFileGroup.getId());
    }

    public void clear(RequestFileGroup requestFileGroup) {
        sqlSession().delete(NS + ".deleteRequestFileGroup", requestFileGroup.getId());
    }

    public void save(RequestFileGroup group) {
        if (group.getId() == null) {
            sqlSession().insert(NS + ".insertRequestFileGroup", group);
        } else {
            sqlSession().update(NS + ".updateRequestFileGroup", group);
        }

        RequestFile paymentFile = group.getPaymentFile();

        if (paymentFile != null && paymentFile.getId() != null){
            paymentFile.setStatus(group.getStatus());

            requestFileBean.save(paymentFile);
        }
    }

    public void clearEmptyGroup() {
        sqlSession().delete(NS + ".clearEmptyGroup");
    }


    public void updateStatus(final long requestFileId, final RequestFileStatus status) {
        sqlSession().update(NS + ".updateStatus", new HashMap<String, Object>() {

            {
                put("fileId", requestFileId);
                put("status", status);
            }
        });
    }


    public long getPaymentFileId(long benefitFileId) {
        return (Long) sqlSession().selectOne(NS + ".getPaymentFileId", benefitFileId);
    }


    public long getBenefitFileId(long paymentFileId) {
        return (Long) sqlSession().selectOne(NS + ".getBenefitFileId", paymentFileId);
    }

    public RequestFileStatus getRequestFileStatus(RequestFileGroup group) {
        return (RequestFileStatus) sqlSession().selectOne(NS + ".selectGroupStatus", group.getId());
    }

    public void fixProcessingOnInit() {
        sqlSession().update(NS + ".fixLoadingOnInit");
        sqlSession().update(NS + ".fixBingingOnInit");
        sqlSession().update(NS + ".fixFillingOnInit");
        sqlSession().update(NS + ".fixSavingOnInit");
    }

    @SuppressWarnings("Duplicates")
    public void fixProcessingOnError(RequestFileGroup group){
        switch (group.getStatus()){
            case BIND_WAIT:
            case BINDING:
                group.setStatus(BIND_ERROR);
                break;
            case FILL_WAIT:
            case FILLING:
                group.setStatus(FILL_ERROR);
                break;
            case SAVE_WAIT:
            case SAVING:
                group.setStatus(SAVE_ERROR);
                break;
            case EXPORT_WAIT:
            case EXPORTING:
                group.setStatus(EXPORT_ERROR);
                break;
        }

        save(group);
    }

    public void updateIfBound(Long groupId){
        RequestFileGroup group = getRequestFileGroup(groupId);

        if (paymentBean.isPaymentFileBound(group.getPaymentFile().getId())
                && benefitBean.isBenefitFileBound(group.getBenefitFile().getId())){
            group.setStatus(RequestFileStatus.BOUND);

            save(group);
        }
    }
}
