package io.zaaim.arindexer.commun.model;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Index implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Map<String, Float>> index = new HashMap<>();

    private Map<String, Index> subIndexes;

    private Index parentIndex;

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

}
