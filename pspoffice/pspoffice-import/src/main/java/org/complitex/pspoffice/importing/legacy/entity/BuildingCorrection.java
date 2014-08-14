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
public class BuildingCorrection implements Serializable {

    private Long pkId;
    private long id;
    private String idjek;
    private String idul;
    private String dom;
    private String korpus;
    private Long systemBuildingId;
    private boolean processed;
    private String content;

    public BuildingCorrection() {
    }

    public BuildingCorrection(long id, String idjek, String idul, String dom, String korpus, String content) {
        this.id = id;
        this.idjek = idjek;
        this.idul = idul;
        this.dom = dom;
        this.korpus = korpus;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public String getDom() {
        return dom;
    }

    public void setDom(String dom) {
        this.dom = dom;
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

    public String getIdul() {
        return idul;
    }

    public void setIdul(String idul) {
        this.idul = idul;
    }

    public String getKorpus() {
        return korpus;
    }

    public void setKorpus(String korpus) {
        this.korpus = korpus;
    }

    public Long getPkId() {
        return pkId;
    }

    public void setPkId(Long pkId) {
        this.pkId = pkId;
    }

    public Long getSystemBuildingId() {
        return systemBuildingId;
    }

    public void setSystemBuildingId(Long systemBuildingId) {
        this.systemBuildingId = systemBuildingId;
    }
}
