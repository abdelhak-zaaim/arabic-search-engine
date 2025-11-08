package io.zaaim.arindexer.controller;

import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.zaaim.arindexer.commun.model.Index;
import io.zaaim.arindexer.index.service.impl.ArabicIndexer;
import io.zaaim.arindexer.stemmer.ArabicStemmerKhoja;
import io.zaaim.arindexer.util.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class IndexController {

    public void configureRoutes(Routing.Builder routing) {
        routing
                .get("/greet", this::greet)
                .get("/startIndexing", this::startIndexing)
                .get("/stem/{word}", this::stem)
                .post("/{index}/save", this::save);
    }

    private void greet(ServerRequest req, ServerResponse res) {
        res.send("Hello World !");
    }

    private void startIndexing(ServerRequest req, ServerResponse res) {
        try {
            Index index = new ArabicIndexer().createIndex(Constants.STORAGE_DIR);
            index.saveToFileAsXml(Constants.STORAGE_DIR.resolve("index.xml"));
            res.send("Indexing started. Index saved to index.xml");
        } catch (IOException e) {
            res.status(500).send("Error starting indexing: " + e.getMessage());
        }
    }

    private void stem(ServerRequest request, ServerResponse response) {
        String word = request.path().param("word");
        if (word == null || word.isBlank()) {
            response.send("Missing path parameter 'word'.");
            return;
        }
        ArabicStemmerKhoja stemmer = new ArabicStemmerKhoja();
        String stemmed = stemmer.stem(word);
        response.send("Stemmed word: " + stemmed);
    }

    private void save(ServerRequest request, ServerResponse response) {
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
    }
}