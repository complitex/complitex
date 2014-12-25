package org.complitex.template.web.component;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.common.entity.StringLocale;
import org.complitex.common.strategy.StringLocaleBean;

import javax.ejb.EJB;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Artem
 */
public class LocalePicker extends Panel {

    @EJB(name = "LocaleBean")
    private StringLocaleBean stringLocaleBean;

    public LocalePicker(String id){
        this(id, null, true);
    }

    public LocalePicker(String id, IModel<Locale> model, final boolean update) {
        super(id);

        List<Locale> locales = Lists.newArrayList(Iterables.transform(stringLocaleBean.getAllLocales(),
                new Function<StringLocale, Locale>() {

                    @Override
                    public Locale apply(StringLocale locale) {
                        return stringLocaleBean.convert(locale);
                    }
                }));

        if (model == null) {
            model = new Model<Locale>() {

                @Override
                public Locale getObject() {
                    return getSession().getLocale();
                }

                @Override
                public void setObject(Locale locale) {
                    getSession().setLocale(locale);
                }
            };
        }

        IChoiceRenderer<Locale> renderer = new ChoiceRenderer<Locale>() {

            @Override
            public Object getDisplayValue(Locale locale) {
                return locale.getDisplayName(getLocale());
            }
        };

        add(new DropDownChoice<Locale>("localeDropDown", model, locales, renderer) {

            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return update;
            }
        });
    }
}
