package ru.complitex.admin.web;

import org.apache.wicket.Component;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.Preference;
import ru.complitex.common.exception.WrongCurrentPasswordException;
import ru.complitex.common.service.SessionBean;
import ru.complitex.common.strategy.StringLocaleBean;
import ru.complitex.common.web.component.ShowMode;
import ru.complitex.common.web.component.back.BackInfo;
import ru.complitex.common.web.component.back.BackInfoManager;
import ru.complitex.common.web.component.search.SearchComponentState;
import ru.complitex.common.web.component.search.WiQuerySearchComponent;
import ru.complitex.template.web.component.IMainUserOrganizationPicker;
import ru.complitex.template.web.component.LocalePicker;
import ru.complitex.template.web.component.MainUserOrganizationPickerFactory;
import ru.complitex.template.web.security.SecurityRole;
import ru.complitex.template.web.template.FormTemplatePage;
import ru.complitex.template.web.template.MenuManager;

import javax.ejb.EJB;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static ru.complitex.common.web.DictionaryFwSession.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 01.07.11 16:40
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class ProfilePage extends FormTemplatePage {

    @EJB
    private StringLocaleBean stringLocaleBean;

    @EJB
    private SessionBean sessionBean;

    public ProfilePage(PageParameters parameters) {
        add(new Label("title", getString("title")));
        add(new FeedbackPanel("messages"));

        //Форма
        Form<Void> form = new Form<Void>("form");
        add(form);

        //Локаль
        String language = getTemplateSession().getPreferenceString(GLOBAL_PAGE, LOCALE_KEY);

        final IModel<Locale> localeModel = new Model<Locale>(language != null ? new Locale(language) : stringLocaleBean.getSystemLocale());
        form.add(new LocalePicker("locale", localeModel, false));

        //Пароль
        final PasswordTextField currentPassword = new PasswordTextField("current_password", new Model<>(""));
        form.add(currentPassword.setRequired(false));

        final PasswordTextField password = new PasswordTextField("password", new Model<>(""));
        form.add(password.setRequired(false));

        final PasswordTextField password2 = new PasswordTextField("password2", new Model<>(""));
        form.add(password2.setRequired(false));

        //Главная организация пользователя
        WebMarkupContainer mainUserOrganizationContainer = new WebMarkupContainer("mainUserOrganizationContainer");
        final IModel<DomainObject> mainUserOrganizationModel = new Model<>(sessionBean.loadMainUserOrganization());
        Component mainUserOrganizationPicker =
                MainUserOrganizationPickerFactory.create("mainUserOrganizationPicker", mainUserOrganizationModel);
        mainUserOrganizationContainer.setVisible(((IMainUserOrganizationPicker) mainUserOrganizationPicker).visible());
        mainUserOrganizationContainer.add(mainUserOrganizationPicker);
        form.add(mainUserOrganizationContainer);

        //Адрес по умолчанию
        final List<String> searchFilters = Arrays.asList("country", "region", "city", "street");

        Preference useDefaultPreference = getTemplateSession().getOrCreatePreference(GLOBAL_PAGE, IS_USE_DEFAULT_STATE_KEY);
        final Model<Boolean> useDefaultModel = new Model<Boolean>(Boolean.valueOf(useDefaultPreference.getValue()));
        form.add(new CheckBox("use_default_address", useDefaultModel));

        final SearchComponentState defaultSearchComponentState = new SearchComponentState();
        for (String s : searchFilters) {
            defaultSearchComponentState.put(s, getTemplateSession().getPreferenceDomainObject(DEFAULT_STATE_PAGE, s));
        }

        form.add(new WiQuerySearchComponent("searchComponent", defaultSearchComponentState, searchFilters, null, ShowMode.ALL, true));

        //Сохранение
        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                if (password.getModelObject() != null && !password.getModelObject().isEmpty()) {
                    //Пароль
                    if (!password.getModelObject().equals(password2.getModelObject())) {
                        error(getString("error_passwords_not_match"));

                        return;
                    }

                    if (currentPassword.getModelObject() != null) {
                        try {
                            sessionBean.updatePassword(currentPassword.getModelObject(), password.getModelObject());
                        } catch (WrongCurrentPasswordException e) {
                            error(getString("error_wrong_current_password"));
                        }
                    } else {
                        error(getString("error_wrong_current_password"));
                    }
                }

                //Локаль
                getSession().setLocale(localeModel.getObject());

                //Главная организация пользователя
                final DomainObject mainUserOrganization = mainUserOrganizationModel.getObject();
                if (mainUserOrganization != null && mainUserOrganization.getObjectId() != null) {
                    sessionBean.updateMainUserOrganization(getTemplateSession(), mainUserOrganization);
                }

                //Адрес по умолчанию
                for (String s : searchFilters) {
                    DomainObject domainObject = defaultSearchComponentState.get(s);

                    if (domainObject != null) {
                        getTemplateSession().putPreference(DEFAULT_STATE_PAGE, s, String.valueOf(domainObject.getObjectId()), true);
                    }

                    //update session search component state to the default state:
                    if (!defaultSearchComponentState.isEmptyState() && useDefaultModel.getObject()) {
                        SearchComponentState sessionSearchComponentState = getTemplateSession().getGlobalSearchComponentState();
                        sessionSearchComponentState.updateState(defaultSearchComponentState);
                        getTemplateSession().storeGlobalSearchComponentState();
                    }
                }

                //Использовать ли адрес по умолчанию при входе в систему
                getTemplateSession().putPreference(GLOBAL_PAGE, IS_USE_DEFAULT_STATE_KEY, useDefaultModel.getObject().toString(), true);

                info(getString("info_saved"));
            }
        });

        final String backInfoSessionKey = parameters.get(BACK_INFO_SESSION_KEY).toString();
        form.add(new Button("cancel") {

            @Override
            public void onSubmit() {
                if (!Strings.isEmpty(backInfoSessionKey)) {
                    final BackInfo backInfo = BackInfoManager.get(this, backInfoSessionKey);
                    if (backInfo != null) {
                        backInfo.back(this);
                        return;
                    }
                }
                setResponsePage(getApplication().getHomePage());
            }
        });

        MenuManager.removeMenuItem();
    }
}
