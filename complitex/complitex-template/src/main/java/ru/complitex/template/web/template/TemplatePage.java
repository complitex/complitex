package ru.complitex.template.web.template;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.string.Strings;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.PreferenceKey;
import ru.complitex.common.service.SessionBean;
import ru.complitex.common.util.ResourceUtil;
import ru.complitex.resources.WebCommonResourceInitializer;
import ru.complitex.template.web.component.MainUserOrganizationPickerFactory;
import ru.complitex.template.web.component.toolbar.ToolbarButton;
import ru.complitex.template.web.pages.login.Login;
import ru.complitex.template.web.security.SecurityRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 22.07.2010 16:09:45
 *
 * Суперкласс шаблон для отображения содержания страниц.
 * Для инициализации шаблона наследники должны вызывать метод super().
 */
public abstract class TemplatePage extends WebPage {
    public static final String BACK_INFO_SESSION_KEY = "back_info_session_key";

    @EJB
    private SessionBean sessionBean;
    private String page = getClass().getName();
    private Set<String> resourceBundle = new HashSet<String>();

    protected Logger log(){
        return LoggerFactory.getLogger(getClass());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(WebCommonResourceInitializer.COMMON_JS));
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(TemplatePage.class,
                TemplatePage.class.getSimpleName() + ".js")));

