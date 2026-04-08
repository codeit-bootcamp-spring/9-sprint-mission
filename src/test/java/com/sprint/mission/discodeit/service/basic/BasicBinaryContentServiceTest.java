package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicBinaryContentServiceTest {

    @Mock
    private BinaryContentRepository binaryContentRepository;
    @Mock
    private BinaryContentMapper binaryContentMapper;
    @Mock
    private BinaryContentStorage binaryContentStorage;

    @InjectMocks
    private BasicBinaryContentService binaryContentService;

    private final UUID contentId = UUID.randomUUID();

    @Test
    @DisplayName("파일 생성 성공")
    void create_success() {
        // given
        byte[] bytes = "file content".getBytes();
        BinaryContentCreateRequest request = new BinaryContentCreateRequest(
                "test.png", "image/png", bytes);
        BinaryContent binaryContent = new BinaryContent("test.png", (long) bytes.length, "image/png");
        BinaryContentDto expectedDto = new BinaryContentDto(contentId, "test.png", (long) bytes.length, "image/png");

        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(binaryContent);
        given(binaryContentMapper.toDto(binaryContent)).willReturn(expectedDto);

        // when
        BinaryContentDto result = binaryContentService.create(request);

        // then
        assertThat(result.fileName()).isEqualTo("test.png");
        assertThat(result.contentType()).isEqualTo("image/png");
        then(binaryContentStorage).should().put(any(), eq(bytes));
    }

    @Test
    @DisplayName("파일 단건 조회 성공")
    void find_success() {
        // given
        BinaryContent binaryContent = new BinaryContent("test.png", 1024L, "image/png");
        BinaryContentDto expectedDto = new BinaryContentDto(contentId, "test.png", 1024L, "image/png");

        given(binaryContentRepository.findById(contentId)).willReturn(Optional.of(binaryContent));
        given(binaryContentMapper.toDto(binaryContent)).willReturn(expectedDto);

        // when
        BinaryContentDto result = binaryContentService.find(contentId);

        // then
        assertThat(result.id()).isEqualTo(contentId);
    }

    @Test
    @DisplayName("파일 단건 조회 실패 - 없음")
    void find_fail_notFound() {
        // given
        given(binaryContentRepository.findById(contentId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> binaryContentService.find(contentId))
                .isInstanceOf(BinaryContentNotFoundException.class);
    }

    @Test
    @DisplayName("ID 목록으로 파일 조회")
    void findAllByIdIn_success() {
        // given
        List<UUID> ids = List.of(contentId);
        BinaryContent binaryContent = new BinaryContent("test.png", 1024L, "image/png");
        BinaryContentDto dto = new BinaryContentDto(contentId, "test.png", 1024L, "image/png");

        given(binaryContentRepository.findAllByIdIn(ids)).willReturn(List.of(binaryContent));
        given(binaryContentMapper.toDto(binaryContent)).willReturn(dto);

        // when
        List<BinaryContentDto> result = binaryContentService.findAllByIdIn(ids);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("파일 삭제 성공")
    void delete_success() {
        // given
        given(binaryContentRepository.existsById(contentId)).willReturn(true);

        // when
        binaryContentService.delete(contentId);

        // then
        then(binaryContentStorage).should().delete(contentId);
        then(binaryContentRepository).should().deleteById(contentId);
    }

    @Test
    @DisplayName("파일 삭제 실패 - 없음")
    void delete_fail_notFound() {
        // given
        given(binaryContentRepository.existsById(contentId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> binaryContentService.delete(contentId))
                .isInstanceOf(BinaryContentNotFoundException.class);
    }
}
