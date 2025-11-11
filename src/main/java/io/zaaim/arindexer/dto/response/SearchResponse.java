package io.zaaim.arindexer.dto.response;

import java.util.Map;

public record SearchResponse(String query, String index, int limit, Map<String, Float> results) {
}
