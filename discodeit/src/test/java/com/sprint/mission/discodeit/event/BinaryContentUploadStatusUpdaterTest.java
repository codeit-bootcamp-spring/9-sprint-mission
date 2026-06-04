package com.sprint.mission.discodeit.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BinaryContentUploadStatusUpdaterTest {

  @Mock
  private BinaryContentRepository binaryContentRepository;

  @InjectMocks
  private BinaryContentUploadStatusUpdater statusUpdater;

  @Test
  @DisplayName("update 성공: BinaryContent 업로드 상태를 변경한다")
  void update_success() {
    UUID binaryContentId = UUID.randomUUID();
    BinaryContent binaryContent = new BinaryContent("a.png", 3L, "image/png");

    given(binaryContentRepository.findById(binaryContentId)).willReturn(Optional.of(binaryContent));

    statusUpdater.update(binaryContentId, BinaryContentStatus.SUCCESS);

    assertEquals(BinaryContentStatus.SUCCESS, binaryContent.getStatus());
  }
}
