package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.any;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicBinaryContentServiceTest {

  @Mock
  BinaryContentRepository binaryContentRepository;

  @Mock
  BinaryContentMapper binaryContentMapper;

  @Mock
  BinaryContentStorage binaryContentStorage;

  @InjectMocks
  BasicBinaryContentService binaryContentService;

  @Test
  void create_success() {
    BinaryContentCreateRequest request = new BinaryContentCreateRequest("a.txt", "text/plain", new byte[]{1, 2});
    BinaryContentDto dto = new BinaryContentDto(UUID.randomUUID(), "a.txt", 2L, "text/plain");

    given(binaryContentMapper.toDto(any(BinaryContent.class))).willReturn(dto);

    BinaryContentDto result = binaryContentService.create(request);

    assertNotNull(result);
    then(binaryContentRepository).should().save(any(BinaryContent.class));
    then(binaryContentStorage).should().put(org.mockito.ArgumentMatchers.any(), any(byte[].class));
  }

  @Test
  void find_success() {
    UUID id = UUID.randomUUID();
    BinaryContent binary = new BinaryContent("a.txt", 2L, "text/plain");
    BinaryContentDto dto = new BinaryContentDto(id, "a.txt", 2L, "text/plain");

    given(binaryContentRepository.findById(id)).willReturn(Optional.of(binary));
    given(binaryContentMapper.toDto(binary)).willReturn(dto);

    BinaryContentDto result = binaryContentService.find(id);

    assertNotNull(result);
  }

  @Test
  void find_fail_notFound() {
    UUID id = UUID.randomUUID();
    given(binaryContentRepository.findById(id)).willReturn(Optional.empty());

    assertThrows(BinaryContentNotFoundException.class, () -> binaryContentService.find(id));
  }

  @Test
  void delete_success() {
    UUID id = UUID.randomUUID();
    given(binaryContentRepository.existsById(id)).willReturn(true);

    binaryContentService.delete(id);

    then(binaryContentRepository).should().deleteById(id);
  }

  @Test
  void delete_fail_notFound() {
    UUID id = UUID.randomUUID();
    given(binaryContentRepository.existsById(id)).willReturn(false);

    assertThrows(BinaryContentNotFoundException.class, () -> binaryContentService.delete(id));
  }

  @Test
  void findAllByIdIn_success() {
    UUID id = UUID.randomUUID();
    BinaryContent binary = new BinaryContent("a.txt", 2L, "text/plain");
    BinaryContentDto dto = new BinaryContentDto(id, "a.txt", 2L, "text/plain");

    given(binaryContentRepository.findAllById(List.of(id))).willReturn(List.of(binary));
    given(binaryContentMapper.toDto(binary)).willReturn(dto);

    List<BinaryContentDto> result = binaryContentService.findAllByIdIn(List.of(id));

    assertNotNull(result);
  }
}


