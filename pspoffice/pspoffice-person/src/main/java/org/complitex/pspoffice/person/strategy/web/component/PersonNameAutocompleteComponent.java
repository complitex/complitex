/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.component;

import com.google.common.base.Function;
import java.util.ArrayList;
import static com.google.common.collect.Lists.*;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import org.apache.wicket.Component;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IModelComparator;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.complitex.pspoffice.person.strategy.entity.PersonName;
import org.complitex.pspoffice.person.strategy.entity.PersonName.PersonNameType;
import org.complitex.pspoffice.person.strategy.service.PersonNameBean;
import org.odlabs.wiquery.ui.autocomplete.AutocompleteAjaxComponent;
import org.odlabs.wiquery.ui.core.JsScopeUiEvent;

/**
 *
 * @author Artem
 */
public class PersonNameAutocompleteComponent extends AutocompleteAjaxComponent<String> {

    private static final int AUTOCOMPLETE_SIZE = 5;
    private static final int AUTOCOMPLETE_MIN_LENGTH = 3;
    @EJB
    private PersonNameBean personNameBean;
    private final PersonNameType personNameType;
    private final boolean saveIfNotFound;
    private List<PersonName> cachedPersonNames;
    private final Locale locale;
    private final IModel<Long> personNameIdModel;
    private final IModel<String> delegateModel;
    private final String defaultNameValue;

    private class PersonNameComponentModel extends Model<String> {

        private String value;

        PersonNameComponentModel(PersonName defaultValue) {
            if (defaultValue != null) {
                setValue(defaultValue.getName(), defaultValue.getId());
            } else {
                setValue(defaultNameValue, null);
            }
        }

        @Override
        public String getObject() {
            return value;
        }

        private void setValue(String value, Long personNameId) {
            if (personNameIdModel != null) {
                personNameIdModel.setObject(personNameId);
            }
            if (delegateModel != null) {
                delegateModel.setObject(value);
            }
            this.value = value;
        }

        @Override
        public void setObject(String term) {
            final List<PersonName> lastRenderedPersonNames = cachedPersonNames != null
                    ? cachedPersonNames : new ArrayList<PersonName>();
            boolean found = false;
            final String normalizedTerm = PersonNameBean.normalizeName(term);
            for (PersonName currentPersonName : lastRenderedPersonNames) {
                if (currentPersonName.getName().equals(normalizedTerm)) {
                    setValue(currentPersonName.getName(), currentPersonName.getId());
                    found = true;
                    break;
                }
            }
            if (!found) {
                if (saveIfNotFound) {
                    PersonName personName = personNameBean.findOrSave(personNameType, normalizedTerm, locale, saveIfNotFound);
                    setValue(personName.getName(), personName.getId());
                } else {
                    setValue(term, null);
                }
            }
        }
    }

    public PersonNameAutocompleteComponent(String id, IModel<Long> personNameIdModel, PersonNameType personNameType,
            Locale locale, boolean saveIfNotFound) {
        this(id, personNameIdModel, null, personNameType, locale, saveIfNotFound);
    }

    public PersonNameAutocompleteComponent(String id, IModel<Long> personNameIdModel, IModel<String> delegateModel,
            PersonNameType personNameType, Locale locale, boolean saveIfNotFound) {
        super(id, new Model<String>());
        this.personNameIdModel = personNameIdModel;
        this.locale = locale;
        this.personNameType = personNameType;
        this.saveIfNotFound = saveIfNotFound;
        PersonName defaultPersonName = personNameIdModel != null
                ? personNameBean.findById(personNameType, personNameIdModel.getObject()) : null;
        this.delegateModel = delegateModel;
        this.defaultNameValue = defaultPersonName == null ? (delegateModel != null ? delegateModel.getObject() : null) : null;
        setModel(new PersonNameComponentModel(defaultPersonName));

        //UI corrections
        //autocomplete delay.
        getAutocompleteField().setDelay(1000);

        //in DEPLOYMENT mode make sure that autocomplete triggered only if at least AUTOCOMPLETE_MIN_LENGTH characters have been entered.
        if (getApplication().getConfigurationType() == RuntimeConfigurationType.DEPLOYMENT) {
            getAutocompleteField().setMinLength(AUTOCOMPLETE_MIN_LENGTH);
        }

        getAutocompleteField().setOpenEvent(JsScopeUiEvent.quickScope(
                "var input = $(this);"+
                "var isFocused = input.is(':focus');"+
                "if(!isFocused){"+
                    "input.autocomplete('close');"+
                "}"
        ));
        getAutocompleteField().setSearchEvent(JsScopeUiEvent.quickScope(
                "return $(this).is(':focus');"
        ));
    }

    @Override
    public IModelComparator getModelComparator() {
        return new IModelComparator() {

            @Override
            public boolean compare(Component component, Object b) {
                final Object a = component.getDefaultModelObject();
                if (a == null && b == null) {
                    return true;
                }
                if (a == null || b == null) {
                    return false;
                }
                return a.equals(b) && (Strings.isEmpty(defaultNameValue) || !defaultNameValue.equals(a));
            }
        };
    }

    @Override
    public List<String> getValues(String term) {
        cachedPersonNames = personNameBean.find(personNameType, term, locale, AUTOCOMPLETE_SIZE);
        return newArrayList(transform(cachedPersonNames, new Function<PersonName, String>() {

            @Override
            public String apply(PersonName personName) {
                return personName.getName();
            }
        }));
    }

    @Override
    public String getValueOnSearchFail(String input) {
        return input;
    }
}
