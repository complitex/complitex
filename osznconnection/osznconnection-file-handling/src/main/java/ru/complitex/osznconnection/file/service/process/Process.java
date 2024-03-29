package ru.complitex.osznconnection.file.service.process;

import ru.complitex.common.entity.IExecutorObject;
import ru.complitex.common.service.executor.ExecutorCommand;
import ru.complitex.osznconnection.file.entity.RequestFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.01.11 16:07
 */
public class Process<T extends IExecutorObject> extends ExecutorCommand<T>{
    private List<RequestFile> linkError = new CopyOnWriteArrayList<>();

    private Map<Object, Integer> processedIndex = new ConcurrentHashMap<>();

    private ProcessType processType;

    private boolean preprocess = false;
    private int preprocessErrorCount = 0;
    private boolean preprocessError = false;

    public Process(ProcessType processType) {
        this.processType = processType;
    }

    public ProcessType getProcessType() {
        return processType;
    }

    public boolean isPreprocess() {
        return preprocess;
    }

    public void setPreprocess(boolean preprocess) {
        this.preprocess = preprocess;
    }

    public void preprocessError(){
        preprocessError = true;
        preprocess = false;
    }

    public void addLinkError(List<RequestFile> list){
        linkError.addAll(list);
        preprocessErrorCount = linkError.size();
    }

    public List<RequestFile> getLinkError(boolean flush) {
        List<RequestFile> list = new ArrayList<>();
        list.addAll(linkError);

        if (flush){
            linkError.clear();
        }

        return Collections.unmodifiableList(list);
    }

     @SuppressWarnings({"unchecked"})
     public  List<T> getProcessed(Object queryKey){
        List<T> list = new ArrayList<>();

        Integer index = processedIndex.get(queryKey);

        int size = getProcessed().size();

        List processed = getProcessed().subList(index != null ? index : 0, size);

        for (Object obj : processed){
            list.add((T) obj);
        }

        processedIndex.put(queryKey, size);

        return Collections.unmodifiableList(list);
    }

    public int getErrorCount() {
        return super.getErrorCount() + preprocessErrorCount;
    }

    public boolean isProcessing(){
        return super.isRunning() || preprocess;
    }

    public boolean isCriticalError(){
        return super.isCriticalError() || preprocessError;
    }

    public void init(){
        super.init();

        preprocessError = false;
        processedIndex.clear();
        linkError.clear();
        preprocessErrorCount = 0;
    }
}
