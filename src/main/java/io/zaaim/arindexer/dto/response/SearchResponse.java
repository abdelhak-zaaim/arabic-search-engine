package io.zaaim.arindexer.dto.response;

public record SearchResponse(String query, String index, int limit,  java.util.Map<String, Float> results) {
}
