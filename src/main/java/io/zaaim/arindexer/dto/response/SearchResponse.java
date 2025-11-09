package io.zaaim.arindexer.dto.response;

public record SearchResponse(String query, String[] filters, int limit,  java.util.Map<String, Float> results) {
}
