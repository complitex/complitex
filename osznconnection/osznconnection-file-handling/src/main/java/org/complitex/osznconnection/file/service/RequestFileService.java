package org.complitex.osznconnection.file.service;

import org.complitex.common.service.ConfigBean;
import org.complitex.osznconnection.file.entity.RequestFile;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * inheaven on 09.03.2016.
 */
@Stateless
public class RequestFileService {
    @EJB
    private ConfigBean configBean;

    @EJB
    private RequestFileBean requestFileBean;

    public String getServiceProviderCode(RequestFile requestFile){
        String mask = null;

        switch (requestFile.getType()){

        }


        return null;
    }

    public String getServiceProviderCode(Long requestFileId){
        return getServiceProviderCode(requestFileBean.getRequestFile(requestFileId));
    }
}
