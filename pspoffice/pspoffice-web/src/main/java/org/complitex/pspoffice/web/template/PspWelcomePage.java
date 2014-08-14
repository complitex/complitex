/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.web.template;

import org.apache.wicket.RestartResponseException;
import org.complitex.pspoffice.person.strategy.web.list.apartment_card.ApartmentCardSearch;
import org.complitex.template.web.pages.welcome.WelcomePage;
import org.complitex.template.web.security.SecurityRole;

/**
 *
 * @author Artem
 */
public final class PspWelcomePage extends WelcomePage {

    public PspWelcomePage() {
        if (!hasAnyRole(SecurityRole.INFO_PANEL_ALLOWED)) {
            throw new RestartResponseException(ApartmentCardSearch.class);
        }
    }
}
