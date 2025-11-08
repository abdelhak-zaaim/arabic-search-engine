package io.zaaim.arindexer.commun.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Index implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Map<String, Float>> index = new HashMap<>();

    private Set<Index> subIndexes;

    public void addEntry(String key, Map<String, Float> vector) {
        index.put(key, vector);
    }

    public void addSubIndex(Index subIndex) {
        subIndexes.add(subIndex);
    }

}
