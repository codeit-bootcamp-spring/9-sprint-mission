package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.Instant;

public class JCFChannelService implements ChannelService {

    private final List<Channel> data;

    public JCFChannelService() {
        this.data = new ArrayList<>();
    }

    @Override
    public void create(Channel channel) {
        data.add(channel);
    }
    @Override
    public Channel findByName(String channelName) {
        for (Channel channel : data) {
            if (channel.getChannelName().equals(channelName)) return channel;
            }
        return null;
    }
    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public boolean update(UUID id, String channelName, String channelDescription, boolean isPrivate) {
        for (Channel c : data) {
            if (c.getId().equals(id)) {
                c.update(channelName, channelDescription, isPrivate);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(UUID id) {
        for (Channel c : data) {
            if (c.getId().equals(id)) {
                return data.remove(c);
            }
        }
        return false;
    }
    // =====================
// DTO 기반 신규 기능 (임시 구현: 컴파일 통과용)
// =====================

    @Override
    public ChannelResponse create(ChannelCreateRequest request) {
        // JCF 서비스에서는 실제 생성 로직을 아직 안 한다면,
        // 최소한 기존 create(Channel)로 우회해서 Channel을 만들고,
        // 응답 DTO로 변환해서 리턴한다.

        Channel channel = new Channel(
                request.name(),
                request.description(),
                request.isPrivate()
        );

        // 기존 CRUD 메서드 재사용
        create(channel);

        // JCF에서는 마지막 메시지 계산 안 함
        return new ChannelResponse(
                channel.getId(),
                channel.getChannelName(),
                channel.getChannelDescription(),
                channel.isPrivate(),
                null,
                null
        );
    }

    @Override
    public ChannelResponse find(UUID channelId) {
        // 기존 JCF는 id로 찾는 메서드가 없을 수 있으니,
        // findAll()에서 찾아서 응답으로 바꿔준다.
        for (Channel ch : findAll()) {
            if (ch.getId().equals(channelId)) {
                return toResponse(ch, null);
            }
        }
        return null;
    }

    @Override
    public List<ChannelResponse> findAllDto() {
        List<ChannelResponse> result = new ArrayList<>();
        for (Channel ch : findAll()) {
            result.add(toResponse(ch, null));
        }
        return result;
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        // JCF에서는 ReadStatusRepository 같은 연동을 아직 안 한다면
        // "PRIVATE 채널 필터링"을 못하니까,
        // 일단 전체 목록을 그대로 반환(임시).
        // (중요: BasicChannelService가 진짜 로직 담당)
        return findAllDto();
    }

    // =====================
// 내부 변환 유틸 (JCF용)
// =====================
    private ChannelResponse toResponse(Channel channel, Instant lastMessageAt) {
        // ChannelResponse가 record면 생성자 인자 순서가 중요함.
        // 너가 만든 ChannelResponse 필드 순서에 맞춰야 한다.

        // ✅ ChannelResponse는
        // (id, name, description, isPrivate, participants, lastMessageAt) 형태
        // participants는 JCF에서는 아직 못 구하니까 null로 둔다.
        return new ChannelResponse(
                channel.getId(),
                channel.getChannelName(),
                channel.getChannelDescription(),
                channel.isPrivate(),
                null,
                lastMessageAt
        );
    }

}
