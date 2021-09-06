package ru.complitex.catalog.entity;

/**
 * @author Ivanov Anatoliy
 */
public class Value extends KeyRelevance {
    private Type type;
    private Locale locale;
    private Catalog referenceCatalog;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Catalog getReferenceCatalog() {
        return referenceCatalog;
    }

    public void setReferenceCatalog(Catalog referenceCatalog) {
        this.referenceCatalog = referenceCatalog;
    }

    public String getResourceKey() {
        return getName().toLowerCase() + (getLocale() != null ? ("." + getLocale().getName().toLowerCase()) : "");
    }

    public boolean is(int keyId, int locale) {
        return is(keyId) && this.locale != null && this.locale.getKeyId() == locale;
    }
}
