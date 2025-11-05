package io.zaaim.arindexer;

import io.helidon.webserver.WebServer;
import io.zaaim.arindexer.controller.SearchController;
import io.zaaim.arindexer.controller.WebUIController;
import io.zaaim.arindexer.service.IndexService;
import io.zaaim.arindexer.service.SearchService;
import safar.basic.morphology.stemmer.impl.ISRIStemmer;

public class Main {
    public static void main(String[] args) {
        // Initialize stemmer
        ISRIStemmer stemmer = new ISRIStemmer();

        // Initialize services
        IndexService indexService = new IndexService(stemmer);
        SearchService searchService = new SearchService(indexService);
        SearchController searchController = new SearchController(indexService, searchService);
        WebUIController webUIController = new WebUIController();

        // Build and start web server
        WebServer server = WebServer.builder()
                .port(8080)
                .routing(router -> router
                        // Web UI
                        .get("/", webUIController::serveIndex)
                        .get("/ui", webUIController::serveIndex)

                        // API - Documents
                        .post("/api/documents", searchController::addDocument)
                        .get("/api/indexes/{indexName}/documents", searchController::getDocuments)
                        .get("/api/indexes/{indexName}/documents/{docId}", searchController::getDocument)
                        .delete("/api/indexes/{indexName}/documents/{docId}", searchController::deleteDocument)

                        // API - Search
                        .get("/api/search", searchController::search)
                        .get("/api/search/related", searchController::getRelated)

                        // API - Indexes
                        .get("/api/indexes", searchController::listIndexes)
                        .get("/api/indexes/{indexName}", searchController::getIndexInfo)
                        .delete("/api/indexes/{indexName}", searchController::deleteIndex)
                        .post("/api/indexes/{indexName}/rebuild", searchController::rebuildIndex)
                )
                .build()
                .start();

        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                ║");
        System.out.println("║          🔍 Arabic Search Engine with TF-IDF                    ║");
        System.out.println("║                                                                ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.println("║                                                                ║");
        System.out.println("║  Server is running at: http://localhost:" + server.port() + "                           ║");
        System.out.println("║  Web UI:              http://localhost:" + server.port() + "/                           ║");
        System.out.println("║  API Docs:            http://localhost:" + server.port() + "/api/indexes                 ║");
        System.out.println("║                                                                ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.println("║  Features:                                                     ║");
        System.out.println("║  ✓ TF-IDF Vector Indexing                                      ║");
        System.out.println("║  ✓ Arabic Stemming (ISRI)                                      ║");
        System.out.println("║  ✓ Multiple Indexes Support                                    ║");
        System.out.println("║  ✓ Cosine Similarity Search                                    ║");
        System.out.println("║  ✓ Advanced Filtering                                          ║");
        System.out.println("║  ✓ Related Documents Discovery                                 ║");
        System.out.println("║  ✓ RESTful API                                                 ║");
        System.out.println("║  ✓ Web Interface (Arabic)                                      ║");
        System.out.println("║                                                                ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
    }
}
