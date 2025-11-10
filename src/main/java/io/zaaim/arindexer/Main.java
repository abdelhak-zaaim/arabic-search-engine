package io.zaaim.arindexer;

import io.helidon.media.jackson.JacksonSupport;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
import io.zaaim.arindexer.controller.IndexController;
import io.zaaim.arindexer.controller.SearchController;
import io.zaaim.arindexer.controller.WebController;
import io.zaaim.arindexer.service.DocumentIndexService;
import io.zaaim.arindexer.storage.SecureFileSystemStorageService;
import io.zaaim.arindexer.storage.StorageService;
import io.zaaim.arindexer.util.Constants;

import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws Exception {
        Files.createDirectories(Constants.STORAGE_DIR);

        Path privateDir = Path.of(System.getProperty("user.home"), ".arindexer", "data");
        StorageService storageService = new SecureFileSystemStorageService(privateDir);
        DocumentIndexService indexService = new DocumentIndexService(storageService);
        SearchController searchController = new SearchController(indexService);


        IndexController controller = new IndexController();
        WebController webController = new WebController();

        Routing.Builder routingBuilder = Routing.builder();
        controller.configureRoutes(routingBuilder);
        searchController.configureRoutes(routingBuilder);
        webController.configureRoutes(routingBuilder);

        Routing routing = routingBuilder.build();

        WebServer webServer = WebServer.builder().port(9001).addMediaSupport(JacksonSupport.create()).addRouting(routing).build();

        webServer.start().thenAccept(ws -> System.out.println("Web server is up! http://localhost:" + ws.port())).exceptionally(t -> {
            System.err.println("Startup failed: " + t.getMessage());
            t.printStackTrace(System.err);
            return null;
        });
    }
}