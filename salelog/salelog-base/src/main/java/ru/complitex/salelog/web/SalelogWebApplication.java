/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.salelog.web;

import ru.complitex.template.web.ComplitexWebApplication;
import ru.complitex.template.web.component.toolbar.ToolbarButton;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author Artem
 */
public class SalelogWebApplication extends ComplitexWebApplication {

    @Override
    public List<ToolbarButton> getApplicationToolbarButtons(String id) {
        return Collections.emptyList();
    }
}
