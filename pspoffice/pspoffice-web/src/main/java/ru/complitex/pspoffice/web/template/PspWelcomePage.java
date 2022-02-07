/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.web.template;

import org.apache.wicket.RestartResponseException;
import ru.complitex.pspoffice.person.strategy.web.list.apartment_card.ApartmentCardSearch;
import ru.complitex.template.web.pages.welcome.WelcomePage;
import ru.complitex.template.web.security.SecurityRole;

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
