package io.zaaim.arindexer.controller;

import io.helidon.common.http.MediaType;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WebController {

    public void configureRoutes(Routing.Rules rules) {
        rules.get("/", this::serveIndex)
             .get("/styles.css", this::serveCSS)
             .get("/app.js", this::serveJS);
    }

    private void serveIndex(ServerRequest req, ServerResponse res) {
        serveStaticFile(res, "web/index.html", "text/html");
    }

    private void serveCSS(ServerRequest req, ServerResponse res) {
        serveStaticFile(res, "web/styles.css", "text/css");
    }

    private void serveJS(ServerRequest req, ServerResponse res) {
        serveStaticFile(res, "web/app.js", "application/javascript");
    }

    private void serveStaticFile(ServerResponse res, String resourcePath, String contentType) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                res.status(404).send("File not found");
                return;
            }
            byte[] content = is.readAllBytes();
            res.headers().contentType(MediaType.parse(contentType));
            res.send(content);
        } catch (IOException e) {
            res.status(500).send("Error reading file: " + e.getMessage());
        }
    }
}