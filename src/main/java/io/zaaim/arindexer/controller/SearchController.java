package io.zaaim.arindexer.controller;

import io.helidon.common.http.MediaType;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.zaaim.arindexer.dto.response.SearchResponse;
import io.zaaim.arindexer.service.DocumentIndexService;
import io.zaaim.arindexer.service.impl.SearchImpl;
import io.zaaim.arindexer.util.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SearchController {
    private final DocumentIndexService indexService;


    public SearchController(DocumentIndexService indexService) {
        this.indexService = indexService;
    }

    public void configureRoutes(Routing.Builder routing) {
        routing.get("/search/{index}", this::search)
                .get("/search", this::searchDefault)
                .get("/indexes", this::getIndexes);
    }

    private void getIndexes(ServerRequest serverRequest, ServerResponse serverResponse) {
        try {
            List<String> indexes;
            try (var stream = Files.list(Constants.INDEXES_DIR)) {
                indexes = stream
                        .filter(Files::isRegularFile)
                        .map(path -> path.getFileName().toString())
                        .collect(Collectors.toList());
            }

            serverResponse.headers().contentType(MediaType.parse("application/json; charset=UTF-8"));
            serverResponse.send(indexes);
        } catch (IOException e) {
            serverResponse.status(500).send("Error retrieving indexes: " + e.getMessage());
        }
    }

    private void search(ServerRequest request, ServerResponse response) {
        String query = request.queryParams().first("q").orElse(null);
        if (query == null || query.isBlank()) {
            response.send("Missing query parameter 'q'. Example: /search?q=term");
            return;
        }
        int limit;
        try {
            limit = Integer.parseInt(request.queryParams().first("limit").orElse("5"));
        } catch (NumberFormatException e) {
            response.status(400).send("Invalid limit");
            return;
        }
        String index = request.path().param("index");

        Map<String, Float> rawResults = indexService.search(query, index, limit);

        Map<String, Float> hydrated = rawResults.entrySet().stream()
                .limit(limit)
                .collect(java.util.stream.Collectors.toMap(
                        e -> indexService.fetchContent(e.getKey()),
                        Map.Entry::getValue));

        SearchResponse searchResponse = new SearchResponse(query, index, limit, hydrated);
        response.headers().contentType(io.helidon.common.http.MediaType.parse("application/json; charset=UTF-8"));
        response.send(searchResponse);
    }
    private void searchDefault(ServerRequest request, ServerResponse response) {
        String index = request.queryParams().first("index").orElse(null);
        if (index == null || index.isBlank()) {
            response.send("Missing query parameter 'index'.");
            return;
        }

        String query = request.queryParams().first("q").orElse("");
        int limit = Integer.parseInt(request.queryParams().first("limit").orElse("5"));

        SearchImpl searchService = new SearchImpl();
        Map<String, Float> results = searchService.search(query, index, limit);

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

        SearchResponse searchResponse = new SearchResponse(query, index, limit,results);

        response.headers().contentType(MediaType.parse("application/json; charset=UTF-8"));
        response.send(searchResponse);
    }
}