package ru.complitex.template.web;

import org.apache.wicket.markup.html.WebPage;
import ru.complitex.common.service.BroadcastService;
import ru.complitex.common.util.EjbBeanLocator;
import ru.complitex.template.web.pages.HomePageFactory;
import ru.complitex.template.web.template.TemplateWebApplication;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 20.12.2009 23:56:14
 */
public class ComplitexWebApplication extends TemplateWebApplication {

    @Override
    protected void init() {
        super.init();

        //broadcaster
        EjbBeanLocator.getBean(BroadcastService.class).setApplication(this);

        //bootstrap
//        BootstrapSettings settings = new BootstrapSettings();
//        settings.setThemeProvider(new SingleThemeProvider(new ComplitexTheme()));
//        Bootstrap.install(this, settings);
    }

    @Override
    public Class<? extends WebPage> getHomePage() {
        return (Class) getWebComponentResolver().getComponentClass(HomePageFactory.WEB_COMPONENT_NAME);
    }
}
