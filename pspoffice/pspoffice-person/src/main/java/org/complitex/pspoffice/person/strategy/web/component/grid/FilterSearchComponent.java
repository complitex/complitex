/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.component.grid;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.web.component.search.WiQuerySearchComponent;
import org.odlabs.wiquery.ui.autocomplete.AutocompleteAjaxComponent;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Artem
 */
public abstract class FilterSearchComponent extends AutocompleteAjaxComponent<DomainObject> {

    public FilterSearchComponent(String id, IModel<DomainObject> filterModel) {
        super(id, filterModel);
    }

    protected final void init() {
        setChoiceRenderer(new IChoiceRenderer<DomainObject>() {

            @Override
            public Object getDisplayValue(DomainObject object) {
                return FilterSearchComponent.this.render(object);
            }

            @Override
            public String getIdValue(DomainObject object, int index) {
                return String.valueOf(object.getObjectId());
            }

            @Override
            public DomainObject getObject(String id, IModel<? extends List<? extends DomainObject>> choices) {
                return choices.getObject().stream().filter(c -> id.equals(String.valueOf(c.getObjectId()))).findAny().get();
            }
        });

        setAutoUpdate(true);
    }

    @Override
    public List<DomainObject> getValues(String term) {
        final List<DomainObject> choiceList = Lists.newArrayList();

        final List<? extends DomainObject> equalToExample = find(DomainObjectFilter.ComparisonType.EQUALITY, term,
                WiQuerySearchComponent.AUTO_COMPLETE_SIZE);
        choiceList.addAll(equalToExample);

        if (equalToExample.size() < WiQuerySearchComponent.AUTO_COMPLETE_SIZE) {
            final Set<Long> idsSet = Sets.newHashSet();
            for (DomainObject o : equalToExample) {
                idsSet.add(o.getObjectId());
            }

            final List<? extends DomainObject> likeExample = find(DomainObjectFilter.ComparisonType.LIKE, term,
                    WiQuerySearchComponent.AUTO_COMPLETE_SIZE);

            final Iterator<? extends DomainObject> likeIterator = likeExample.iterator();
            while (likeIterator.hasNext() && choiceList.size() < WiQuerySearchComponent.AUTO_COMPLETE_SIZE) {
                final DomainObject likeObject = likeIterator.next();
                if (!idsSet.contains(likeObject.getObjectId())) {
                    choiceList.add(likeObject);
                    idsSet.add(likeObject.getObjectId());
                }
            }
        }
        return choiceList;
    }

    protected abstract List<? extends DomainObject> find(DomainObjectFilter.ComparisonType comparisonType, String term, int size);

    @Override
    public DomainObject getValueOnSearchFail(String input) {
        return null;
    }

    protected abstract String render(DomainObject object);
}
