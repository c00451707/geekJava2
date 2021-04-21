package io.hust.pony.demoweb.meta;

import java.io.Serializable;

public interface MetaData<T extends Serializable> extends Serializable {
    T getId();

    void setId(T var1);
}
