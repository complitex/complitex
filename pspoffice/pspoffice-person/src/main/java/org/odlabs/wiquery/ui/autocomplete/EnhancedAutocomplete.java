/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.odlabs.wiquery.ui.autocomplete;

import org.apache.wicket.model.IModel;
import org.odlabs.wiquery.core.javascript.JsQuery;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.core.javascript.JsUtils;

/**
 *
 * @author Artem
 */
public class EnhancedAutocomplete<T> extends Autocomplete<T> {

    public EnhancedAutocomplete(String id, IModel<T> model) {
        super(id, model);
    }

    public EnhancedAutocomplete(String id) {
        super(id);
    }

    @Override
    public JsStatement statement() {
        return new JsQuery(this).$().chain("enhanced_address_autocomplete", getOptions().getJavaScriptOptions());
    }

    @Override
    public JsStatement close() {
        return new JsQuery(this).$().chain("enhanced_address_autocomplete", "'close'");
    }

    @Override
    public JsStatement destroy() {
        return new JsQuery(this).$().chain("enhanced_address_autocomplete", "'destroy'");
    }

    @Override
    public JsStatement disable() {
        return new JsQuery(this).$().chain("enhanced_address_autocomplete", "'disable'");
    }

    @Override
    public JsStatement enable() {
        return new JsQuery(this).$().chain("enhanced_address_autocomplete", "'enable'");
    }

    @Override
    public JsStatement search() {
        return new JsQuery(this).$().chain("enhanced_address_autocomplete", "'search'");
    }

    @Override
    public JsStatement search(String value) {
        return new JsQuery(this).$().chain("enhanced_address_autocomplete", "'search'", JsUtils.quotes(value));
    }

    @Override
    public JsStatement widget() {
        return new JsQuery(this).$().chain("enhanced_address_autocomplete", "'widget'");
    }
}
