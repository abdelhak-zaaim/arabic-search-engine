package io.zaaim.arindexer.service;

import io.helidon.common.http.HttpRequest;
import io.zaaim.arindexer.model.Index;

import java.util.List;
import java.util.Map;

public interface Search {
    Map<String, Float> search(String query, String index, int limit);
}
