package org.complitex.osznconnection.file.entity.subsidy;

import org.complitex.osznconnection.file.entity.AbstractFieldMapEntity;

import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov
 * 24.01.2018 22:05
 */
public class SubsidySplit extends AbstractFieldMapEntity<SubsidySplitField> implements Serializable{
    private Long id;
    private Long subsidyId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubsidyId() {
        return subsidyId;
    }

    public void setSubsidyId(Long subsidyId) {
        this.subsidyId = subsidyId;
    }

    @Override
    public String toString() {
        return super.getToStringHelper()
                .add("id", id)
                .add("subsidyId", subsidyId)
                .toString();
    }
}
