package ru.complitex.eirc.mb_transformer.web.admin;

import ru.complitex.template.web.template.TemplatePage;

/**
 * @author Pavel Sknar
 */
public class UserList extends ru.complitex.admin.web.UserList {
    @Override
    protected Class<? extends TemplatePage> getEditPageClass() {
        return UserEdit.class;
    }
}
