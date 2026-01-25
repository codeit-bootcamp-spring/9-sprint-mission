package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Category;
import com.sprint.mission.discodeit.service.CategoryService;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFCategoryService implements CategoryService {
    private final Map<UUID, Category> categoryMap = new ConcurrentHashMap<>();
    private final Map<String, Category> nameMap = new ConcurrentHashMap<>();

    private JCFCategoryService() {}

    private static class InstanceHolder {
        private static final JCFCategoryService INSTANCE = new JCFCategoryService();
    }

    public static JCFCategoryService getInstance() {
        return InstanceHolder.INSTANCE;
    }
    @Override
    public Category save(Category category) {
        categoryMap.put(category.getId(), category);
        nameMap.put(category.getName(), category);
        return category;
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
    public synchronized void update(Category newCategory) {
        if (!categoryMap.containsKey(newCategory.getId())) return;

        if (nameMap.containsKey(newCategory.getName()) &&
                !nameMap.get(newCategory.getName()).getId().equals(newCategory.getId())) {
            throw new IllegalStateException("이미 존재하는 카테고리 이름입니다.");
        }

        nameMap.entrySet().removeIf(entry -> entry.getValue().getId().equals(newCategory.getId()));
        categoryMap.put(newCategory.getId(), newCategory);
        nameMap.put(newCategory.getName(), newCategory);
    }

    @Override
    public boolean delete(UUID id) {
        Category removed = categoryMap.remove(id);
        if (removed != null) {
            nameMap.remove(removed.getName());
            return true;
        }
        return false;
    }
}