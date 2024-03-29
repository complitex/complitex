package ru.complitex.admin.web;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.admin.Module;
import ru.complitex.admin.service.UserBean;
import ru.complitex.admin.strategy.UserInfoStrategy;
import ru.complitex.common.entity.*;
import ru.complitex.common.entity.*;
import ru.complitex.common.service.LogBean;
import ru.complitex.common.service.PreferenceBean;
import ru.complitex.common.strategy.StringLocaleBean;
import ru.complitex.common.util.CloneUtil;
import ru.complitex.common.web.component.DomainObjectInputPanel;
import ru.complitex.common.web.component.ShowMode;
import ru.complitex.common.web.component.organization.OrganizationIdPicker;
import ru.complitex.common.web.component.search.SearchComponentState;
import ru.complitex.common.web.component.search.WiQuerySearchComponent;
import ru.complitex.template.web.component.LocalePicker;
import ru.complitex.template.web.security.SecurityRole;
import ru.complitex.template.web.template.FormTemplatePage;
import ru.complitex.template.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.*;

import static ru.complitex.common.entity.UserGroup.GROUP_NAME.*;
import static ru.complitex.common.web.DictionaryFwSession.*;
import static ru.complitex.organization_type.strategy.OrganizationTypeStrategy.USER_ORGANIZATION_TYPE;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 31.07.2010 14:12:33
 *
 *  Страница создания и редактирования пользователя
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class UserEdit extends FormTemplatePage {
    private static final List<String> SEARCH_FILTERS = Arrays.asList("country", "region", "city", "street");

    @EJB
    private UserBean userBean;

    @EJB
    private UserInfoStrategy userInfoStrategy;

    @EJB
    private LogBean logBean;

    @EJB
    private PreferenceBean preferenceBean;

    @EJB
    private StringLocaleBean stringLocaleBean;

    public UserEdit() {
        super();
        init(null, false);
    }

    public UserEdit(PageParameters parameters) {
        super();
        init(parameters.get("user_id").toOptionalLong(), "copy".equals(parameters.get("action").toString()));
    }

    @SuppressWarnings("Duplicates")
    private void init(Long userId, boolean copyUser) {
        add(new Label("title", new ResourceModel("title")));
        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        //Модель данных
        User user = userId != null ? userBean.getUser(userId) : userBean.newUser();
        final IModel<User> userModel = new Model<>(user);

        final SearchComponentState searchComponentState = new SearchComponentState();
        Locale userLocale = null;
        if (userId != null) {
            // Адрес
            for (String s : SEARCH_FILTERS) {
                searchComponentState.put(s, preferenceBean.getPreferenceDomainObject(userId, DEFAULT_STATE_PAGE, s));
            }

            // Локаль
            Preference localePreference = preferenceBean.getPreference(userId, GLOBAL_PAGE, LOCALE_KEY);
            userLocale = localePreference != null ? new Locale(localePreference.getValue()) : null;
        }
        final IModel<Locale> localeModel = new Model<>(userLocale != null ? userLocale : stringLocaleBean.getSystemLocale());

        Boolean copyUseDefaultAddressFlag = null;
        //Копирование
        if (copyUser) {
            // запомнить флаг установки адреса в поисковой строке прежде чем обнулить user id
            final Preference useDefaultAddressPreference = preferenceBean.getPreference(userId, GLOBAL_PAGE, IS_USE_DEFAULT_STATE_KEY);
            if (useDefaultAddressPreference != null) {
                copyUseDefaultAddressFlag = Boolean.valueOf(useDefaultAddressPreference.getValue());
            }

            userId = null;
            user.setId(null);
            user.setUserInfoObjectId(null);

            // заменить весь объект UserInfo
            user.setUserInfo(userInfoStrategy.newInstance());

            // очистить логин
            user.setLogin(null);

            for (UserOrganization userOrganization : user.getUserOrganizations()) {
                userOrganization.setId(null);
                userOrganization.setUserId(null);
            }

            for (UserGroup userGroup : user.getUserGroups()) {
                userGroup.setId(null);
                userGroup.setLogin(null);
            }
        }

        final User oldUser = (userId != null) ? CloneUtil.cloneObject(userModel.getObject()) : null;

        //Форма
        Form<User> form = new Form<>("form");
        add(form);

        //Логин
        RequiredTextField<String> login = new RequiredTextField<>("login", new PropertyModel<String>(userModel, "login"));
        login.setEnabled(userId == null);
        form.add(login);

        //Пароль
        PasswordTextField password = new PasswordTextField("password", new PropertyModel<>(userModel, "newPassword"));
        password.setEnabled(userId != null);
        password.setRequired(false);
        form.add(password);

        //Информация о пользователе
        DomainObjectInputPanel userInfo = new DomainObjectInputPanel("user_info", userModel.getObject().getUserInfo(),
                "user_info", "UserInfoStrategy", null, null);
        form.add(userInfo);

        //Локаль
        form.add(new LocalePicker("locale", localeModel, false));

        //Группы привилегий
        CheckGroup<UserGroup> usergroups = new CheckGroup<>("usergroups",
                new PropertyModel<Collection<UserGroup>>(userModel, "userGroups"));

        usergroups.add(new Check<>("ADMINISTRATORS", getUserGroup(userModel.getObject(), ADMINISTRATORS.name())));
        usergroups.add(new Check<>("EMPLOYEES", getUserGroup(userModel.getObject(), EMPLOYEES.name())));
        usergroups.add(new Check<>("EMPLOYEES_CHILD_VIEW", getUserGroup(userModel.getObject(), EMPLOYEES_CHILD_VIEW.name())));

        //Дополнительные группы привилегий
        usergroups.add(new ListView<String>("customUserGroups", getTemplateWebApplication().getTemplateLoader().getGroupNames()) {
            @Override
            protected void populateItem(ListItem<String> item) {
                item.add(new Check<>("userGroupName", getUserGroup(userModel.getObject(), item.getModel().getObject())));
                item.add(new Label("userGroupLabel", new ResourceModel(item.getModel().getObject())));
            }
        });

        form.add(usergroups);

        //Организация
        final WebMarkupContainer organizationContainer = new WebMarkupContainer("organizationContainer");
        organizationContainer.setOutputMarkupId(true);
        form.add(organizationContainer);

        final RadioGroup<Integer> organizationGroup = new RadioGroup<>("organizationGroup", new Model<Integer>() {

            @Override
            public void setObject(Integer index) {
                if (index != null && index >= 0 && index < userModel.getObject().getUserOrganizations().size()) {
                    for (UserOrganization uo : userModel.getObject().getUserOrganizations()) {
                        uo.setMain(false);
                    }
                    userModel.getObject().getUserOrganizations().get(index).setMain(true);
                }
            }

            @Override
            public Integer getObject() {
                for (int i = 0; i < userModel.getObject().getUserOrganizations().size(); i++) {
                    if (userModel.getObject().getUserOrganizations().get(i).isMain()) {
                        return i;
                    }
                }
                return 0;
            }
        });
        organizationGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });
        organizationContainer.add(organizationGroup);

        organizationGroup.add(new ListView<UserOrganization>("userOrganizations",
                new PropertyModel<>(userModel, "userOrganizations")) {

            {
                setReuseItems(true);
            }

            @Override
            protected void populateItem(final ListItem<UserOrganization> item) {
                final UserOrganization userOrganization = item.getModelObject();
                final ListView listView = this;

                item.add(new Radio<>("radio", new Model<>(item.getIndex())));
                item.add(new OrganizationIdPicker("organizationObjectId",
                        new PropertyModel<>(userOrganization, "organizationObjectId"), USER_ORGANIZATION_TYPE));
                item.add(new AjaxLink("delete") {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        userModel.getObject().getUserOrganizations().remove(item.getIndex());
                        listView.removeAll();
                        target.add(organizationContainer);
                        target.add(messages);
                    }
                });
            }
        });

        //Добавить организацию
        form.add(new AjaxLink<Void>("addOrganization") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                userModel.getObject().getUserOrganizations().add(new UserOrganization());
                target.add(organizationContainer);
                target.add(messages);
            }
        });

        //Адрес по умолчанию
        final WebMarkupContainer addressContainer = new WebMarkupContainer("addressContainer");
        form.add(addressContainer);
        addressContainer.setVisible(isDefaultAddressVisible());

        Boolean useDefaultAddressFlag;
        if (!copyUser) {
            Preference useDefaultAddressPreference = preferenceBean.getOrCreatePreference(userId, GLOBAL_PAGE, IS_USE_DEFAULT_STATE_KEY);
            useDefaultAddressFlag = Boolean.valueOf(useDefaultAddressPreference.getValue());
        } else {
            useDefaultAddressFlag = copyUseDefaultAddressFlag;
        }
        final Model<Boolean> useDefaultAddressModel = new Model<>(useDefaultAddressFlag);
        addressContainer.add(new CheckBox("use_default_address", useDefaultAddressModel));

        addressContainer.add(isDefaultAddressVisible() ?
                        new WiQuerySearchComponent("searchComponent", searchComponentState, SEARCH_FILTERS, null, ShowMode.ACTIVE, true) :
                        new Label("searchComponent", "default_address")
        );

        //Сохранить
        IndicatingAjaxButton save = new IndicatingAjaxButton("save") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                User user = userModel.getObject();

                try {
                    //Валидация
                    if (UserEdit.this.validate(user)) {

                        //Сохранение пользователя
                        userBean.save(user, getTemplateSession());

                        //Локаль
                        preferenceBean.save(user.getId(), GLOBAL_PAGE, LOCALE_KEY, localeModel.getObject().getLanguage());

                        //Адрес по умолчанию
                        for (String s : SEARCH_FILTERS) {
                            DomainObject domainObject = searchComponentState.get(s);
                            if (domainObject != null) {
                                preferenceBean.save(user.getId(), DEFAULT_STATE_PAGE, s, domainObject.getObjectId() + "");
                            }
                        }

                        //Использовать ли адрес по умолчанию при входе в систему
                        preferenceBean.save(user.getId(), GLOBAL_PAGE, IS_USE_DEFAULT_STATE_KEY, useDefaultAddressModel.getObject().toString());

                        logBean.info(Module.NAME, UserEdit.class, User.class, null, user.getId(),
                                (user.getId() == null) ? Log.EVENT.CREATE : Log.EVENT.EDIT, getLogChanges(oldUser, user), null);

                        log().info("Пользователь сохранен: {}", user);
                        getSession().info(getString("info.saved"));
                        back(user.getId());
                    } else {
                        target.add(messages);
                    }
                } catch (Exception e) {
                    log().error("Ошибка сохранения пользователя", e);
                    error(getString("error.saved"));
                    target.add(messages);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(messages);
            }
        };
        form.add(save);

        //Отмена
        AjaxLink cancel = new AjaxLink("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                back(userModel.getObject().getId());
            }
        };
        form.add(cancel);
    }

    private boolean validate(User user) {
        boolean valid = true;

        //Уникальность логина
        if (user.getId() == null && !userBean.isUniqueLogin(user.getLogin())) {
            error(getString("error.login_not_unique"));
            valid = false;
        }

        //Невыбранные организации
        if (!user.getUserOrganizations().isEmpty()) {
            for (UserOrganization userOrganization : user.getUserOrganizations()) {
                if (userOrganization.getOrganizationObjectId() == null) {
                    error(getString("error.unselected_organization"));
                    valid = false;
                }
            }
        }

        //Пользователю, не являющемуся администратором, не назначено ни одной организации
        if (user.getUserOrganizations().isEmpty() && user.getUserGroups() != null) {
            boolean isAdmin = false;
            for (UserGroup ug : user.getUserGroups()) {
                if (ug.getGroupName().equals(UserGroup.GROUP_NAME.ADMINISTRATORS.name())) {
                    isAdmin = true;
                    break;
                }
            }
            if (!isAdmin) {
                error(getString("error.organization_required"));
                valid = false;
            }
        }
        return valid;
    }

    private IModel<UserGroup> getUserGroup(User user, String groupName) {
        for (UserGroup userGroup : user.getUserGroups()) {
            if (userGroup.getGroupName().equals(groupName)) {
                return new Model<>(userGroup);
            }
        }

        return new Model<>(new UserGroup(groupName));
    }

    @SuppressWarnings("Duplicates")
    private List<LogChange> getLogChanges(User oldUser, User newUser) {
        List<LogChange> logChanges = new ArrayList<>();

        //логин
        if (newUser.getId() == null) {
            logChanges.add(new LogChange(getString("login"), null, newUser.getLogin()));
        }

        //пароль
        if (newUser.getNewPassword() != null) {
            logChanges.add(new LogChange(getString("password"), oldUser.getPassword(),
                    DigestUtils.sha256Hex(newUser.getNewPassword())));
        }

        //информация о пользователе
        List<LogChange> userInfoLogChanges = logBean.getLogChanges(userInfoStrategy,
                oldUser != null ? oldUser.getUserInfo() : null, newUser.getUserInfo());

        logChanges.addAll(userInfoLogChanges);

        //группы привилегий
        if (oldUser == null) {
            for (UserGroup ng : newUser.getUserGroups()) {
                logChanges.add(new LogChange(getString("usergroup"), null, getString(ng.getGroupName())));
            }
        } else {
            for (UserGroup og : oldUser.getUserGroups()) { //deleted group
                boolean deleted = true;

                for (UserGroup ng : newUser.getUserGroups()) {
                    if (ng.getGroupName().equals(og.getGroupName())) {
                        deleted = false;
                        break;
                    }
                }

                if (deleted) {
                    logChanges.add(new LogChange(getString("usergroup"), getString(og.getGroupName()), null));
                }
            }

            for (UserGroup ng : newUser.getUserGroups()) { //added group
                boolean added = true;

                for (UserGroup og : oldUser.getUserGroups()) {
                    if (og.getGroupName().equals(ng.getGroupName())) {
                        added = false;
                        break;
                    }
                }

                if (added) {
                    logChanges.add(new LogChange(getString("usergroup"), null, getString(ng.getGroupName())));
                }
            }
        }

        return logChanges;
    }

    private void back(Long userId) {
        if (userId != null) {
            PageParameters params = new PageParameters();
            //params.set(UserList.SCROLL_PARAMETER, userId);
            setResponsePage(getPageListClass(), params);
        } else {
            setResponsePage(getPageListClass());
        }
    }

    protected Class<? extends TemplatePage> getPageListClass() {
        return UserList.class;
    }

    protected boolean isDefaultAddressVisible(){
        return true;
    }
}
