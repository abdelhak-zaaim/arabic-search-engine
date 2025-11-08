package io.zaaim.arindexer.commun.model;

import java.util.HashMap;
import java.util.Map;

public class Index {

    private Map<String, Map<String, Float>> index = new HashMap<>();
    private int documentCount = 0;

    public Map<String, Map<String, Float>> getIndex() {
        return index;
    }
}
