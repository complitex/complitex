/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.importing.legacy.entity;

/**
 *
 * @author Artem
 */
public class PersonCorrection {

    private Long pkId;
    private long id;
    private String idbud;
    private String rah;
    private String kv;
    private String fam;
    private String im;
    private String ot;
    private String datar;
    private String reltovlaskv;
    private String grajd;
    private String pol;
    private String idrel;
    private String nkra;
    private String nobl;
    private String nrayon;
    private String nmisto;
    private String iddok;
    private String dokseria;
    private String doknom;
    private String dokvidan;
    private String dokdatvid;
    private String idarm;
    private String pkra;
    private String pobl;
    private String prayon;
    private String pmisto;
    private String pdpribza;
    private String pidul;
    private String pbud;
    private String pkorp;
    private String pkv;
    private String pdpribvm;
    private String dprop;
    private String idvidp;
    private String vkra;
    private String vobl;
    private String vrayon;
    private String vmisto;
    private String vidul;
    private String vbud;
    private String vkorp;
    private String vkv;
    private String vdata;
    private String idvip;
    private String larc;
    private String nom;
    private String parentnom;
    private Long systemPersonId;
    private Long systemRegistrationId;
    private boolean processed;
    private boolean kid;
    private String content;

    public PersonCorrection() {
    }

