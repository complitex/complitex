package org.complitex.osznconnection.file.entity;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * 
 * @author Artem
 */
public class StatusDetail implements Serializable {

    private String id;
    private Long count;
    private Map<String, Object> details;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    public String getDetail(String detailName) {
        return Objects.toString(details.get(detailName));
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
