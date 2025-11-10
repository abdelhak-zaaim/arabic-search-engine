package io.zaaim.arindexer.model;

import io.zaaim.arindexer.storage.IndexFormat;
import io.zaaim.arindexer.storage.IndexStorageService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Index implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Map<String, Float>> indexMap;
    private transient IndexStorageService storage; // not serialized
    private Path indexPath;

    public Index(Map<String, Map<String, Float>> indexMap) {
        this.indexMap = new HashMap<>();
        for (Map.Entry<String, Map<String, Float>> entry : indexMap.entrySet()) {
            this.indexMap.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
    }

    public Index withStorage(IndexStorageService storage) {
        this.storage = storage;
        return this;
    }

    // Save to private storage by id and format
    public void saveToStorage(String id, IndexFormat format) {
        ensureStorage();
        Path path = storage.resolveIndex(id, format.extension());
        if (format == IndexFormat.XML) {
            saveToFileAsXml(path);
        } else {
            saveIndexToFile(path);
        }
        this.indexPath = path;
    }

    // Load from private storage by id and format
    public static Index loadFromStorage(IndexStorageService storage, String id, IndexFormat format) {
        Path path = storage.resolveIndex(id, format.extension());
        if (!Files.exists(path)) {
            throw new IllegalStateException("Index not found: " + path);
        }
        Index idx = (format == IndexFormat.XML) ? loadIndexFromXml(path) : loadIndexFromFile(path);
        idx.indexPath = path;
        idx.storage = storage;
        return idx;
    }

    // Legacy: load from arbitrary file path (kept for compatibility)
    public static Index fromFile(Path indexPath) {
        if (indexPath != null && indexPath.toFile().exists()) {
            Index index = indexPath.toString().endsWith(".xml")
                    ? loadIndexFromXml(indexPath)
                    : loadIndexFromFile(indexPath);
            index.indexPath = indexPath;
            return index;
        }
        throw new IllegalStateException("Index path is not set or file does not exist");
    }

    public Map<String, Map<String, Float>> getIndexMap() {
        return Collections.unmodifiableMap(indexMap);
    }

    public Path getIndexPath() {
        return indexPath;
    }

    // Fixed name: save serialized object
    public void saveIndexToFile(Path path) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(this);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save index to file: " + path, e);
        }
    }

    // Backward compatibility with old typo (optional)
    @Deprecated
    public void saveIndexTotoFile(Path path) {
        saveIndexToFile(path);
    }

    public static Index loadIndexFromFile(Path path) {
        try (var ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return (Index) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to load index from file: " + path, e);
        }
    }

    public void saveToFileAsXml(Path path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<index>\n");
            writer.write("  <entries>\n");
            for (Map.Entry<String, Map<String, Float>> entry : indexMap.entrySet()) {
                writer.write("    <entry key=\"" + escapeXml(entry.getKey()) + "\">\n");
                for (Map.Entry<String, Float> vector : entry.getValue().entrySet()) {
                    writer.write("      <vector term=\"" + escapeXml(vector.getKey()) +
                            "\" score=\"" + vector.getValue() + "\"/>\n");
                }
                writer.write("    </entry>\n");
            }
            writer.write("  </entries>\n");
            writer.write("</index>\n");
        } catch (IOException e) {
            throw new RuntimeException("Failed to save index as XML: " + path, e);
        }
    }

    private String escapeXml(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    public static Index loadIndexFromXml(Path path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            Map<String, Map<String, Float>> vector = new HashMap<>();
            String line;
            String currentKey = null;
            Map<String, Float> currentVector = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("<entry key=\"")) {
                    currentKey = extractAttribute(line, "key");
                    currentVector = new HashMap<>();
                } else if (line.startsWith("<vector term=\"")) {
                    String term = extractAttribute(line, "term");
                    String scoreStr = extractAttribute(line, "score");
                    Float score = Float.parseFloat(scoreStr);
                    if (currentVector != null) {
                        currentVector.put(term, score);
                    }
                } else if (line.startsWith("</entry>")) {
                    if (currentKey != null && currentVector != null) {
                        vector.put(currentKey, currentVector);
                        currentKey = null;
                        currentVector = null;
                    }
                }
            }
            return new Index(vector);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load index from XML: " + path, e);
        }
    }

    private static String extractAttribute(String line, String attributeName) {
        String pattern = attributeName + "=\"";
        int startIndex = line.indexOf(pattern);
        if (startIndex == -1) return null;
        startIndex += pattern.length();
        int endIndex = line.indexOf("\"", startIndex);
        if (endIndex == -1) return null;
        return unescapeXml(line.substring(startIndex, endIndex));
    }

    private static String unescapeXml(String text) {
        return text.replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&apos;", "'")
                .replace("&amp;", "&");
    }

    private void ensureStorage() {
        if (this.storage == null) {
            throw new IllegalStateException("IndexStorageService is not set. Call withStorage(...) first.");
        }
    }
}