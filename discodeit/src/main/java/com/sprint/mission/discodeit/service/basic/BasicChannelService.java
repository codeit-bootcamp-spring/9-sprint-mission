package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;


    @Override
    public Channel createPublic(ChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
        return channelRepository.save(channel);
    }
//매개변수로 받아온 ChannelCreateRequest의 이름과 설명을 가지고 Channeltype은 퍼블릭으로 새채널을 만들어서
//channel에 담고 channelrepository의 save 메서드에 매개변수로 channel을 입력하여 리턴값으로 save 메서드의 리턴값을 반환한다

    @Override
    public Channel createPrivate(ChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        Channel savedChannel = channelRepository.save(channel);

        if (request.memberIds() != null) {
            request.memberIds().forEach(userId -> {
                ReadStatus readStatus = new ReadStatus(userId, savedChannel.getId(), null);
                readStatusRepository.save(readStatus);
            });
        }
        return savedChannel;
    }


    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        return channelRepository.findAll().stream()
                .filter(channel -> {
                    if (channel.getType() == ChannelType.PUBLIC) return true;

                    return readStatusRepository.findAllByChannelId(channel.getId()).stream()
                            .anyMatch(rs -> rs.getUserId().equals(userId));
                })
                .map(this::toDto)
                .toList();
    }
/*매개변수로 userId를 받아온다. 매개변수로 받아온 userId로 Chnnel에 있는 모든 데이터를 가져와서 스트림 형식으로 바꾼다
.filter부분에서 chaanel객체를 하나씩 꺼내서 containsUser메소드를 이용해서 userid로 추출한 정보를 Channel로 변환하고 리스트 형식으로 바꾼다
*/

    @Override
    public Channel update(UUID channelId, ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel not found"));

        channel.update(request.name(), request.description());
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel not found");
        }
        channelRepository.deleteById(channelId);
    }
/*channelId를 매개변수로 받아서 channelRepository.existsById메서드의 매개변수에 채널아이디를 널어주고
리턴값이 false일때 오류를 반환한다 아닐시 channelRepository.deleteById메서드의 매개변수에 채널 아이디를 넣어서 호출한다
 */
    @Override
    public Channel find(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    }

    private ChannelDto toDto(Channel channel) {
        // 참여자 명단 가져오기 (비공개 채널일 때만 ReadStatus를 뒤져서 가져옴)
        List<UUID> participantIds = new ArrayList<>();
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            readStatusRepository.findAllByChannelId(channel.getId()).stream()
                    .map(ReadStatus::getUserId)
                    .forEach(participantIds::add);
        }

        // 마지막 메시지 시간 찾기 (메시지가 없으면 아주 옛날 시간으로 설정)
        Instant lastMessageAt = messageRepository.findAllByChannelId(channel.getId())
                .stream()
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                .map(Message::getCreatedAt)
                .findFirst()
                .orElse(Instant.MIN);

        return new ChannelDto(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                participantIds,
                lastMessageAt
        );
    }
}
