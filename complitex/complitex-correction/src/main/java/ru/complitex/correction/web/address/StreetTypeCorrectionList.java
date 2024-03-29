package ru.complitex.correction.web.address;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.address.strategy.street_type.StreetTypeStrategy;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.service.SessionBean;
import ru.complitex.correction.entity.Correction;
import ru.complitex.correction.service.CorrectionBean;
import ru.complitex.correction.web.AbstractCorrectionList;
import ru.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import java.util.List;
import java.util.Locale;

@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class StreetTypeCorrectionList extends AbstractCorrectionList{
    @EJB
    private SessionBean sessionBean;

    @EJB
    private CorrectionBean correctionBean;

    @EJB
    private StreetTypeStrategy streetTypeStrategy;

    public StreetTypeCorrectionList() {
        super("street_type");
    }


    @Override
    protected List<Correction> getCorrections(FilterWrapper<Correction> filterWrapper) {
        sessionBean.authorize(filterWrapper);

        List<Correction> streetTypeCorrections = correctionBean.getCorrections(filterWrapper);

        Locale locale = getLocale();

        if (streetTypeCorrections != null && !streetTypeCorrections.isEmpty()) {
            for (Correction streetTypeCorrection : streetTypeCorrections) {
                DomainObject streetTypeObject = streetTypeStrategy.getDomainObject(streetTypeCorrection.getObjectId(), false);
                if (streetTypeObject == null) {
                    streetTypeObject = streetTypeStrategy.getDomainObject(streetTypeCorrection.getObjectId(), true);
                    streetTypeCorrection.setEditable(false);
                }
                streetTypeCorrection.setDisplayObject(streetTypeStrategy.displayFullName(streetTypeObject, locale));
            }
        }
        return streetTypeCorrections;
    }

    @Override
    protected Class<? extends WebPage> getEditPage() {
        return StreetTypeCorrectionEdit.class;
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new StringResourceModel("title", this, null);
    }

    @Override
    protected PageParameters getEditPageParams(Long objectCorrectionId) {
        PageParameters parameters = new PageParameters();
        if (objectCorrectionId != null) {
            parameters.set(StreetTypeCorrectionEdit.CORRECTION_ID, objectCorrectionId);
        }
        return parameters;
    }
}
