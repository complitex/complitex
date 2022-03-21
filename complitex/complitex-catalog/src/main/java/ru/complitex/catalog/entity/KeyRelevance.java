package ru.complitex.catalog.entity;

/**
 * @author Ivanov Anatoliy
 */
public class KeyRelevance extends Relevance {
    private Integer keyId;
    private String name;

    public Integer getKeyId() {
        return keyId;
    }

    public void setKeyId(Integer keyId) {
        this.keyId = keyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean is(int keyId) {
        return this.keyId == keyId;
    }
}
