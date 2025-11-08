package io.zaaim.arindexer.index.service;

import io.helidon.common.http.HttpRequest;
import io.zaaim.arindexer.commun.model.Index;

import java.io.IOException;
import java.nio.file.Path;

public interface Indexer {
    Index createIndex(Path indexPath) throws IOException;
}
