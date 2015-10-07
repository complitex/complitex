/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.web;

import com.google.common.collect.ImmutableList;
import org.complitex.pspoffice.person.toolbar.PspSearchButton;
import org.complitex.template.web.ComplitexWebApplication;
import org.complitex.template.web.component.toolbar.ToolbarButton;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author Artem
 */
public class PspWebApplication extends ComplitexWebApplication {

    @Override
    public List<ToolbarButton> getApplicationToolbarButtons(String id) {
        return Collections.unmodifiableList(ImmutableList.of(new PspSearchButton(id)));
    }
}
