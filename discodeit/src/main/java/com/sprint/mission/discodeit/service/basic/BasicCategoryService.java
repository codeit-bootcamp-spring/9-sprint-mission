package com.sprint.mission.discodeit.service.basic;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.sprint.mission.discodeit.entity.Category;
import com.sprint.mission.discodeit.repository.CategoryRepository;
import com.sprint.mission.discodeit.service.CategoryService;
import java.util.*;
@Service
@RequiredArgsConstructor
public class BasicCategoryService implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public Category save(Category category) {
        categoryRepository.save(category);
        return category;
    }

    @Override
    public Optional<Category> findById(UUID id) { return categoryRepository.findById(id); }

    @Override
    public Optional<Category> findByName(String name) { return categoryRepository.findByName(name); }

    @Override
    public List<Category> findAll() { return categoryRepository.findAll(); }

    @Override
    public void update(Category category) { categoryRepository.save(category); }

    @Override
    public boolean delete(UUID id) {
        if (categoryRepository.findById(id).isPresent()) {
            categoryRepository.delete(id);
            return true;
        }
        return false;
    }
}