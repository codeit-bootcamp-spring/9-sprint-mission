package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Category;
import com.sprint.mission.discodeit.repository.CategoryRepository;
import org.springframework.stereotype.Repository;
import java.io.*;
import java.util.*;

@Repository
public class FileCategoryRepository implements CategoryRepository {
    private final String FILE_PATH = "categories.ser";
    private Map<UUID, Category> categoryMap;

    public FileCategoryRepository() {
        this.categoryMap = loadData();
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

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(categoryMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Category category) {
        categoryMap.put(category.getId(), category);
        saveData();
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return Optional.ofNullable(categoryMap.get(id));
    }

    @Override
    public Optional<Category> findByName(String name) {
        return categoryMap.values().stream()
                .filter(c -> c.getName().equals(name))
                .findFirst();
    }

    @Override
    public List<Category> findAll() {
        return new ArrayList<>(categoryMap.values());
    }

    @Override
    public void delete(UUID id) {
        if (categoryMap.remove(id) != null) {
            saveData();
        }
    }
}