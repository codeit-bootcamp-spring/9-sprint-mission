package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;

    @Override
    public Optional<Channel> save(Channel channel) {
        // [의도 반영] 중복 체크 없이 바로 저장합니다.
        // 이름이 같더라도 새로운 UUID를 가진 독립된 채널로 생성됩니다.
        channelRepository.save(channel);
        log.info("채널 생성 완료: {} (ID: {})", channel.getName(), channel.getId());
        return Optional.of(channel);
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return channelRepository.findById(id);
    }

    @Override
    public Optional<Channel> findByName(String name) {
        return channelRepository.findByName(name);
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public void update(Channel channel) {
        if (channelRepository.findById(channel.getId()).isPresent()) {
            channelRepository.save(channel);
            log.info("채널 업데이트 완료: {}", channel.getName());
        }
    }

    @Override
    public boolean delete(UUID id) {
        if (channelRepository.findById(id).isPresent()) {
            channelRepository.delete(id);
            log.info("채널 삭제 완료 (ID: {})", id);
            return true;
        }
        return false;
    }
}