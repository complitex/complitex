/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.importing.legacy.entity;

import java.io.Serializable;

/**
 *
 * @author Artem
 */
public class ReferenceDataCorrection implements Serializable {

    private Long pkId;
    private long id;
    private String idjek;
    private String nkod;
    private Long systemObjectId;
    private boolean processed;
    private String content;
    private String entity;

    public ReferenceDataCorrection() {
    }

    public ReferenceDataCorrection(String entity, long id, String idjek, String nkod, String content) {
        this.entity = entity;
        this.id = id;
        this.idjek = idjek;
        this.nkod = nkod;
        this.content = content;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIdjek() {
        return idjek;
    }

    public void setIdjek(String idjek) {
        this.idjek = idjek;
    }

    public String getNkod() {
        return nkod;
    }

    public void setNkod(String nkod) {
        this.nkod = nkod;
    }

    public Long getPkId() {
        return pkId;
    }

    public void setPkId(Long pkId) {
        this.pkId = pkId;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public Long getSystemObjectId() {
        return systemObjectId;
    }

    public void setSystemObjectId(Long systemObjectId) {
        this.systemObjectId = systemObjectId;
    }
}
