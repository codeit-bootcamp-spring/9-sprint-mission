package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.TestJpaConfig;
import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@Import(TestJpaConfig.class)
@ActiveProfiles("test")
class BinaryContentRepositoryTest {

    @Autowired
    private BinaryContentRepository binaryContentRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("ID 목록으로 BinaryContent를 조회한다")
    void findAllByIdIn() {
        BinaryContent bc1 = new BinaryContent("a.png", 100L, "image/png");
        BinaryContent bc2 = new BinaryContent("b.pdf", 200L, "application/pdf");
        BinaryContent bc3 = new BinaryContent("c.txt", 50L, "text/plain");
        em.persist(bc1);
        em.persist(bc2);
        em.persist(bc3);
        em.flush();
        em.clear();

        List<BinaryContent> result = binaryContentRepository.findAllByIdIn(
            List.of(bc1.getId(), bc2.getId()));

        assertThat(result).hasSize(2);
        assertThat(result).extracting(BinaryContent::getFileName)
            .containsExactlyInAnyOrder("a.png", "b.pdf");
    }

    @Test
    @DisplayName("빈 ID 목록으로 조회하면 빈 리스트를 반환한다")
    void findAllByIdIn_emptyList() {
        List<BinaryContent> result = binaryContentRepository.findAllByIdIn(List.of());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 ID 목록으로 조회하면 빈 리스트를 반환한다")
    void findAllByIdIn_nonExistent() {
        List<BinaryContent> result = binaryContentRepository.findAllByIdIn(
            List.of(UUID.randomUUID(), UUID.randomUUID()));

        assertThat(result).isEmpty();
    }
}
