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
public class StreetCorrection implements Serializable {

    private Long pkId;
    private long id;
    private String idjek;
    private String utype;
    private String nkod;
    private String rtype;
    private String nkod1;
    private Long systemStreetId;
    private boolean processed;
    private String content;

    public StreetCorrection() {
    }

    public StreetCorrection(long id, String idjek, String utype, String nkod, String rtype, String nkod1, String content) {
        this.id = id;
        this.idjek = idjek;
        this.utype = utype;
        this.nkod = nkod;
        this.rtype = rtype;
        this.nkod1 = nkod1;
        this.content = content;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIdjek() {
        return idjek;
    }

    public void setIdjek(String idjek) {
        this.idjek = idjek;
    }

    public Long getPkId() {
        return pkId;
    }

    public void setPkId(Long pkId) {
        this.pkId = pkId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNkod() {
        return nkod;
    }

    public void setNkod(String nkod) {
        this.nkod = nkod;
    }

    public String getNkod1() {
        return nkod1;
    }

    public void setNkod1(String nkod1) {
        this.nkod1 = nkod1;
    }

    public String getRtype() {
        return rtype;
    }

    public void setRtype(String rtype) {
        this.rtype = rtype;
    }

    public String getUtype() {
        return utype;
    }

    public void setUtype(String utype) {
        this.utype = utype;
    }

    public Long getSystemStreetId() {
        return systemStreetId;
    }

    public void setSystemStreetId(Long systemStreetId) {
        this.systemStreetId = systemStreetId;
    }
}
