package ru.complitex.pspoffice.api.model;

import java.util.List;

/**
 * @author Anatoly A. Ivanov
 *         Date: 23.05.2017.
 */
public class BuildingObject extends AddressObject{
    private List<Name> numbers;
    private List<Name> corps;
    private List<Name> structures;

    private List<BuildingObject> alternatives;

    public BuildingObject() {
    }

    public BuildingObject(Long objectId, Long parentId, List<Name> numbers, List<Name> corps, List<Name> structures) {
        super(objectId, parentId);

        this.numbers = numbers;
        this.corps = corps;
        this.structures = structures;
    }

    public List<Name> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<Name> numbers) {
        this.numbers = numbers;
    }

    public List<Name> getCorps() {
        return corps;
    }

    public void setCorps(List<Name> corps) {
        this.corps = corps;
    }

    public List<Name> getStructures() {
        return structures;
    }

    public void setStructures(List<Name> structures) {
        this.structures = structures;
    }

    public List<BuildingObject> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(List<BuildingObject> alternatives) {
        this.alternatives = alternatives;
    }
}
