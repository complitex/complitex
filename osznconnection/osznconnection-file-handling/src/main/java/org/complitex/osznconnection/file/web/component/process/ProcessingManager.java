package org.complitex.osznconnection.file.web.component.process;

import com.google.common.collect.ImmutableSet;
import org.complitex.common.util.EjbBeanLocator;
import org.complitex.osznconnection.file.service.process.ProcessManagerService;
import org.complitex.osznconnection.file.service.process.ProcessType;

import java.io.Serializable;
import java.util.Set;

public class ProcessingManager implements Serializable {
    private Set<ProcessType> supportedProcessTypes;

    public ProcessingManager(ProcessType... supportedProcessTypes) {
        this.supportedProcessTypes = ImmutableSet.copyOf(supportedProcessTypes);
    }

    private ProcessManagerService processManagerBean() {
        return EjbBeanLocator.getBean(ProcessManagerService.class);
    }

    public boolean isGlobalProcessing() {
        ProcessManagerService processManagerService = processManagerBean();
        boolean isGlobalProcessing = false;
        for (ProcessType processType : supportedProcessTypes) {
            isGlobalProcessing |= processManagerService.isGlobalProcessing(processType);
        }

        return isGlobalProcessing;
    }
}
