package ru.complitex.common.web.component;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.util.Arrays;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 21.09.12 15:07
 */
public class EnumDropDownChoice<T extends Enum<T>> extends DropDownChoice<T> {
    public EnumDropDownChoice(String id, Class<T> enumClass, boolean nullValid) {
        super(id, Arrays.asList(enumClass.getEnumConstants()));

        init();

        setNullValid(nullValid);
    }

    public EnumDropDownChoice(String id, Class<T> enumClass, IModel<T> model, boolean nullValid) {
        super(id, model, Arrays.asList(enumClass.getEnumConstants()));

        init();

        setNullValid(nullValid);
    }

    private void init(){
        setChoiceRenderer(new IChoiceRenderer<T>() {
            @Override
            public Object getDisplayValue(T object) {
                String s = object.name();

                try {
                    s = getString(object.name());
                } catch (Exception e) {
                    //missing resource
                }

                return s != null ? s : object.name();
            }

            @Override
            public String getIdValue(T object, int index) {
                return object.ordinal() + "";
            }

            @Override
            public T getObject(String id, IModel<? extends List<? extends T>> choices) {
                return choices.getObject().stream().filter(c -> id.equals(c.ordinal() + "")).findAny().get();
            }
        });
    }
}
