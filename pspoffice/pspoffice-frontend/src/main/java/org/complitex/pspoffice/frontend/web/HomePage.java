package org.complitex.pspoffice.frontend.web;

import org.complitex.pspoffice.frontend.web.person.PersonListPage;

/**
 * @author Anatoly A. Ivanov
 * 26.06.2017 13:48
 */
public class HomePage extends BasePage{
    public HomePage() {
        setResponsePage(PersonListPage.class);
    }
}
