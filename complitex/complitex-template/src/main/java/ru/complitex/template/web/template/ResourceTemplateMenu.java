package ru.complitex.template.web.template;

import com.google.common.base.CaseFormat;
import org.apache.wicket.Page;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.strategy.StrategyFactory;
import ru.complitex.common.util.EjbBeanLocator;
import ru.complitex.template.web.pages.EntityDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.01.2010 12:14:04
 */
public abstract class ResourceTemplateMenu implements ITemplateMenu {
    private Logger log(){
        return LoggerFactory.getLogger(ResourceTemplateMenu.class);
    }

    private List<ITemplateLink> templateLinks = new ArrayList<>();

     /**
     * Используется ResourceBundle для локализации
     * @param locale текущая локаль
     * @return ResourceBundle
     */
    private ResourceBundle getResourceBundle(String baseName, Locale locale){
        try {
            return ResourceBundle.getBundle(baseName, locale);
        } catch (Exception e) {
            log().error("Ресурс файла локализации не найден", e);
        }
        return null;
    }

    protected String getString(String baseName, Locale locale, String key){
        try {
            return getResourceBundle(baseName, locale).getString(key);
        } catch (Exception e) {
            log().error("Не найдено значение в файле локализации", e);
        }
        return "[NO LOCALE]";
    }

    protected String getString(Class base, Locale locale, String key){
        return getString(base.getName(), locale, key);
    }

    protected String getString(String key, Locale locale){
        return getString(getClass().getName(), locale, key);
    }

    @Override
    public String getTagId() {
        return getClass().getSimpleName();
    }

    @Override
    public String getTitle(Locale locale) {
        return getString("title", locale);
    }

    @Override
    public List<ITemplateLink> getTemplateLinks(Locale locale) {
        return templateLinks;
    }

    protected void add(Class<? extends Page> page){
        String key = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, page.getSimpleName());

        templateLinks.add(new ResourceTemplateLink(key, this, page));
    }

    protected void add(Class<? extends Page> page, PageParameters pageParameters){
        String key = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, page.getSimpleName());

        templateLinks.add(new ResourceTemplateLink(key, this, page, pageParameters, null));
    }

    protected void add(String key, Class<? extends Page> page){
        templateLinks.add(new ResourceTemplateLink(key, this, page));
    }

    protected void add(String key, Class<? extends Page> page, String[] roles){
        templateLinks.add(new ResourceTemplateLink(key, this, page, roles));
    }

    protected void add(String key, Class<? extends Page> page, PageParameters pageParameters){
        templateLinks.add(new ResourceTemplateLink(key, this, page, pageParameters, null));
    }

    protected void add(String key, Class<? extends Page> page, PageParameters pageParameters, String[] roles){
        templateLinks.add(new ResourceTemplateLink(key, this, page, pageParameters, null, roles));
    }

    protected void addDictionary(String key){
        IStrategy serviceStrategy = EjbBeanLocator.getBean(StrategyFactory.class).getStrategy(key);
        add(key, serviceStrategy.getListPage(), serviceStrategy.getListPageParams());
    }

    protected void addDescription(String key){
        add(key, EntityDescription.class, new PageParameters().add(EntityDescription.ENTITY, key));
    }
    
    protected final void add(ITemplateLink menuItemLink){
        templateLinks.add(menuItemLink);
    }
}
