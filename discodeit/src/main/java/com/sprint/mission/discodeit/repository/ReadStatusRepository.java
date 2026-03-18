package com.sprint.mission.discodeit.repository;


import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  //ReadStatus 엔티티에 User user객체가 들어있어서, id기준으로 찾아달라고 말하기위해 _ 사용
  List<ReadStatus> findAllByUser_Id(UUID userId);

  List<ReadStatus> findAllByChannel_Id(UUID channelId);

  void deleteAllByChannelId(UUID channelId);
}

//메세지목록같은경우는 데이터가 많아서 slice 하지만 읽음상태는 list로 충분