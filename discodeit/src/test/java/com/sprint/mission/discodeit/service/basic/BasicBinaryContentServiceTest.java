package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
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
import org.springframework.test.util.ReflectionTestUtils;

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

  @Test
  @DisplayName("create 성공: 메타데이터 저장 후 스토리지 업로드를 수행한다")
  void create_success() {
    byte[] bytes = new byte[]{1, 2, 3};
    BinaryContentCreateRequest request = new BinaryContentCreateRequest("a.png", "image/png", bytes);

    BinaryContent saved = new BinaryContent("a.png", 3L, "image/png");
    UUID binaryContentId = UUID.randomUUID();
    ReflectionTestUtils.setField(saved, "id", binaryContentId);

    BinaryContentResponse expected = new BinaryContentResponse(binaryContentId, "a.png", 3L, "image/png");

    given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(saved);
    given(binaryContentMapper.toResponse(saved)).willReturn(expected);

    BinaryContentResponse actual = binaryContentService.create(request);

    assertSame(expected, actual);
    then(binaryContentRepository).should().save(any(BinaryContent.class));
    then(binaryContentStorage).should().put(eq(binaryContentId), eq(bytes));
    then(binaryContentMapper).should().toResponse(saved);
  }

  @Test
  @DisplayName("create 실패: 스토리지 업로드에서 예외가 발생하면 전파한다")
  void create_fail_storageError() {
    byte[] bytes = new byte[]{9, 8};
    BinaryContentCreateRequest request = new BinaryContentCreateRequest("b.png", "image/png", bytes);

    BinaryContent saved = new BinaryContent("b.png", 2L, "image/png");
    UUID binaryContentId = UUID.randomUUID();
    ReflectionTestUtils.setField(saved, "id", binaryContentId);

    given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(saved);
    given(binaryContentStorage.put(eq(binaryContentId), eq(bytes))).willThrow(
        new RuntimeException("storage error"));

    assertThrows(RuntimeException.class, () -> binaryContentService.create(request));

    then(binaryContentMapper).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("find 성공: ID로 조회한 파일 정보를 DTO로 반환한다")
  void find_success() {
    UUID binaryContentId = UUID.randomUUID();
    BinaryContent binaryContent = new BinaryContent("a.png", 3L, "image/png");
    BinaryContentResponse expected = new BinaryContentResponse(binaryContentId, "a.png", 3L, "image/png");

    given(binaryContentRepository.findById(binaryContentId)).willReturn(Optional.of(binaryContent));
    given(binaryContentMapper.toResponse(binaryContent)).willReturn(expected);

    BinaryContentResponse actual = binaryContentService.find(binaryContentId);

    assertSame(expected, actual);
  }

  @Test
  @DisplayName("find 실패: 파일이 없으면 BinaryContentNotFoundException이 발생한다")
  void find_fail_notFound() {
    UUID binaryContentId = UUID.randomUUID();

    given(binaryContentRepository.findById(binaryContentId)).willReturn(Optional.empty());

    assertThrows(BinaryContentNotFoundException.class, () -> binaryContentService.find(binaryContentId));

    then(binaryContentMapper).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("findAllByIdIn 성공: 조회된 목록을 DTO 목록으로 변환한다")
  void findAllByIdIn_success() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();

    BinaryContent content1 = new BinaryContent("a.png", 3L, "image/png");
    BinaryContent content2 = new BinaryContent("b.txt", 4L, "text/plain");

    BinaryContentResponse response1 = new BinaryContentResponse(id1, "a.png", 3L, "image/png");
    BinaryContentResponse response2 = new BinaryContentResponse(id2, "b.txt", 4L, "text/plain");

    given(binaryContentRepository.findAllByIdIn(List.of(id1, id2))).willReturn(List.of(content1, content2));
    given(binaryContentMapper.toResponse(content1)).willReturn(response1);
    given(binaryContentMapper.toResponse(content2)).willReturn(response2);

    List<BinaryContentResponse> result = binaryContentService.findAllByIdIn(List.of(id1, id2));

    assertEquals(List.of(response1, response2), result);
  }

  @Test
  @DisplayName("delete 성공: 파일이 존재하면 삭제한다")
  void delete_success() {
    UUID binaryContentId = UUID.randomUUID();
    BinaryContent binaryContent = new BinaryContent("a.png", 3L, "image/png");

    given(binaryContentRepository.findById(binaryContentId)).willReturn(Optional.of(binaryContent));

    binaryContentService.delete(binaryContentId);

    then(binaryContentRepository).should().delete(binaryContent);
  }

  @Test
  @DisplayName("delete 실패: 파일이 없으면 BinaryContentNotFoundException이 발생한다")
  void delete_fail_notFound() {
    UUID binaryContentId = UUID.randomUUID();

    given(binaryContentRepository.findById(binaryContentId)).willReturn(Optional.empty());

    assertThrows(BinaryContentNotFoundException.class, () -> binaryContentService.delete(binaryContentId));

    then(binaryContentRepository).should().findById(binaryContentId);
    then(binaryContentRepository).shouldHaveNoMoreInteractions();
  }
}

