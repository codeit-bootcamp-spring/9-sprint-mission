package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Category;
import com.sprint.mission.discodeit.repository.CategoryRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFCategoryRepository implements CategoryRepository {
    private final Map<UUID, Category> categoryMap = new ConcurrentHashMap<>();

    public JCFCategoryRepository() {}

    @Override
    public void save(Category category) {
        categoryMap.put(category.getId(), category);
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
        categoryMap.remove(id);
    }
}