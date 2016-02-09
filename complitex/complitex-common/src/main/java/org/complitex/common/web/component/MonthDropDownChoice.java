package org.complitex.common.web.component;

import com.google.common.base.Strings;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 25.08.2010 16:22:44
 */
public class MonthDropDownChoice extends DropDownChoice<Integer> {

    private final String[] MONTHS = DateFormatSymbols.getInstance(getLocale()).getMonths();

    public MonthDropDownChoice(String id) {
        super(id, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
        setChoiceRenderer(new IChoiceRenderer<Integer>() {

            @Override
            public Object getDisplayValue(Integer object) {
                return MONTHS[object - 1];
            }

            @Override
            public String getIdValue(Integer object, int index) {
                return object.toString();
            }

            @Override
            public Integer getObject(String id, IModel<? extends List<? extends Integer>> choices) {
                return Strings.isNullOrEmpty(id) ? null : Integer.valueOf(id);
            }
        });
    }

    public MonthDropDownChoice(String id, IModel<Integer> model) {
        this(id);
        setModel(model);
    }
}
