package io.zaaim.arindexer.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Constants {
    // Configure base directory with -Dsave.dir=/absolute/or/relative/path (defaults to saved-texts)

    public static final Path STORAGE_DIR = Paths.get(System.getProperty("save.dir", "saved-texts")).toAbsolutePath();

}
