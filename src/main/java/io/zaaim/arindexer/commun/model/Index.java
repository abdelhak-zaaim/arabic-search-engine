package io.zaaim.arindexer.commun.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Index implements Serializable {
    // add serialisation version
    private static final long serialVersionUID = 1L;

    private Map<String, Map<String, Float>> index = new HashMap<>();
    private int documentCount;

}
