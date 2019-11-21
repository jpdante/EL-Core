package com.ellisiumx.elcore.redis;

import java.util.Collection;

public interface DataRepository<T extends Data> {

    Collection<T> getElements();

    T getElement(String dataId);

    Collection<T> getElements(Collection<String> dataIds);

    void addElement(T element, int timeout);

    void addElement(T element);

    void removeElement(T element);

    void removeElement(String dataId);

    boolean elementExists(String dataId);

    int clean();
}
