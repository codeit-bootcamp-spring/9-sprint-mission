package com.sprint.mission.discodeit.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class BinaryContentUploadStatusUpdaterTest {

  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private BinaryContentMapper binaryContentMapper;
  @Mock
  private ApplicationEventPublisher eventPublisher;

  @InjectMocks
  private BinaryContentUploadStatusUpdater statusUpdater;

  @Test
  @DisplayName("update 성공: BinaryContent 업로드 상태를 변경한다")
  void update_success() {
    UUID binaryContentId = UUID.randomUUID();
    BinaryContent binaryContent = new BinaryContent("a.png", 3L, "image/png");

    given(binaryContentRepository.findById(binaryContentId)).willReturn(Optional.of(binaryContent));
    given(binaryContentMapper.toResponse(binaryContent)).willReturn(
        new BinaryContentResponse(binaryContentId, "a.png", 3L, "image/png", BinaryContentStatus.SUCCESS)
    );

    statusUpdater.update(binaryContentId, BinaryContentStatus.SUCCESS);

    assertEquals(BinaryContentStatus.SUCCESS, binaryContent.getStatus());
    then(eventPublisher).should().publishEvent(org.mockito.ArgumentMatchers.any(
        SseBinaryContentUpdatedEvent.class
    ));
  }
}
