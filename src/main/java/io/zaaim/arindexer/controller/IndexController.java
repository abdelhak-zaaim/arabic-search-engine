package io.zaaim.arindexer.controller;

import io.helidon.common.http.MediaType;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.zaaim.arindexer.model.Index;
import io.zaaim.arindexer.service.impl.ArabicIndexer;
import io.zaaim.arindexer.stemmer.ArabicStemmerKhoja;
import io.zaaim.arindexer.util.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IndexController {

    public void configureRoutes(Routing.Builder routing) {
        routing
                .get("/greet", this::greet)
                .get("/startIndexing", this::startIndexing)
                .get("/stem/{word}", this::stem)
                .post("/{index}/save", this::save)
                .post("/document/add", this::addDocument)
                .get("/documents", this::getDocuments);
    }

    private void greet(ServerRequest req, ServerResponse res) {
        res.send("Hello World !");
    }

    private void startIndexing(ServerRequest req, ServerResponse res) {
        try {
            Index index = new ArabicIndexer().createIndex(Constants.STORAGE_DIR);

            if (!Files.exists(Constants.INDEXES_DIR)) Files.createDirectories(Constants.INDEXES_DIR);
            String xmlIndexFileName = "index-" + System.currentTimeMillis() + ".xml";
            String serIndexFileName = "index-" + System.currentTimeMillis() + ".ser";

            index.saveToFileAsXml(Constants.INDEXES_DIR.resolve(xmlIndexFileName));
            index.saveIndexTotoFile(Constants.INDEXES_DIR.resolve(serIndexFileName));

            res.send("Indexing started. Index saved to: " + Constants.INDEXES_DIR.resolve(serIndexFileName));
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

    private void addDocument(ServerRequest request, ServerResponse response) {
        String fileName = request.queryParams().first("name").orElse(null);
        if (fileName == null || fileName.isBlank()) {
            response.status(400).send("{\"error\": \"Missing query parameter 'name'\"}");
            return;
        }

        String safeFileName = Paths.get(fileName).getFileName().toString();

        request.content().as(String.class)
                .thenAccept(content -> {
                    try {
                        Files.createDirectories(Constants.STORAGE_DIR);
                        Path target = Constants.STORAGE_DIR.resolve(safeFileName).normalize();

                        if (!target.startsWith(Constants.STORAGE_DIR)) {
                            response.status(400).send("{\"error\": \"Invalid file name\"}");
                            return;
                        }

                        Files.writeString(target, content,
                                StandardOpenOption.CREATE,
                                StandardOpenOption.TRUNCATE_EXISTING);

                        response.headers().contentType(MediaType.parse("application/json; charset=UTF-8"));
                        response.send("{\"message\": \"Document added successfully\", \"path\": \"" + target.getFileName() + "\"}");
                    } catch (IOException e) {
                        response.status(500).send("{\"error\": \"Error saving document: " + e.getMessage() + "\"}");
                    }
                })
                .exceptionally(ex -> {
                    response.status(500).send("{\"error\": \"Failed to read request body: " + ex.getMessage() + "\"}");
                    return null;
                });
    }

    private void getDocuments(ServerRequest request, ServerResponse response) {
        try {
            if (!Files.exists(Constants.STORAGE_DIR)) {
                Files.createDirectories(Constants.STORAGE_DIR);
            }

            List<Map<String, String>> documents;

            try (Stream<Path> paths = Files.walk(Constants.STORAGE_DIR)) {
                documents = paths
                        .filter(Files::isRegularFile)
                        .map(path -> {
                            Map<String, String> doc = new HashMap<>();
                            doc.put("name", path.getFileName().toString());
                            doc.put("path", Constants.STORAGE_DIR.relativize(path).toString());
                            try {
                                doc.put("size", String.valueOf(Files.size(path)));
                                doc.put("modified", Files.getLastModifiedTime(path).toString());
                                // Read first 200 chars as preview
                                String content = Files.readString(path);
                                doc.put("preview", content.substring(0, Math.min(200, content.length())));
                            } catch (IOException e) {
                                doc.put("size", "Unknown");
                                doc.put("modified", "Unknown");
                                doc.put("preview", "");
                            }
                            return doc;
                        })
                        .collect(Collectors.toList());
            }

            response.headers().contentType(MediaType.parse("application/json; charset=UTF-8"));
            response.send(documents);
        } catch (IOException e) {
            response.status(500).send("{\"error\": \"Error retrieving documents: " + e.getMessage() + "\"}");
        }
    }
}