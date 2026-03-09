package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.UploadFileException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;
    private final PageResponseMapper pageResponseMapper;

    @Transactional
    @Override
    public MessageDto create(MessageCreateRequest request, List<BinaryContentCreateRequest> attachments) {

        Channel channel = channelRepository.findById(request.channelId()).orElseThrow();
        User author = userRepository.findById(request.authorId()).orElseThrow();

        List<BinaryContent> binaryContents = Collections.emptyList();

        if (!attachments.isEmpty()) {
            binaryContents = attachments.stream()
                .map(attachment -> {
                    BinaryContent newBinaryContent = new BinaryContent(
                        attachment.fileName(),
                        attachment.size(),
                        attachment.contentType()
                    );
                    binaryContentStorage.put(newBinaryContent.getId(), attachment.bytes());
                    return newBinaryContent;
                })
                .toList();
        }
        binaryContentRepository.saveAll(binaryContents);

        Message newMessage = new Message(
            channel,
            author,
            request.content(),
            binaryContents
        );
        messageRepository.save(newMessage);
        return messageMapper.toDto(newMessage);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        Message removeMessage = messageRepository.findById(id).orElseThrow();

        List<BinaryContent> attachments = removeMessage.getAttachments();
        if (!attachments.isEmpty()) {
          binaryContentRepository.deleteAll(attachments);
        }

        messageRepository.delete(removeMessage);
    }

    @Override
    public MessageDto findByID(UUID id) {
        return messageMapper.toDto(messageRepository.findById(id).orElseThrow());
    }

    @Override
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor,Pageable pageable) {
        int pageSize = pageable.getPageSize();
        Pageable limit = PageRequest.of(0, pageSize + 1);

        List<Message> messages = messageRepository.findAllByChannel_Id(channelId, cursor, limit);

        boolean hasNext = messages.size() > pageSize;
        List<Message> content = hasNext ? messages.subList(0, pageSize) : messages;

        Instant nextCursor = content.isEmpty() ? null :
            content.get(content.size() - 1).getCreatedAt();

        long totalElements = messageRepository.countByChannel_Id(channelId);

        return new PageResponse<>(
            content.stream().map(messageMapper::toDto).toList(),
            nextCursor,
            pageSize,
            hasNext,
            totalElements
        );
    }

    @Transactional
    @Override
    public MessageDto updateContent(UUID id, String newContent) {
        Message target = messageRepository.findById(id).orElseThrow();
        target.updateContent(newContent);
        return messageMapper.toDto(target);
    }
}
