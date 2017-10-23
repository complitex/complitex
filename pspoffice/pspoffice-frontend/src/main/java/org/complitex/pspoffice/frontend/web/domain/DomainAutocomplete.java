package org.complitex.pspoffice.frontend.web.domain;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteTextRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.complitex.pspoffice.frontend.service.PspOfficeClient;
import ru.complitex.pspoffice.api.model.DomainModel;

import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov
 * 23.10.2017 15:07
 */
public class DomainAutocomplete extends Panel {
    @Inject
    private PspOfficeClient pspOfficeClient;

    public DomainAutocomplete(String id, IModel<String> label, String entity, IModel<Long> domainId) {
        super(id);

        HiddenField inputId = new HiddenField<>("inputId", domainId);
        inputId.setOutputMarkupId(true);
        add(inputId);

        AutoCompleteTextField input = new AutoCompleteTextField<DomainModel>("input",
                new LoadableDetachableModel<DomainModel>() {
                    @Override
                    protected DomainModel load() {
                        return getDomainModel(entity, domainId.getObject());
                    }
                },
                new AbstractAutoCompleteTextRenderer<DomainModel>() {

                    @Override
                    protected String getTextValue(DomainModel object) {
                        return object.getAttributes().get(0).getValues().get("1");
                    }

                    @Override
                    protected CharSequence getOnSelectJavaScriptExpression(DomainModel item) {
                        return "$('#" + inputId.getMarkupId() + "').val('" + item.getId() + "'); input";
                    }
                }) {
            @Override
            protected Iterator<DomainModel> getChoices(String input) {
                return pspOfficeClient.request("domain/" + entity, "value", input)
                        .get(new GenericType<List<DomainModel>>() {})
                        .iterator();
            }

            @Override
            protected IConverter<?> createConverter(Class<?> type) {
                if (DomainModel.class.equals(type)) {
                    return new IConverter<DomainModel>() {
                        @Override
                        public DomainModel convertToObject(String s, Locale locale) throws ConversionException {
                            return null;
                        }

                        @Override
                        public String convertToString(DomainModel c, Locale locale) {
                            return c.getAttributes().get(0).getValues().get("1");
                        }
                    };
                }

                return super.createConverter(type);
            }
        };
        add(input);

        add(new Label("label", label).add(new AttributeModifier("for", input.getMarkupId())));
    }

    private DomainModel getDomainModel(String entity, Long id) {
        if (id == null){
            return null;
        }

        DomainModel domainModel =  pspOfficeClient.request("domain/" + entity + "/" + id).get(DomainModel.class);

        domainModel.getAttributes().stream()
                .filter(a -> a.getValues() == null)
                .forEach(a -> a.setValues(new HashMap<>()));

        return domainModel;
    }
}
