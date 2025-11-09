package io.zaaim.arindexer.controller;

import io.helidon.common.http.MediaType;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.zaaim.arindexer.dto.response.SearchResponse;
import io.zaaim.arindexer.service.impl.SearchImpl;
import io.zaaim.arindexer.util.Constants;

import java.awt.image.AreaAveragingScaleFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SearchController {

    public void configureRoutes(Routing.Builder routing) {
        routing.get("/search/{index}", this::search)
                .get("/search/{term}", this::searchByTerm);
    }

    private void search(ServerRequest request, ServerResponse response) {
        String query = request.queryParams().first("q").orElse(null);
        if (query == null || query.isBlank()) {
            response.send("Missing query parameter 'q'. Example: /search?q=term");
            return;
        }

        int limit = Integer.parseInt(request.queryParams().first("limit").orElse("5"));

        String index = request.path().param("index");
        String[] filters = index.split("/");
        SearchImpl searchService = new SearchImpl();

        Map<String, Float> results = searchService.search(query, filters, limit);

        results = results.entrySet().stream().map(entry -> {
            try {
                return new AbstractMap.SimpleEntry<>(
                        Files.readString(Path.of(Constants.STORAGE_DIR.resolve(entry.getKey()).toUri())),
                        entry.getValue()
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).limit(limit).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        SearchResponse searchResponse = new SearchResponse(query, filters, limit,results);

        response.headers().contentType(MediaType.parse("application/json; charset=UTF-8"));
        response.send(searchResponse);

    }

    private void searchByTerm(ServerRequest request, ServerResponse response) {
        String term = request.path().param("term");
        if (term == null || term.isBlank()) {
            response.send("Missing path parameter 'term'.");
            return;
        }
        // Implement your search logic here
        response.send("Searching for term: " + term);
    }
}