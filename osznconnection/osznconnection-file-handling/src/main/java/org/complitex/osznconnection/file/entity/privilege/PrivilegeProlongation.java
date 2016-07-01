package org.complitex.osznconnection.file.entity.privilege;

import org.complitex.osznconnection.file.entity.AbstractAccountRequest;
import org.complitex.osznconnection.file.entity.RequestFileType;

/**
 * @author inheaven on 23.06.2016.
 */
public class PrivilegeProlongation extends AbstractAccountRequest<PrivilegeProlongationDBF>{
    public enum TYPE {S, P}

    @Override
    public RequestFileType getRequestFileType() {
        return RequestFileType.PRIVILEGE_PROLONGATION;
    }
}
