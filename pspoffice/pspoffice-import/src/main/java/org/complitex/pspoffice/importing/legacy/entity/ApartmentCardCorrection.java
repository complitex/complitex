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
public class ApartmentCardCorrection implements Serializable {

    private Long pkId;
    private long id;
    private String idbud;
    private String rah;
    private String kv;
    private String fio;
    private String idprivat;
    private String larc;
    private Long systemApartmentCardId;
    private boolean processed;
    private String content;

    public ApartmentCardCorrection() {
    }

    public ApartmentCardCorrection(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public String getIdprivat() {
        return idprivat;
    }

    public void setIdprivat(String idprivat) {
        this.idprivat = idprivat;
    }

    public String getLarc() {
        return larc;
    }

    public void setLarc(String larc) {
        this.larc = larc;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIdbud() {
        return idbud;
    }

    public void setIdbud(String idbud) {
        this.idbud = idbud;
    }

    public String getKv() {
        return kv;
    }

    public void setKv(String kv) {
        this.kv = kv;
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

    public String getRah() {
        return rah;
    }

    public void setRah(String rah) {
        this.rah = rah;
    }

    public Long getSystemApartmentCardId() {
        return systemApartmentCardId;
    }

    public void setSystemApartmentCardId(Long systemApartmentCardId) {
        this.systemApartmentCardId = systemApartmentCardId;
    }
}
