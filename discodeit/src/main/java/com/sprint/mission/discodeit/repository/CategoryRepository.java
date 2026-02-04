package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Category;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {
    void save(Category category);
    Optional<Category> findById(UUID id);
    Optional<Category> findByName(String name);
    List<Category> findAll();
    void delete(UUID id);
}