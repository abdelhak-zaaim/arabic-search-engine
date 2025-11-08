package io.zaaim.arindexer.commun.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Index implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Map<String, Float>> index = new HashMap<>();

    private Map<String, Index> subIndexes;

    private Index parentIndex;

    public void addEntry(String key, Map<String, Float> vector) {
        index.put(key, new HashMap<>(vector));
    }

    public void addSubIndex(String key, Index subIndex) {
        if (subIndexes == null) {
            subIndexes = new HashMap<>();
        }
        subIndex.parentIndex = this;
        subIndexes.put(key, subIndex);
    }

}
