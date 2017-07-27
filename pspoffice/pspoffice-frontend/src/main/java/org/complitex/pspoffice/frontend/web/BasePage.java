package org.complitex.pspoffice.frontend.web;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * @author Anatoly A. Ivanov
 * 26.06.2017 13:49
 */
public abstract class BasePage extends WebPage{
    protected BasePage() {
        add(new Label("title", getTitleModel()));
    }

    protected IModel<String> getTitleModel(){
        return Model.of("");
    }
}
