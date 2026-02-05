package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.io.*;
import java.util.*;

public class FileBinaryContentRepository implements BinaryContentRepository {

    private final String path;
    private Map<UUID, BinaryContent> store;

    public FileBinaryContentRepository() {
        this("data/binary-content.ser");
    }

    public FileBinaryContentRepository(String path) {
        this.path = path;
        this.store = load(path);
    }

    @SuppressWarnings("unchecked")
    private static Map<UUID, BinaryContent> load(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof Map<?, ?> map) {
                return (Map<UUID, BinaryContent>) map;
            }
            return new HashMap<>();
        } catch (EOFException e) {
            return new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to load file store: " + path, e);
        }
    }

    private void flush() {
        File file = new File(path);
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(store);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file store: " + path, e);
        }
    }

    @Override
    public synchronized BinaryContent save(BinaryContent binaryContent) {
        store.put(binaryContent.getId(), binaryContent);
        flush();
        return binaryContent;
    }

    @Override
    public synchronized Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public synchronized void delete(UUID id) {
        store.remove(id);
        flush();
    }

    @Override
    public synchronized boolean existsById(UUID id) {
        return store.containsKey(id);
    }

    @Override
    public synchronized List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<BinaryContent> result = new ArrayList<>();
        for (UUID id : ids) {
            BinaryContent bc = store.get(id);
            if (bc != null) {
                result.add(bc);
            }
        }
        return result;
    }
}
