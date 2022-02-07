package ru.complitex.osznconnection.file.service.status.details;

import ru.complitex.osznconnection.file.entity.StatusDetailInfo;
import ru.complitex.osznconnection.file.entity.example.AbstractRequestExample;

import java.io.Serializable;

/**
 *
 * @author Artem
 */
public abstract class AbstractExampleConfigurator<T extends AbstractRequestExample> implements ExampleConfigurator<T>, Serializable {

    @Override
    public T createExample(Class<T> exampleClass, StatusDetailInfo statusDetailInfo) {
        try {
            T example = exampleClass.newInstance();
            example.setStatus(statusDetailInfo.getStatus());
            return example;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
