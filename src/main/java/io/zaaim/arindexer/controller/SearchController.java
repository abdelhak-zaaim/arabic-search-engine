package io.zaaim.arindexer.controller;

import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;

public class SearchController {

    public void configureRoutes(Routing.Builder routing) {
        routing.get("/search", this::search)
                .get("/search/{term}", this::searchByTerm);
    }

    private void search(ServerRequest request, ServerResponse response) {
        String query = request.queryParams().first("q").orElse(null);
        if (query == null || query.isBlank()) {
            response.send("Missing query parameter 'q'. Example: /search?q=term");
            return;
        }
        // Implement your search logic here
        response.send("Searching for: " + query);
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