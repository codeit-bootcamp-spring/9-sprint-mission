package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Category;
import com.sprint.mission.discodeit.repository.CategoryRepository;
import java.io.*;
import java.util.*;

public class FileCategoryRepository implements CategoryRepository {
    private final String FILE_PATH = "categories.ser";

    private FileCategoryRepository() {}
    private static class Holder {
        private static final FileCategoryRepository INSTANCE = new FileCategoryRepository();
    }
    public static FileCategoryRepository getInstance() {
        return Holder.INSTANCE;
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, Category> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Category>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();
        }
    }

    private void saveData(Map<UUID, Category> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Category category) {
        Map<UUID, Category> data = loadData();
        data.put(category.getId(), category);
        saveData(data);
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return Optional.ofNullable(loadData().get(id));
    }

    @Override
    public Optional<Category> findByName(String name) {
        return loadData().values().stream()
                .filter(c -> c.getName().equals(name))
                .findFirst();
    }

    @Override
    public List<Category> findAll() {
        return new ArrayList<>(loadData().values());
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, Category> data = loadData();
        data.remove(id);
        saveData(data);
    }
}