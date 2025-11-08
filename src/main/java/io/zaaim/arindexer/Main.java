package io.zaaim.arindexer;

import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
import io.zaaim.arindexer.controller.IndexController;
import io.zaaim.arindexer.controller.SearchController;
import io.zaaim.arindexer.index.service.impl.ArabicIndexer;
import io.zaaim.arindexer.steemer.ArabicStemmerKhoja;
import io.zaaim.arindexer.util.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Main {

    public static void main(String[] args) throws Exception {
        Files.createDirectories(Constants.STORAGE_DIR);

        IndexController controller = new IndexController();
        SearchController searchController = new SearchController();

        Routing.Builder routingBuilder = Routing.builder();
        controller.configureRoutes(routingBuilder);
        searchController.configureRoutes(routingBuilder);
        Routing routing = routingBuilder.build();

        WebServer.builder()
                .routing(routing)
                .port(9001)
                .build()
                .start()
                .thenAccept(ws -> System.out.println(
                        "Server started at: http://localhost:" + ws.port()
                                + " | save dir: " + Constants.STORAGE_DIR));
    }
}