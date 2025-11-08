package io.zaaim.arindexer;

import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
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

        Routing routing = Routing.builder()
                .get("/greet", (req, res) -> res.send("Hello World !"))
                .post("/{index}/save", (request, response) -> {
                    String index = request.path().param("index");
                    if (index == null || index.isBlank()) {
                        response.send("Missing path parameter 'index'.");
                        return;
                    }

                    String name = request.queryParams().first("name").orElse(null);
                    if (name == null || name.isBlank()) {
                        response.send("Missing query parameter 'name'. Example: /{index}/save?name=file.txt");
                        return;
                    }

                    // Sanitize to single path segments
                    String safeIndex = Paths.get(index).getFileName().toString();
                    String safeName = Paths.get(name).getFileName().toString();

                    request.content().as(String.class)
                            .thenAccept(text -> {
                                try {
                                    Path indexDir = Constants.STORAGE_DIR.resolve(safeIndex).normalize();
                                    if (!indexDir.startsWith(Constants.STORAGE_DIR)) {
                                        response.send("Invalid index.");
                                        return;
                                    }
                                    Files.createDirectories(indexDir);

                                    Path target = indexDir.resolve(safeName).normalize();
                                    if (!target.startsWith(indexDir)) {
                                        response.send("Invalid file name.");
                                        return;
                                    }

                                    Files.writeString(target, text,
                                            StandardOpenOption.CREATE,
                                            StandardOpenOption.TRUNCATE_EXISTING);

                                    response.send("Saved to: " + target);
                                } catch (IOException e) {
                                    response.send("Error saving file: " + e.getMessage());
                                }
                            })
                            .exceptionally(ex -> {
                                response.send("Failed to read request body: " + ex.getMessage());
                                return null;
                            });
                })
                .build();

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