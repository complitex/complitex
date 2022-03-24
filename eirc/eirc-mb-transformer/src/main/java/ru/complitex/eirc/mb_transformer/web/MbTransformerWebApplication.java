/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.eirc.mb_transformer.web;

import ru.complitex.template.web.ComplitexWebApplication;
import ru.complitex.template.web.component.toolbar.ToolbarButton;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author Pavel Sknar
 */
public class MbTransformerWebApplication extends ComplitexWebApplication {

    @Override
    public List<ToolbarButton> getApplicationToolbarButtons(String id) {
        return Collections.emptyList();//Collections.unmodifiableList(ImmutableList.of(new PspSearchButton(id)));
    }
}
