/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.importing.legacy.entity;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Artem
 */
public class ImportStatus implements Serializable {

    private final AtomicInteger index;
    private final AtomicBoolean finishedStatus = new AtomicBoolean(false);

    public ImportStatus(int index) {
        this.index = new AtomicInteger(index);
    }

    public int getIndex() {
        return index.get();
    }
    
    public void increment(){
        this.index.incrementAndGet();
    }

    public boolean isFinished() {
        return finishedStatus.get();
    }

    public void finish(){
        finishedStatus.lazySet(true);
    }
}
