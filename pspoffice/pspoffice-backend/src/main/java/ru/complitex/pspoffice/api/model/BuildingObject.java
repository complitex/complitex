package ru.complitex.pspoffice.api.model;

import java.util.List;

/**
 * @author Anatoly A. Ivanov
 *         Date: 23.05.2017.
 */
public class BuildingObject extends AddressObject{
    private List<AddressName> numbers;
    private List<AddressName> corps;
    private List<AddressName> structures;

    private List<BuildingObject> alternatives;

    public BuildingObject(Long objectId, Long parentId, List<AddressName> numbers, List<AddressName> corps, List<AddressName> structures) {
        super(objectId, parentId);

        this.numbers = numbers;
        this.corps = corps;
        this.structures = structures;
    }

    public List<AddressName> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<AddressName> numbers) {
        this.numbers = numbers;
    }

    public List<AddressName> getCorps() {
        return corps;
    }

    public void setCorps(List<AddressName> corps) {
        this.corps = corps;
    }

    public List<AddressName> getStructures() {
        return structures;
    }

    public void setStructures(List<AddressName> structures) {
        this.structures = structures;
    }

    public List<BuildingObject> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(List<BuildingObject> alternatives) {
        this.alternatives = alternatives;
    }
}
