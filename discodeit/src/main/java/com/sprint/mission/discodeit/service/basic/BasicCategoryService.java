package com.sprint.mission.discodeit.service.basic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.sprint.mission.discodeit.entity.Category;
import com.sprint.mission.discodeit.repository.CategoryRepository;
import com.sprint.mission.discodeit.service.CategoryService;
import java.util.*;
@Service
@RequiredArgsConstructor
@Slf4j
public class BasicCategoryService implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public Optional<Category> save(Category category) {
        // [수정] 중복 체크 없이 바로 저장합니다.
        // 이름이 같아도 ID(UUID)가 다르면 다른 객체로 인정하는 'C++의 std::vector' 방식입니다.
        categoryRepository.save(category);
        log.info("카테고리 저장 완료: {} (ID: {})", category.getName(), category.getId());
        return Optional.of(category);
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