package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Category;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryService {
    Optional<Category> save(Category category);
    Optional<Category> findById(UUID id);
    Optional<Category> findByName(String name);
    List<Category> findAll();
    void update(Category category);
    boolean delete(UUID id);
}