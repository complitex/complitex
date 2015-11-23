package org.complitex.template.web.template;


import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.07.2010 16:51:09
 */
public class FormTemplatePage extends TemplatePage {
    public FormTemplatePage() {
        this(Model.of(""));
    }

    public FormTemplatePage(IModel<String> header) {
        add(new Label("header", header));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

//        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(FormTemplatePage.class,
//                FormTemplatePage.class.getSimpleName() + ".js")));
    }
}
