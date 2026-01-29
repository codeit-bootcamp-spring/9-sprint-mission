//package com.sprint.mission.discodeit.repository.jcf;
//
//import com.sprint.mission.discodeit.entity.ReadStatus;
//import com.sprint.mission.discodeit.repository.ReadStatusRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.*;
//
//@Repository
//public class JCFReadStatusRepository implements ReadStatusRepository {
//    // <channelId, <ReadStatusId, value>>
//    private Map<UUID, Map<UUID, ReadStatus>> readStatusMap;
//    // <ReadStatusId, channelId>
//    private Map<UUID, UUID> channelIdMap;
//
//    @Override
//    public void save(ReadStatus readStatus) {
//        UUID id = readStatus.getId();
//        UUID channelId = readStatus.getChannelId();
//
//        readStatusMap.computeIfAbsent(id, k->new HashMap<>())
//                .put(channelId, readStatus);
//        channelIdMap.put(id, channelId);
//
//    }
//
//    @Override
//    public boolean remove(UUID id) {
//        UUID channelId = channelIdMap.get(id);
//        readStatusMap.computeIfPresent(channelId
//                , (k, readStatuses) ->{
//            readStatuses.remove(id);
//            return readStatuses.isEmpty() ? null : readStatuses;
//        });
//        return false;
//    }
//
//    @Override
//    public ReadStatus findByID(UUID id) {
//        UUID channelId = channelIdMap.get(id);
//
//        return Optional.ofNullable(readStatusMap.get(channelId))
//                .map(m -> m.get(id))
//                .orElse(null);
//    }
//
//    @Override
//    public List<ReadStatus> findAll() {
//        return List.of();
//    }
//}