//        response.render(JavaScriptHeaderItem.forReference(CoreUIJavaScriptResourceReference.get()));
//        response.render(JavaScriptHeaderItem.forReference(WidgetJavaScriptResourceReference.get()));

        response.render(CssHeaderItem.forReference(WebCommonResourceInitializer.STYLE_CSS));
    }

    protected TemplatePage() {
        add(new Link<Void>("home") {

            @Override
            public void onClick() {
                setResponsePage(getApplication().getHomePage());
            }
        });

//        final boolean infoPanelAllowed = hasAnyRole(SecurityRole.INFO_PANEL_ALLOWED);
//        if (!infoPanelAllowed) {
//            MenuManager.hideMainMenu();
//        }
        WebMarkupContainer infoPanelButton = new WebMarkupContainer("infoPanelButton");
//        infoPanelButton.setVisibilityAllowed(infoPanelAllowed);
        add(infoPanelButton);

        WebMarkupContainer infoPanel = new WebMarkupContainer("infoPanel");
//        infoPanel.setVisibilityAllowed(infoPanelAllowed);
        add(infoPanel);

        //menu
        infoPanel.add(new ListView<ITemplateMenu>("sidebar", newTemplateMenus()) {

            @Override
            protected void populateItem(ListItem<ITemplateMenu> item) {
                item.add(new TemplateMenu("menu_placeholder", "menu", this, item.getModelObject()));
            }
        });

        boolean isUserAuthorized = isUserAuthorized();
        //full user name.
        final String fullName = isUserAuthorized ? sessionBean.getCurrentUserFullName(getLocale()) : null;
        add(new Label("current_user_fullname", fullName));

        //main user's organization.
        IModel<DomainObject> mainUserOrganizationModel = new Model<DomainObject>() {

            @Override
            public void setObject(DomainObject mainUserOrganization) {
                getTemplateSession().setMainUserOrganization(mainUserOrganization);
            }

            @Override
            public DomainObject getObject() {
                return sessionBean.getMainUserOrganization(getTemplateSession());
            }
        };
        
        //main user organization picker component
        add(MainUserOrganizationPickerFactory.create("mainUserOrganizationPicker", mainUserOrganizationModel));

        add(new Form<Void>("exit") {

            @Override
            public void onSubmit() {
                getSession().invalidate();
                setResponsePage(Login.class);
            }
        }.setVisible(isUserAuthorized));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onInitialize() {
        super.onInitialize();

        //add user profile page link.
        if (isUserAuthorized()) {
            final String profilePageClassName = "ru.complitex.admin.web.ProfilePage";
            try {
                final Class<? extends WebPage> profilePageClass =
                        (Class<? extends WebPage>) Thread.currentThread().getContextClassLoader().loadClass(profilePageClassName);

                add(new Link<Void>("profile") {

                    @Override
                    public void onClick() {
                        onProfileClick(profilePageClass);
                    }
                });
            } catch (ClassNotFoundException e) {
                log().warn("Profile page class was not found: " + profilePageClassName, e);
                add(new EmptyPanel("profile"));
            }
        } else {
            add(new EmptyPanel("profile"));
        }

        //toolbar
        WebMarkupContainer toolbar = new WebMarkupContainer("toolbar");
        add(toolbar);
        //common toolbar buttons.
        ListView<ToolbarButton> commonPart = new ListView<ToolbarButton>("commonPart", getCommonButtons("commonButton")) {

            @Override
            protected void populateItem(ListItem<ToolbarButton> item) {
                item.add(item.getModelObject());
            }
        };
        toolbar.add(commonPart);

        //application-wide toolbar buttons.
        List<ToolbarButton> applicationButtons = getTemplateWebApplication().
                getApplicationToolbarButtons("applicationButton");
        if (applicationButtons == null) {
            applicationButtons = Collections.emptyList();
        }
        ListView<ToolbarButton> applicationPart = new ListView<ToolbarButton>("applicationPart", applicationButtons) {

            @Override
            protected void populateItem(ListItem<ToolbarButton> item) {
                item.add(item.getModelObject());
            }
        };
        toolbar.add(applicationPart);

        //page-wide toolbar buttons.
        List<ToolbarButton> pageButtons = getToolbarButtons("pageButton");
        if (pageButtons == null) {
            pageButtons = Collections.emptyList();
        }
        ListView<ToolbarButton> pagePart = new ListView<ToolbarButton>("pagePart", pageButtons) {

            @Override
            protected void populateItem(ListItem<ToolbarButton> item) {
                item.add(item.getModelObject());
            }
        };
        toolbar.add(pagePart);
    }

    private List<ToolbarButton> getCommonButtons(String id) {
        return new ArrayList<>();
    }

    /**
     * Боковая панель с меню, которое устанавливается в конфигурационном файле.
     */
    private class TemplateMenu extends Fragment {

        private TemplateMenu(String id, String markupId, MarkupContainer markupProvider, ITemplateMenu menu) {
            super(id, markupId, markupProvider);

            if (!Strings.isEmpty(menu.getTagId())) {
                setMarkupId(menu.getTagId());
            }

            add(new Label("menu_title", menu.getTitle(getLocale())));

            List<ITemplateLink> templateLinks = menu.getTemplateLinks(getLocale()).stream()
                    .filter(l -> l.getRoles() == null || sessionBean.hasAnyUserGroup(l.getRoles()))
                    .collect(Collectors.toList());

            add(new ListView<ITemplateLink>("menu_items", templateLinks) {

                @Override
                protected void populateItem(ListItem<ITemplateLink> item) {
                    final ITemplateLink templateLink = item.getModelObject();
                    BookmarkablePageLink link = new BookmarkablePageLink<Class<? extends Page>>("link", templateLink.getPage(),
                            templateLink.getParameters());
                    if (!Strings.isEmpty(templateLink.getTagId())) {
                        link.setMarkupId(templateLink.getTagId());
                    }

                    link.add(new Label("label", templateLink.getLabel(getLocale())));
                    item.add(link);
                }
            });
        }
    }

    private List<ITemplateMenu> newTemplateMenus() {
        List<ITemplateMenu> templateMenus = new ArrayList<>();
        for (Class<ITemplateMenu> menuClass : getTemplateWebApplication().getMenuClasses()) {
            if (isTemplateMenuAuthorized(menuClass)) {
                try {
                    ITemplateMenu templateMenu = menuClass.newInstance();
                    templateMenus.add(templateMenu);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return templateMenus;
    }

    /**
     * Проверка роли пользователя для отображения меню модуля.
     * @param menuClass Класс меню.
     * @return Отображать ли меню пользователю в зависимости от его роли.
     */
    private boolean isTemplateMenuAuthorized(Class<?> menuClass) {
        boolean authorized = true;

        final AuthorizeInstantiation classAnnotation = menuClass.getAnnotation(AuthorizeInstantiation.class);
        if (classAnnotation != null) {
            authorized = getTemplateWebApplication().hasAnyRole(classAnnotation.value());
        }

        return authorized;
    }

    /**
     * Subclass can override method in order to specify custom page toolbar buttons.
     * @param id Component id
     * @return List of ToolbarButton to add to Template
     */
    protected List<ToolbarButton> getToolbarButtons(String id) {
        return null;
    }

    protected boolean hasAnyRole(String... roles) {
        return getTemplateWebApplication().hasAnyRole(roles);
    }

    protected TemplateWebApplication getTemplateWebApplication() {
        return (TemplateWebApplication) getApplication();
    }

    protected TemplateSession getTemplateSession() {
        return (TemplateSession) getSession();
    }

    protected String getStringOrKey(String key) {
        if (key == null) {
            return "";
        }

        try {
            return getString(key);
        } catch (MissingResourceException e) {
            //resource is not found
        }

        for (String bundle : resourceBundle) {
            try {
                return ResourceUtil.getResourceBundle(bundle, getLocale()).getString(key);
            } catch (MissingResourceException e) {
                //resource is not found
            }
        }

        return key;
    }

    protected String getStringOrKey(Enum<?> key) {
        return key != null ? getStringOrKey(key.name()) : "";
    }

    protected String getStringFormat(String key, Object... args) {
        try {
            return MessageFormat.format(getString(key), args);
        } catch (Exception e) {
            log().error("Ошибка форматирования файла свойств", e);
            return key;
        }
    }

    protected void addResourceBundle(String bundle) {
        resourceBundle.add(bundle);
    }

    protected void addAllResourceBundle(Collection<String> bundle) {
        resourceBundle.addAll(bundle);
    }

    /* Template Session Preferences */
    public String getPreferencesPage() {
        return page;
    }

    public void setPreferencesPage(String page) {
        this.page = page;
    }

    public void setFilterObject(Object filterObject) {
        getTemplateSession().putPreferenceObject(page, PreferenceKey.FILTER_OBJECT, filterObject);
    }

    public <T> T getFilterObject(T _default) {
        return getTemplateSession().getPreferenceObject(page, PreferenceKey.FILTER_OBJECT, _default);
    }

    public <T extends Serializable> IModel<FilterWrapper<T>> newFilterModel(T _default){
        return Model.of(getFilterObject(FilterWrapper.of(_default)));
    }

    public final boolean isUserAuthorized() {
        return getTemplateWebApplication().hasAnyRole(SecurityRole.AUTHORIZED);
    }

    protected void onProfileClick(Class<? extends WebPage> profilePageClass) {
        setResponsePage(profilePageClass, null);
    }
}