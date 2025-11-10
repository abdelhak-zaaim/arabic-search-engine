package io.zaaim.arindexer;

import io.helidon.media.jackson.JacksonSupport;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
import io.zaaim.arindexer.controller.IndexController;
import io.zaaim.arindexer.controller.SearchController;
import io.zaaim.arindexer.controller.WebController;
import io.zaaim.arindexer.util.Constants;

import java.nio.file.Files;

public class Main {

    public static void main(String[] args) throws Exception {
        Files.createDirectories(Constants.STORAGE_DIR);

        IndexController controller = new IndexController();
        SearchController searchController = new SearchController();
        WebController webController = new WebController();

        Routing.Builder routingBuilder = Routing.builder();
        controller.configureRoutes(routingBuilder);
        searchController.configureRoutes(routingBuilder);
        webController.configureRoutes(routingBuilder);

        Routing routing = routingBuilder.build();

        WebServer webServer = WebServer.builder().port(9001).addMediaSupport(JacksonSupport.create()).addRouting(routing).build();

        webServer.start().thenAccept(ws ->
                System.out.println("Web server is up! http://localhost:" + ws.port())
        ).exceptionally(t -> {
            System.err.println("Startup failed: " + t.getMessage());
            t.printStackTrace(System.err);
            return null;
        });
    }
}