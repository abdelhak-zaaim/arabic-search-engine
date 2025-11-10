package io.zaaim.arindexer.storage;

public enum IndexFormat {
    SERIALIZED(".idx"),
    XML(".xml");

    private final String ext;

    IndexFormat(String ext) {
        this.ext = ext;
    }

    public String extension() {
        return ext;
    }
}