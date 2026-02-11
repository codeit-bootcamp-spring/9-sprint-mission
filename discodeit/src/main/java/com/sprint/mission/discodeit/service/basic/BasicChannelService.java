package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
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
/*매개변수로 ChannelCreateRequest를 받아온다
channeltype이 private이고 이름과 설명은 null인 새 채널을 만든다
channelRepository.save 메소드에 channel을 넣고 리턴값으로 받아온 반환값을 savechannel에 담는다
만약에 매개변수로 받은 request의 memverIds가 null이 아닐때 for문을 실행한다
for문을 돌면서 각 객체의 UUID, savedChannel.getid(),null로 readStatus를 새로 생성해서  readStatusRepository.save
메서드로 저장한다. 리턴값으로 savedChannel 반환한다
 */

    @Override
    public List<Channel> findAllByUserId(UUID userId) {
        return channelRepository.findAll().stream()
                .filter(channel -> channel.containsUser(userId))
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
/* 매개변수로 channelId와 ChannelUpdaterequest를 받아온다
channelRepository의 findById메소드에 channelId를 넣고 리턴값으로 반환된 채널객체를 channel에 넣는다
만약 매개변수로 입력한 ChannelId로 반환된 리턴값이 없으면 오류를 던진다
channel의 이름과 설명을 request로 받아온 이름과 설명으로 update 메소드를 통해 변경한다
channelRepository.save메서드의 리턴값을 반환한다
 */
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
}
//매개변수로 ChannelId를 받아서 channelRepository.findById메서드를 호출하고 리턴값으로 반환된
//채널이 있으면 그 채널을 반환하고 없을시 오류를 생성한다