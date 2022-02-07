package ru.complitex.osznconnection.file.service.process;

import ru.complitex.osznconnection.file.entity.AbstractRequestFileGroup;
import ru.complitex.osznconnection.file.entity.RequestFile;

import java.util.List;

/**
 * inheaven on 12.04.2016.
 */
public class LoadGroupParameter<T extends AbstractRequestFileGroup> {

    List<T> requestFileGroups;
    List<RequestFile> linkError;

    public LoadGroupParameter(List<T> requestFileGroups, List<RequestFile> linkError) {
        this.requestFileGroups = requestFileGroups;
        this.linkError = linkError;
    }

    public List<T> getRequestFileGroups() {
        return requestFileGroups;
    }

    public List<RequestFile> getLinkError() {
        return linkError;
    }
}
