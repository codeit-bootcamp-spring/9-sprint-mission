package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Category;
import com.sprint.mission.discodeit.repository.CategoryRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFCategoryRepository implements CategoryRepository {
    private final Map<UUID, Category> categoryMap = new ConcurrentHashMap<>();
    private final Map<String, Category> nameMap = new ConcurrentHashMap<>();

    private JCFCategoryRepository() {}
    private static class Holder {
        private static final JCFCategoryRepository INSTANCE = new JCFCategoryRepository();
    }
    public static JCFCategoryRepository getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void save(Category category) {
        categoryMap.put(category.getId(), category);
        nameMap.put(category.getName(), category);
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return Optional.ofNullable(categoryMap.get(id));
    }

    @Override
    public Optional<Category> findByName(String name) {
        return Optional.ofNullable(nameMap.get(name));
    }

    @Override
    public List<Category> findAll() {
        return new ArrayList<>(categoryMap.values());
    }

    @Override
    public void delete(UUID id) {
        Category removed = categoryMap.remove(id);
        if (removed != null) {
            nameMap.remove(removed.getName());
        }
    }
}