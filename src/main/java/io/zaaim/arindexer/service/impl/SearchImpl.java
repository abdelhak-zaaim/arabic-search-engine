package io.zaaim.arindexer.service.impl;

import io.zaaim.arindexer.model.Index;
import io.zaaim.arindexer.service.Search;
import io.zaaim.arindexer.util.Constants;

import java.lang.ref.WeakReference;
import java.util.List;

public class SearchImpl implements Search {
    WeakReference<Index> indexCache = new WeakReference<>(null);

    @Override
    public List<String> search(String query, String[] filters, int limit) {

        return List.of();
    }

}
