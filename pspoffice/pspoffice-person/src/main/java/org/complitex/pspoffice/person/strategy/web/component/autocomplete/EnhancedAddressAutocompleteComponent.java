package org.complitex.pspoffice.person.strategy.web.component.autocomplete;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.pspoffice.person.strategy.web.component.AbstractAddressCreateDialog;
import org.odlabs.wiquery.ui.autocomplete.EnhancedAutocompleteComponent;

import java.util.List;

/**
 *
 * @author Artem
 */
abstract class EnhancedAddressAutocompleteComponent extends EnhancedAutocompleteComponent {

    private HiddenField<String> openDialogButton;
    private final String entity;

    EnhancedAddressAutocompleteComponent(String id, IModel<DomainObject> model, IChoiceRenderer<DomainObject> renderer,
            boolean canCreate, String entity, List<Long> userOrganizationIds) {
        super(id, model, renderer);

        if (!(entity.equals("apartment") || entity.equals("room"))) {
            throw new IllegalArgumentException("Entity should be only one of `apartment` or `room`");
        }

        this.entity = entity;

        openDialogButton = new HiddenField<String>("openDialogButton", new Model<String>()) {

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                String valueAttribute = tag.getAttribute("value");
                if (Strings.isEmpty(valueAttribute)) {
                    tag.put("value", EnhancedAddressAutocompleteComponent.this.getString("createNew"));
                }
            }
        };
        openDialogButton.setOutputMarkupPlaceholderTag(true);
        openDialogButton.setVisible(canCreate);
        add(openDialogButton);

        final AbstractAddressCreateDialog addressCreateDialog = newAddressCreateDialog("createDialog", userOrganizationIds);
        add(addressCreateDialog);
        openDialogButton.add(new AjaxEventBehavior("onclick") {

            @Override
            protected void onEvent(AjaxRequestTarget target) {
                addressCreateDialog.open(target, getLastSearchTerm(), getParentEntity(), getParentObject());
            }
        });
    }

    private AbstractAddressCreateDialog newAddressCreateDialog(String id, List<Long> userOrganizationIds) {
        if (entity.equals("apartment")) {
            return new AutocompleteApartmentCreateDialog(id, getAutocompleteField(), userOrganizationIds) {

                @Override
                protected void onCreate(AjaxRequestTarget target, DomainObject saved) {
                    EnhancedAddressAutocompleteComponent.this.onCreate(target, saved);
                }
            };
        } else {
            return new AutocompleteRoomCreateDialog(id, getAutocompleteField(), userOrganizationIds) {

                @Override
                protected void onCreate(AjaxRequestTarget target, DomainObject saved) {
                    EnhancedAddressAutocompleteComponent.this.onCreate(target, saved);
                }
            };
        }
    }

    private void onCreate(AjaxRequestTarget target, DomainObject saved) {
        EnhancedAddressAutocompleteComponent.this.setModelObject(saved);
        onCreate(target);
    }

    abstract void onCreate(AjaxRequestTarget target);

    EnhancedAddressAutocompleteComponent setCanCreate(boolean canCreate, AjaxRequestTarget target) {
        boolean wasVisible = openDialogButton.isVisible();
        openDialogButton.setVisible(canCreate);
        if (wasVisible ^ openDialogButton.isVisible()) {
            target.add(openDialogButton);
        }

        return this;
    }

    boolean isCanCreate() {
        return openDialogButton.isVisible();
    }

    abstract DomainObject getParentObject();

    abstract String getParentEntity();

    @Override
    protected abstract void onUpdate(AjaxRequestTarget target);
}
