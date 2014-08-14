package org.odlabs.wiquery.ui.autocomplete;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.TextRequestHandler;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.string.Strings;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.complitex.dictionary.entity.DomainObject;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.core.javascript.JsUtils;
import org.odlabs.wiquery.ui.core.JsScopeUiEvent;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * $Id: AbstractAutocompleteComponent.java 718 2011-02-27 13:45:41Z roche.jul@gmail.com $
 * <p>
 * Base for the autocomplete component
 * </p>
 * @author Julien Roche
 * @since 1.1
 */
public abstract class EnhancedAutocompleteComponent extends FormComponentPanel<DomainObject> {

    private String term;
    private boolean autoUpdate = false;

    /**
     * Ajax behavior to create the list of possibles values
     * 
     * @author Julien Roche
     * 
     */
    private class InnerAutocompleteAjaxBehavior extends AbstractAjaxBehavior {
        // Constants

        /** Constant of serialization */
        private static final long serialVersionUID = -5411632961744455568L;

        public void onRequest() {
            term =
                    this.getComponent().getRequest().getQueryParameters().getParameterValue("term").toString();

            if (!Strings.isEmpty(term)) {
                StringWriter sw = new StringWriter();
                try {
                    JsonGenerator gen = new JsonFactory().createJsonGenerator(sw);

                    AutocompleteJson value = null;
                    Integer index = 0;
                    List<Object> json = new ArrayList<Object>();

                    for (DomainObject obj : getValues(term)) {
                        index++;
                        value = newAutocompleteJson(index, obj);
                        json.add(value);
                    }

                    new ObjectMapper().writeValue(gen, json);

                } catch (IOException e) {
                    throw new WicketRuntimeException(e);
                }

                RequestCycle.get().scheduleRequestHandlerAfterCurrent(
                        new TextRequestHandler("application/json", "utf-8", sw.toString()));
            }
        }
    }

    /**
     * Inner {@link Autocomplete}
     * @author Julien Roche
     *
     */
    /* Modified by Artem */
    private class InnerAutocomplete<E> extends EnhancedAutocomplete<E> {
        // Constants

        /**	Constant of serialization */
        private static final long serialVersionUID = -6129719872925080990L;

        /**
         * Constructor
         * @param id Wicket identifiant
         * @param model Model
         */
        InnerAutocomplete(String id, IModel<E> model) {
            super(id, model);
        }

