package ru.complitex.pspoffice.api.model;

import java.util.List;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov
 *         Date: 23.05.2017.
 */
public class BuildingObject extends AddressObject{
    private Map<String, String> number;
    private Map<String, String> corp;
    private Map<String, String> structure;

    private List<BuildingObject> alternatives;

    public BuildingObject() {
    }

    public BuildingObject(Long objectId, Long parentId, Map<String, String> number, Map<String, String> corp, Map<String, String> structure) {
        super(objectId, parentId);

        this.number = number;
        this.corp = corp;
        this.structure = structure;
    }

    public Map<String, String> getNumber() {
        return number;
    }

    public void setNumber(Map<String, String> number) {
        this.number = number;
    }

    public Map<String, String> getCorp() {
        return corp;
    }

    public void setCorp(Map<String, String> corp) {
        this.corp = corp;
    }

    public Map<String, String> getStructure() {
        return structure;
    }

    public void setStructure(Map<String, String> structure) {
        this.structure = structure;
    }

    public List<BuildingObject> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(List<BuildingObject> alternatives) {
        this.alternatives = alternatives;
    }
}
