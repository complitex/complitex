package org.complitex.osznconnection.file.web;

import org.apache.wicket.Page;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.entity.ILongId;
import org.complitex.common.service.SessionBean;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.web.component.DataRowHoverBehavior;
import org.complitex.template.web.template.ListTemplatePage;

import javax.ejb.EJB;

/**
 * @author inheaven on 020 20.01.17.
 */
public abstract class AuthorizedListPage<T extends ILongId>  extends ListTemplatePage<T>{
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