        @Override
        public void renderHead(IHeaderResponse response) {
            super.renderHead(response);
            response.render(JavaScriptHeaderItem
                    .forReference(WiQueryAutocompleteJavaScriptResourceReference.get()));

            /* Modified by Artem */
            response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(EnhancedAutocompleteComponent.class,
                    EnhancedAutocompleteComponent.class.getSimpleName() + ".js")));
            /* end modification */
        }

        @Override
        protected void onBeforeRender() {
            onBeforeRenderAutocomplete(this);
            super.onBeforeRender();
        }

        @Override
        public Autocomplete<E> setCloseEvent(JsScopeUiEvent close) {
            throw new WicketRuntimeException("You can't define the close event");
        }

        @Override
        public Autocomplete<E> setSelectEvent(JsScopeUiEvent select) {
            throw new WicketRuntimeException("You can't define the select event");
        }

        @Override
        public Autocomplete<E> setChangeEvent(JsScopeUiEvent select) {
            throw new WicketRuntimeException("You can't define the change event");
        }

        @Override
        public Autocomplete<E> setSource(AutocompleteSource source) {
            throw new WicketRuntimeException("You can't define the source");
        }

        @Override
        public JsStatement statement() {
            StringBuilder js = new StringBuilder();
            js.append("$.ui.autocomplete.wiquery.changeEvent(event, ui,").append(
                    JsUtils.quotes(autocompleteHidden.getMarkupId()));
            if (isAutoUpdate()) {
                js.append(",'").append(updateAjax.getCallbackUrl()).append("'");
            }
            js.append(");");
            super.setChangeEvent(JsScopeUiEvent.quickScope(js.toString()));
            super.setSelectEvent(JsScopeUiEvent.quickScope(js.append("$(event.target).blur();").toString()));
            JsStatement jsStatement = super.statement();
            return jsStatement;
        }
    }
    // Constants
    /** Constant of serialization */
    private static final long serialVersionUID = -3377109382248062940L;
    // Wicket components
    private final InnerAutocompleteAjaxBehavior innerAutcompleteAjaxBehavior;
    /** Constant of wiQuery Autocomplete resource */
    public static final JavaScriptResourceReference WIQUERY_AUTOCOMPLETE_JS =
            new JavaScriptResourceReference(AutocompleteAjaxComponent.class, "wiquery-autocomplete.js");
    // Wicket components
    private final Autocomplete<String> autocompleteField;
    private final HiddenField<String> autocompleteHidden;
    private static final String NOT_ENTERED = "NOT_ENTERED";
    /** The choiceRenderer used to generate display/id values for the objects. */
    private IChoiceRenderer<DomainObject> choiceRenderer;
    private AbstractDefaultAjaxBehavior updateAjax;

    /**
     * Constructor
     * @param id Wicket identifiant
     * @param model Model of the default value
     */
    public EnhancedAutocompleteComponent(String id, final IModel<DomainObject> model) {
        super(id, model);
        setOutputMarkupPlaceholderTag(true);

        autocompleteHidden =
                new HiddenField<String>("autocompleteHidden", new Model<String>(NOT_ENTERED) {

            private static final long serialVersionUID = 1L;

            @Override
            public String getObject() {
                DomainObject modelObject = EnhancedAutocompleteComponent.this.getModelObject();
                if (modelObject != null) {
                    return super.getObject();
                } else {
                    return null;
                }
            }
        });
        autocompleteHidden.setOutputMarkupId(true);
        add(autocompleteHidden);

        autocompleteField = new InnerAutocomplete<String>("autocompleteField", new IModel<String>() {

            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            public String getObject() {
                DomainObject modelObject = EnhancedAutocompleteComponent.this.getModelObject();
                if (modelObject != null) {
                    String objectValue = (String) choiceRenderer.getDisplayValue(modelObject);

                    String displayValue = "";
                    if (objectValue != null) {
                        displayValue = objectValue.toString();
                    }
                    return displayValue;
                } else {
                    return null;
                }
            }

            public void setObject(String object) {
            }

            public void detach() {
            }
        });
        add(autocompleteField);

        updateAjax = new AbstractDefaultAjaxBehavior() {

            private static final long serialVersionUID = 1L;

            @Override
            protected void respond(AjaxRequestTarget target) {
                final String hiddenInput = autocompleteHidden.getInput();
                final String fieldInput = autocompleteField.getInput();
                autocompleteHidden.setConvertedInput(hiddenInput);
                autocompleteField.setConvertedInput(fieldInput);
                validate();
                if (isValid()) {
                    updateModel();
                    onUpdate(target);
                }
            }
        };
        add(updateAjax);

        innerAutcompleteAjaxBehavior = new InnerAutocompleteAjaxBehavior();
        add(innerAutcompleteAjaxBehavior);
    }

    public EnhancedAutocompleteComponent(String id, final IModel<DomainObject> model, IChoiceRenderer<DomainObject> renderer) {
        this(id, model);
        this.setChoiceRenderer(renderer);
    }

    /**
     * Called when the value has been updated via ajax
     * @param target
     */
    protected void onUpdate(AjaxRequestTarget target) {
    }

    @Override
    protected final void convertInput() {
        String valueId = autocompleteHidden.getConvertedInput();
        String input = autocompleteField.getConvertedInput();
        final DomainObject object = this.getModelObject();
        final IChoiceRenderer<DomainObject> renderer = getChoiceRenderer();

        if (NOT_ENTERED.equals(valueId)) {
            valueId = null;
        }

        if (valueId == null && Strings.isEmpty(input)) {
            setConvertedInput(null);
        } else if (valueId == null) {
            setConvertedInput(getValueOnSearchFail(input));
        } else if (Strings.isEmpty(input)) {
            setConvertedInput(null);
        } else if (object == null || input.compareTo((String) renderer.getDisplayValue(object)) != 0) {
            final List<DomainObject> choices = getChoices();
            boolean found = false;
            for (int index = 0; index < choices.size(); index++) {
                // Get next choice
                final DomainObject choice = choices.get(index);
                final String idValue = renderer.getIdValue(choice, index + 1);
                if (idValue.equals(valueId)) {
                    setConvertedInput(choice);
                    found = true;
                    break;
                }
            }
            if (!found) {
                // if it is still not entered, then it means this field was not touched
                // so keep the original value
                if (valueId.equals(NOT_ENTERED)) {
                    setConvertedInput(getModelObject());
                } else {
                    setConvertedInput(getValueOnSearchFail(input));
                }
            }
        } else {
            setConvertedInput(object);
        }
    }

    protected List<DomainObject> getChoices() {
        return getValues(term);
    }

    /**
     * Method called when the search is launched
     * @param term Value typed
     * @return possible values
     */
    public abstract List<DomainObject> getValues(String term);

    /**
     * @return the autocomplete field
     */
    public Autocomplete<String> getAutocompleteField() {
        return autocompleteField;
    }

    /**
     * @return Hidden field storing the identifiant of the Wicket model
     */
    public HiddenField<String> getAutocompleteHidden() {
        return autocompleteHidden;
    }

    /**
     * Method called when the input is not empty and the search failed
     * @param input Current input
     * @return a new value
     */
    public abstract DomainObject getValueOnSearchFail(String input);

    /**
     * Create an {@link AutocompleteJson}
     * 
     * @param id
     * @param obj
     * @return a new instance of {@link AutocompleteJson}
     */
    @SuppressWarnings("unchecked")
    protected AutocompleteJson newAutocompleteJson(int id, DomainObject obj) {

        boolean thisOneSelected = obj.equals(getModelObject());
        Object objectValue = getChoiceRenderer().getDisplayValue(obj);
        Class<?> objectClass = (objectValue == null ? null : objectValue.getClass());

        String displayValue = "";
        if (objectClass != null && objectClass != String.class) {
            final IConverter converter = getConverter(objectClass);
            displayValue = converter.convertToString(objectValue, getLocale());
        } else if (objectValue != null) {
            displayValue = objectValue.toString();
        }
        final String idValue = getChoiceRenderer().getIdValue(obj, id);
        if (thisOneSelected) {
            autocompleteHidden.setModelObject(idValue);
        }

        return new AutocompleteJson(idValue, displayValue);
    }

    public void setChoiceRenderer(IChoiceRenderer<DomainObject> choiceRenderer) {
        this.choiceRenderer = choiceRenderer;
    }

    public IChoiceRenderer<DomainObject> getChoiceRenderer() {
        if (choiceRenderer == null) {
            choiceRenderer = new ChoiceRenderer<DomainObject>();
        }
        return choiceRenderer;
    }

    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    /**
     * Should this value get sent to the server when it is selected automatically
     * @return
     */
    public boolean isAutoUpdate() {
        return autoUpdate;
    }

    protected void onBeforeRenderAutocomplete(Autocomplete<?> autocomplete) {
        DomainObject defaultValue = EnhancedAutocompleteComponent.this.getModelObject();

        if (defaultValue != null) {
            AutocompleteJson value = null;
            value = newAutocompleteJson(0, defaultValue);
            autocomplete.setDefaultModelObject(value.getLabel());
            getAutocompleteHidden().setModelObject(value.getValueId());
        }

        autocomplete.getOptions().putLiteral("source",
                innerAutcompleteAjaxBehavior.getCallbackUrl().toString());
    }

    /* Modified by Artem */
    protected String getLastSearchTerm() {
        return term;
    }
    /* end modification */
}
