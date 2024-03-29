package ru.complitex.osznconnection.file.service.status.details;

import ru.complitex.osznconnection.file.entity.StatusDetail;
import ru.complitex.osznconnection.file.entity.StatusDetailInfo;
import ru.complitex.osznconnection.file.entity.example.AbstractRequestExample;

public interface ExampleConfigurator<T extends AbstractRequestExample> {

    T createExample(Class<T> exampleClass, StatusDetailInfo statusDetailInfo);

    T createExample(StatusDetail statusDetail);
}
