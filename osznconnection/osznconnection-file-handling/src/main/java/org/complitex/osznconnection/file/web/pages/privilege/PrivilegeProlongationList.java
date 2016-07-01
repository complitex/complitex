package org.complitex.osznconnection.file.web.pages.privilege;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.TemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author inheaven on 29.06.16.
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class PrivilegeProlongationList extends TemplatePage{
    private Logger log = LoggerFactory.getLogger(PrivilegeProlongationList.class);


}
