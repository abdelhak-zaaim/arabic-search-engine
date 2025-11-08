package io.zaaim.arindexer.service;

import io.helidon.common.http.HttpRequest;
import io.zaaim.arindexer.model.Index;

import java.util.List;

public interface Search {
    List<String> search(String query, String[] filters, int limit);
}
