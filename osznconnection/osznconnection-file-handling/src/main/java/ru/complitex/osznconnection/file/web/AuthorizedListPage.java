package ru.complitex.osznconnection.file.web;

import org.apache.wicket.Page;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.entity.ILongId;
import ru.complitex.common.service.SessionBean;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.service.RequestFileBean;
import ru.complitex.osznconnection.file.web.component.DataRowHoverBehavior;
import ru.complitex.template.web.template.ListTemplatePage;

import javax.ejb.EJB;

/**
 * @author inheaven on 020 20.01.17.
 */
public abstract class AuthorizedListPage<T extends ILongId> extends ListTemplatePage<T>{
    @EJB
    private SessionBean sessionBean;

    @EJB
    private RequestFileBean requestFileBean;

    public AuthorizedListPage(PageParameters pageParameters, String prefix, Class<? extends Page> backPage) {
        super(pageParameters, prefix, backPage);

        add(new DataRowHoverBehavior());
    }

    @Override
    protected void authorize(PageParameters pageParameters) {
        RequestFile requestFile = requestFileBean.getRequestFile(pageParameters.get("request_file_id").toLongObject());

        //Проверка доступа к данным
        if (!sessionBean.isAuthorized(requestFile.getUserOrganizationId())) {
            throw new UnauthorizedInstantiationException(this.getClass());
        }
    }
}
