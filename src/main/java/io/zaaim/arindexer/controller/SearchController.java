package io.zaaim.arindexer.controller;

import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.zaaim.arindexer.service.impl.SearchImpl;

import java.util.List;

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

        List<String> results = searchService.search(query, filters, limit);

        response.send("Search results for query '" + query + "': " + results);

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