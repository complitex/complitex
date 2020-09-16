package org.complitex.osznconnection.file.entity.privilege;

import org.complitex.osznconnection.file.entity.AbstractAccountRequest;

import static org.complitex.osznconnection.file.entity.RequestFileType.DEBT;

/**
 * @author Anatoly Ivanov
 * 16.09.2020 20:39
 */
public class Debt extends AbstractAccountRequest<DebtDBF> {
    public Debt() {
        super(DEBT);
    }

    public Debt(Long requestFileId){
        super(DEBT);

        setRequestFileId(requestFileId);
    }
}
