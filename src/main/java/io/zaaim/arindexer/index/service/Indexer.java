package io.zaaim.arindexer.index.service;

import io.zaaim.arindexer.model.Index;

import java.io.IOException;
import java.nio.file.Path;

public interface Indexer {
    Index createIndex(Path indexPath) throws IOException;
}
