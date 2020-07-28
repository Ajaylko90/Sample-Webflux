package com.sample.webflux.dao;

import java.util.List;

public interface DBInteraction<K,V> {
    public List<K> getbyID(V id);
    public List<K> getbyIDUsingStoredProcedure(V id);
}
