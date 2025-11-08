package io.zaaim.arindexer.commun.model;

import java.io.*;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Index implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Map<String, Float>> index = new HashMap<>();

    private Map<String, Index> subIndexes;

    private Index parentIndex;

    private Path indexPath;

    // Factory method to create Index from file path
    public static Index fromFile(Path indexPath) {
        // check if indexPath is set and load the index from file
        if (indexPath != null && indexPath.toFile().exists()) {
            // check if the path is xml or serialized object
            Index index;
            if (indexPath.toString().endsWith(".xml")) {
                index = loadIndexFromXml(indexPath);
            } else {
                index = loadIndexFromFile(indexPath);
            }

            index.indexPath = indexPath;
            return index;
        }
        throw new IllegalStateException("Index path is not set or file does not exist");
    }

    public void addEntry(String key, Map<String, Float> vector) {
        index.put(key, new HashMap<>(vector));
    }

    public void addSubIndex(String key, Index subIndex) {
        if (subIndexes == null) {
            subIndexes = new HashMap<>();
        }
        subIndex.parentIndex = this;
        subIndexes.put(key, subIndex);
    }

    public Map<String, Index> getSubIndexes() {
        return Collections.unmodifiableMap(subIndexes);
    }

    public Index getParentIndex() {
        return parentIndex;
    }

    public void saveIndexTotoFile(Path path) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(path.toFile()))) {
            oos.writeObject(this);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save index to file: " + path, e);
        }
    }

    public static Index loadIndexFromFile(Path path) {
        try (var ois = new ObjectInputStream(
                new FileInputStream(path.toFile()))) {
            return (Index) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to load index from file: " + path, e);
        }
    }

    public void saveToFileAsXml(Path path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<index>\n");

            // Write main index entries
            writer.write("  <entries>\n");
            for (Map.Entry<String, Map<String, Float>> entry : index.entrySet()) {
                writer.write("    <entry key=\"" + escapeXml(entry.getKey()) + "\">\n");
                for (Map.Entry<String, Float> vector : entry.getValue().entrySet()) {
                    writer.write("      <vector term=\"" + escapeXml(vector.getKey()) +
                            "\" score=\"" + vector.getValue() + "\"/>\n");
                }
                writer.write("    </entry>\n");
            }
            writer.write("  </entries>\n");

            // Write sub-indexes if they exist
            if (subIndexes != null && !subIndexes.isEmpty()) {
                writer.write("  <subIndexes>\n");
                for (Map.Entry<String, Index> subEntry : subIndexes.entrySet()) {
                    writer.write("    <subIndex name=\"" + escapeXml(subEntry.getKey()) + "\">\n");
                    writeIndexContent(writer, subEntry.getValue(), "      ");
                    writer.write("    </subIndex>\n");
                }
                writer.write("  </subIndexes>\n");
            }

            writer.write("</index>\n");
        } catch (IOException e) {
            throw new RuntimeException("Failed to save index as XML: " + path, e);
        }
    }

    private void writeIndexContent(BufferedWriter writer, Index idx, String indent) throws IOException {
        for (Map.Entry<String, Map<String, Float>> entry : idx.index.entrySet()) {
            writer.write(indent + "<entry key=\"" + escapeXml(entry.getKey()) + "\">\n");
            for (Map.Entry<String, Float> vector : entry.getValue().entrySet()) {
                writer.write(indent + "  <vector term=\"" + escapeXml(vector.getKey()) +
                        "\" score=\"" + vector.getValue() + "\"/>\n");
            }
            writer.write(indent + "</entry>\n");
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
            Index index = new Index();
            String line;
            Index currentSubIndex = null;
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
                        if (currentSubIndex != null) {
                            currentSubIndex.addEntry(currentKey, currentVector);
                        } else {
                            index.addEntry(currentKey, currentVector);
                        }
                        currentKey = null;
                        currentVector = null;
                    }
                } else if (line.startsWith("<subIndex name=\"")) {
                    String subIndexName = extractAttribute(line, "name");
                    currentSubIndex = new Index();
                    index.addSubIndex(subIndexName, currentSubIndex);
                } else if (line.startsWith("</subIndex>")) {
                    currentSubIndex = null;
                }
            }

            return index;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load index from XML: " + path, e);
        }
    }

    private static String extractAttribute(String line, String attributeName) {
        String pattern = attributeName + "=\"";
        int startIndex = line.indexOf(pattern);
        if (startIndex == -1) {
            return null;
        }
        startIndex += pattern.length();
        int endIndex = line.indexOf("\"", startIndex);
        if (endIndex == -1) {
            return null;
        }
        return unescapeXml(line.substring(startIndex, endIndex));
    }

    private static String unescapeXml(String text) {
        return text.replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&apos;", "'")
                .replace("&amp;", "&");
    }

}
