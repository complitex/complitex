package ru.complitex.eirc.registry.service.handle.exchange;

import ru.complitex.common.exception.AbstractException;
import ru.complitex.eirc.registry.entity.Container;
import ru.complitex.eirc.registry.entity.Registry;
import ru.complitex.eirc.registry.entity.RegistryRecordData;
import ru.complitex.eirc.registry.util.StringUtil;

import java.util.List;

public abstract class Operation {

	/**
	 * Symbol used escape special symbols
	 */
	public static final char ESCAPE_SYMBOL = '\\';

	/**
	 * Symbol used to split fields in containers
	 */
	public static final char CONTAINER_DATA_DELIMITER = ':';

    /**
     * Parse data and set operation id
     *
     * @throws AbstractException
     */

    public abstract Long getCode();

    /**
	 * Handle operation.
	 *
     * @throws AbstractException if failure occurs
	 */
	public abstract void process(Registry registry, RegistryRecordData registryRecord, Container container,
                                 List<OperationResult> results) throws AbstractException;

	/**
	 * Handle operation.
	 *
	 * @param watchContext OperationWatchContext
	 * @throws AbstractException if failure occurs
	 */
	public void process(Registry registry, RegistryRecordData registryRecord, Container container,
                              List<OperationResult> results, OperationWatchContext watchContext)
			throws AbstractException {
		watchContext.before(this);
		try {
			process(registry, registryRecord, container, results);
		} finally {
			watchContext.after(this);
		}
	}

    public abstract void rollback(OperationResult<?> operationResult, Container container) throws AbstractException;

    public abstract boolean canRollback(OperationResult<?> operationResult, Container container) throws AbstractException;

    /**
     * Split string with delimiter taking in account {@link Operation#ESCAPE_SYMBOL}
     *
     * @param containers Containers data
     * @return List of separate containers
     */
    protected List<String> splitEscapableData(String containers) {
        return StringUtil.splitEscapable(containers, CONTAINER_DATA_DELIMITER, ESCAPE_SYMBOL);
    }

}
