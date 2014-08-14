/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.importing.legacy.entity;

import org.complitex.dictionary.entity.IImportFile;

/**
 *
 * @author Artem
 */
public enum LegacyDataImportFile implements IImportFile {

    STREET("sul.csv", "ID	UTYPE	NKOD	RTYPE	NKOD1"),
    BUILDING("sbud.csv",
    "ID	IDJEK	IDUL	DOM	KORPUS	NKOD	ETAG	BEGLS	ENDLS	DILN	TIPB	NDOP	UTYPE	UL"),
    OWNERSHIP_FORM("sprivat.csv", "ID	NKOD	CLR"),
    MILITARY_DUTY("sarm.csv", "ID	NKOD"),
    OWNER_RELATIONSHIP("srel.csv", "ID	NKOD	STAT"),
    DEPARTURE_REASON("svip.csv", "ID	NKOD"),
    REGISTRATION_TYPE("svidp.csv", "ID	NKOD"),
    DOCUMENT_TYPE("sdoc.csv", "ID	NKOD"),
    OWNER_TYPE("svlas.csv", "ID	NKOD"),
    PERSON("peop.csv", "ID	IDGEK	IDBUD	RAH	KV	FAM	IM	OT	DATAR	RELTOVLASKV	"
    + "NAC	GRAJD	POL	IDREL	IDOBR	IDSTUP	IDSTAN	NKRA	NOBL	NRAYON	NMISTO	IDDOK	"
    + "DOKSERIA	DOKNOM	DOKVIDAN	DOKDATVID	PRAC	POSADA	IDARM	DATEARM	PKRA	POBL	PRAYON	"
    + "PMISTO	PDPRIBZA	PIDUL	PBUD	PKORP	PKV	PDPRIBVM	DPROP	IDVIDP	DOPPROP	VKRA	"
    + "VOBL	VRAYON	VMISTO	VIDUL	VBUD	VKORP	VKV	VDATA	IDVIP	PARENTNOM	KARTOTEKA	"
    + "SVID	LARC	NOM	IDENTIF"),
    APARTMENT_CARD("rahunok.csv", "ID	IDBUD	RAH	KV	FIO	IDPRIVAT	KIMN	TEL	TIP_TEL	"
            + "KPR	ETAG	BALCON	LODJ	KIM_1	KIM_2	KIM_3	KIM_4	KIM_5	KUHNA	SAN	VANA	"
            + "KORID	KLAD	INSHI	PLOSHA	VSEGO	VSEG_PL	VSEG	BORG	LARC	MANUALADDED	IDTKV");
    
    private final String fileName;
    private final String csvHeader;

    private LegacyDataImportFile(String fileName, String csvHeader) {
        this.fileName = fileName;
        this.csvHeader = csvHeader;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    public String getCsvHeader() {
        return csvHeader;
    }
}