    public PersonCorrection(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public boolean isKid() {
        return kid;
    }

    public void setKid(boolean kid) {
        this.kid = kid;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getParentnom() {
        return parentnom;
    }

    public void setParentnom(String parentnom) {
        this.parentnom = parentnom;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDatar() {
        return datar;
    }

    public void setDatar(String datar) {
        this.datar = datar;
    }

    public String getDokdatvid() {
        return dokdatvid;
    }

    public void setDokdatvid(String dokdatvid) {
        this.dokdatvid = dokdatvid;
    }

    public String getDoknom() {
        return doknom;
    }

    public void setDoknom(String doknom) {
        this.doknom = doknom;
    }

    public String getDokseria() {
        return dokseria;
    }

    public void setDokseria(String dokseria) {
        this.dokseria = dokseria;
    }

    public String getDokvidan() {
        return dokvidan;
    }

    public void setDokvidan(String dokvidan) {
        this.dokvidan = dokvidan;
    }

    public String getDprop() {
        return dprop;
    }

    public void setDprop(String dprop) {
        this.dprop = dprop;
    }

    public String getFam() {
        return fam;
    }

    public void setFam(String fam) {
        this.fam = fam;
    }

    public String getGrajd() {
        return grajd;
    }

    public void setGrajd(String grajd) {
        this.grajd = grajd;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIdarm() {
        return idarm;
    }

    public void setIdarm(String idarm) {
        this.idarm = idarm;
    }

    public String getIdbud() {
        return idbud;
    }

    public void setIdbud(String idbud) {
        this.idbud = idbud;
    }

    public String getIddok() {
        return iddok;
    }

    public void setIddok(String iddok) {
        this.iddok = iddok;
    }

    public String getIdrel() {
        return idrel;
    }

    public void setIdrel(String idrel) {
        this.idrel = idrel;
    }

    public String getIdvidp() {
        return idvidp;
    }

    public void setIdvidp(String idvidp) {
        this.idvidp = idvidp;
    }

    public String getIdvip() {
        return idvip;
    }

    public void setIdvip(String idvip) {
        this.idvip = idvip;
    }

    public String getIm() {
        return im;
    }

    public void setIm(String im) {
        this.im = im;
    }

    public String getKv() {
        return kv;
    }

    public void setKv(String kv) {
        this.kv = kv;
    }

    public String getLarc() {
        return larc;
    }

    public void setLarc(String larc) {
        this.larc = larc;
    }

    public String getNkra() {
        return nkra;
    }

    public void setNkra(String nkra) {
        this.nkra = nkra;
    }

    public String getNmisto() {
        return nmisto;
    }

    public void setNmisto(String nmisto) {
        this.nmisto = nmisto;
    }

    public String getNobl() {
        return nobl;
    }

    public void setNobl(String nobl) {
        this.nobl = nobl;
    }

    public String getNrayon() {
        return nrayon;
    }

    public void setNrayon(String nrayon) {
        this.nrayon = nrayon;
    }

    public String getOt() {
        return ot;
    }

    public void setOt(String ot) {
        this.ot = ot;
    }

    public String getPbud() {
        return pbud;
    }

    public void setPbud(String pbud) {
        this.pbud = pbud;
    }

    public String getPdpribvm() {
        return pdpribvm;
    }

    public void setPdpribvm(String pdpribvm) {
        this.pdpribvm = pdpribvm;
    }

    public String getPdpribza() {
        return pdpribza;
    }

    public void setPdpribza(String pdpribza) {
        this.pdpribza = pdpribza;
    }

    public String getPidul() {
        return pidul;
    }

    public void setPidul(String pidul) {
        this.pidul = pidul;
    }

    public Long getPkId() {
        return pkId;
    }

    public void setPkId(Long pkId) {
        this.pkId = pkId;
    }

    public String getPkorp() {
        return pkorp;
    }

    public void setPkorp(String pkorp) {
        this.pkorp = pkorp;
    }

    public String getPkra() {
        return pkra;
    }

    public void setPkra(String pkra) {
        this.pkra = pkra;
    }

    public String getPkv() {
        return pkv;
    }

    public void setPkv(String pkv) {
        this.pkv = pkv;
    }

    public String getPmisto() {
        return pmisto;
    }

    public void setPmisto(String pmisto) {
        this.pmisto = pmisto;
    }

    public String getPobl() {
        return pobl;
    }

    public void setPobl(String pobl) {
        this.pobl = pobl;
    }

    public String getPol() {
        return pol;
    }

    public void setPol(String pol) {
        this.pol = pol;
    }

    public String getPrayon() {
        return prayon;
    }

    public void setPrayon(String prayon) {
        this.prayon = prayon;
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

    public String getReltovlaskv() {
        return reltovlaskv;
    }

    public void setReltovlaskv(String reltovlaskv) {
        this.reltovlaskv = reltovlaskv;
    }

    public Long getSystemPersonId() {
        return systemPersonId;
    }

    public void setSystemPersonId(Long systemPersonId) {
        this.systemPersonId = systemPersonId;
    }

    public Long getSystemRegistrationId() {
        return systemRegistrationId;
    }

    public void setSystemRegistrationId(Long systemRegistrationId) {
        this.systemRegistrationId = systemRegistrationId;
    }

    public String getVbud() {
        return vbud;
    }

    public void setVbud(String vbud) {
        this.vbud = vbud;
    }

    public String getVdata() {
        return vdata;
    }

    public void setVdata(String vdata) {
        this.vdata = vdata;
    }

    public String getVidul() {
        return vidul;
    }

    public void setVidul(String vidul) {
        this.vidul = vidul;
    }

    public String getVkorp() {
        return vkorp;
    }

    public void setVkorp(String vkorp) {
        this.vkorp = vkorp;
    }

    public String getVkra() {
        return vkra;
    }

    public void setVkra(String vkra) {
        this.vkra = vkra;
    }

    public String getVkv() {
        return vkv;
    }

    public void setVkv(String vkv) {
        this.vkv = vkv;
    }

    public String getVmisto() {
        return vmisto;
    }

    public void setVmisto(String vmisto) {
        this.vmisto = vmisto;
    }

    public String getVobl() {
        return vobl;
    }

    public void setVobl(String vobl) {
        this.vobl = vobl;
    }

    public String getVrayon() {
        return vrayon;
    }

    public void setVrayon(String vrayon) {
        this.vrayon = vrayon;
    }
}
