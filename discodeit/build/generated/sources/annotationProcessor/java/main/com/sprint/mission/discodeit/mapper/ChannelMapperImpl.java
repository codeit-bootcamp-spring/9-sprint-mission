package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-04T18:05:06+0900",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.11.1.jar, environment: Java 17.0.17 (Homebrew)"
)
@Component
public class ChannelMapperImpl extends ChannelMapper {

    @Override
    public ChannelDto toDto(Channel channel) {
        if ( channel == null ) {
            return null;
        }

        UUID id = null;
        ChannelType type = null;
        String name = null;
        String description = null;

        id = channel.getId();
        type = channel.getType();
        name = channel.getName();
        description = channel.getDescription();

        List<UserDto> participants = resolveParticipants(channel);
        Instant lastMessageAt = resolveLastMessageAt(channel);

        ChannelDto channelDto = new ChannelDto( id, type, name, description, participants, lastMessageAt );

        return channelDto;
    }
}
