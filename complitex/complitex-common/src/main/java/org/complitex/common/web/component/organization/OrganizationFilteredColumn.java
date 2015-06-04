package org.complitex.common.web.component.organization;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.model.PropertyModel;
import org.complitex.common.web.component.domain.DomainObjectFilteredColumn;

import java.util.Locale;

/**
 * @author inheaven on 031 31.03.15 18:30
 */
public class OrganizationFilteredColumn<T> extends DomainObjectFilteredColumn<T> {
    private Long[] types;

    public OrganizationFilteredColumn(String propertyExpression, Locale locale, Long... types) {
        super("organization", propertyExpression, locale);

        this.types = types;
    }

    @Override
    public Component getFilter(String componentId, FilterForm<?> form) {
        return new OrganizationIdPicker(componentId, new PropertyModel<>(form.getModel(), getPropertyExpression()), types);
    }
}