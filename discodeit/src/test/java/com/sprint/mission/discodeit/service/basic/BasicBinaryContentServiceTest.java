package com.sprint.mission.discodeit.service.basic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
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
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private BinaryContentStorage binaryContentStorage;
  @Mock
  private BinaryContentMapper binaryContentMapper;

  @InjectMocks
  private BasicBinaryContentService binaryContentService;

  @Test
  void findById_Success() {
    UUID id = UUID.randomUUID();
    BinaryContent content = mock(BinaryContent.class);

    BinaryContentDto response = mock(BinaryContentDto.class);

    given(binaryContentRepository.findById(any())).willReturn(Optional.of(content));

    given(binaryContentMapper.toDto(any())).willReturn(response);

    binaryContentService.findById(id);

    verify(binaryContentRepository).findById(any());
  }
}