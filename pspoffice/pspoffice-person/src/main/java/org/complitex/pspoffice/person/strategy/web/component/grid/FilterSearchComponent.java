/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.component.grid;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.example.ComparisonType;
import org.complitex.dictionary.web.component.search.WiQuerySearchComponent;
import org.odlabs.wiquery.ui.autocomplete.AutocompleteAjaxComponent;

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
                return String.valueOf(object.getId());
            }
        });

        setAutoUpdate(true);
    }

    @Override
    public List<DomainObject> getValues(String term) {
        final List<DomainObject> choiceList = Lists.newArrayList();

        final List<? extends DomainObject> equalToExample = find(ComparisonType.EQUALITY, term,
                WiQuerySearchComponent.AUTO_COMPLETE_SIZE);
        choiceList.addAll(equalToExample);

        if (equalToExample.size() < WiQuerySearchComponent.AUTO_COMPLETE_SIZE) {
            final Set<Long> idsSet = Sets.newHashSet();
            for (DomainObject o : equalToExample) {
                idsSet.add(o.getId());
            }

            final List<? extends DomainObject> likeExample = find(ComparisonType.LIKE, term,
                    WiQuerySearchComponent.AUTO_COMPLETE_SIZE);

            final Iterator<? extends DomainObject> likeIterator = likeExample.iterator();
            while (likeIterator.hasNext() && choiceList.size() < WiQuerySearchComponent.AUTO_COMPLETE_SIZE) {
                final DomainObject likeObject = likeIterator.next();
                if (!idsSet.contains(likeObject.getId())) {
                    choiceList.add(likeObject);
                    idsSet.add(likeObject.getId());
                }
            }
        }
        return choiceList;
    }

    protected abstract List<? extends DomainObject> find(ComparisonType comparisonType, String term, int size);

    @Override
    public DomainObject getValueOnSearchFail(String input) {
        return null;
    }

    protected abstract String render(DomainObject object);
}
