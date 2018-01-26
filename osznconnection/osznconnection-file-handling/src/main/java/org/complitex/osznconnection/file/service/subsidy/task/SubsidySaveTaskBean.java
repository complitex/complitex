package org.complitex.osznconnection.file.service.subsidy.task;

import org.complitex.common.service.executor.ITaskBean;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.subsidy.Subsidy;
import org.complitex.osznconnection.file.entity.subsidy.SubsidyDBF;
import org.complitex.osznconnection.file.entity.subsidy.SubsidySplitField;
import org.complitex.osznconnection.file.service.process.AbstractSaveTaskBean;
import org.complitex.osznconnection.file.service.process.RequestFileDirectoryType;
import org.complitex.osznconnection.file.service.subsidy.SubsidyBean;
import org.complitex.osznconnection.file.service.subsidy.SubsidySplitBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.util.*;

import static org.complitex.osznconnection.file.entity.RequestStatus.SUBSIDY_RECALCULATED;
import static org.complitex.osznconnection.file.entity.RequestStatus.SUBSIDY_SPLITTED;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class SubsidySaveTaskBean extends AbstractSaveTaskBean implements ITaskBean<RequestFile> {

    @EJB
    private SubsidyBean subsidyBean;

    @EJB
    private SubsidySplitBean subsidySplitBean;

    @Override
    protected List<Subsidy> getAbstractRequests(RequestFile requestFile) {
        List<Subsidy> subsidies =  subsidyBean.getSubsidies(requestFile.getId());

        List<Subsidy> list = new ArrayList<>();

        for (Iterator<Subsidy> iterator = subsidies.iterator(); iterator.hasNext(); ) {
            Subsidy s = iterator.next();

            if (SUBSIDY_SPLITTED.equals(s.getStatus()) || SUBSIDY_RECALCULATED.equals(s.getStatus())) {
                subsidySplitBean.getSubsidySplits(s.getId()).forEach(split -> {
                    Subsidy subsidy = new Subsidy();

                    subsidy.setId(s.getId());

                    subsidy.setDbfFields(new HashMap<>(s.getDbfFields()));

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

    @Override
    protected String getPuAccountFieldName() {
        return SubsidyDBF.RASH.name();
    }

    @Override
    public Class<?> getControllerClass() {
        return SubsidySaveTaskBean.class;
    }

    @Override
    protected RequestFileDirectoryType getSaveDirectoryType() {
        return RequestFileDirectoryType.SAVE_SUBSIDY_DIR;
    }
}
