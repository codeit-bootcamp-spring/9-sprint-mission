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
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
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
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
        log.info("메시지 생성 시도: channelId={}, authorId={}", request.channelId(), request.authorId());
        Channel channel = channelRepository.findById(request.channelId())
            .orElseThrow(() -> {
                log.warn("메시지 생성 실패: 존재하지 않는 채널 ID={}", request.channelId());
                return new ChannelNotFoundException(request.channelId());
            });
        User author = userRepository.findById(request.authorId())
            .orElseThrow(() -> {
                log.warn("메시지 생성 실패: 존재하지 않는 사용자 ID={}", request.authorId());
                return new UserNotFoundException(request.authorId());
            });

        List<BinaryContent> binaryContents = Collections.emptyList();

        if (!attachments.isEmpty()) {
            log.debug("메시지 첨부 파일 저장 시작: count={}", attachments.size());
            List<BinaryContent> finalBinaryContents = attachments.stream()
                .map(a -> new BinaryContent(a.fileName(), a.size(), a.contentType()))
                .toList();

            binaryContentRepository.saveAll(finalBinaryContents);
            binaryContents = finalBinaryContents;

            for (int i = 0; i < attachments.size(); i++) {
                BinaryContent entity = binaryContents.get(i);
                byte[] fileData = attachments.get(i).bytes();

                try {
                    binaryContentStorage.put(entity.getId(), fileData);
                } catch (Exception e) {
                    log.error("파일 저장 실패: {}", entity.getFileName());
                    throw e;
                }
            }
        }

        Message newMessage = new Message(
            channel,
            author,
            request.content(),
            binaryContents
        );
        messageRepository.save(newMessage);
        log.info("메시지 생성 완료: messageId={}", newMessage.getId());
        return messageMapper.toDto(newMessage);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        log.info("메시지 삭제 시도: messageId={}", id);
        Message removeMessage = messageRepository.findById(id).orElseThrow(() -> {
            log.warn("삭제 실패: 존재하지 않는 메시지 ID={}", id);
            return new MessageNotFoundException(id);
        });

        List<BinaryContent> attachments = removeMessage.getAttachments();
        if (!attachments.isEmpty()) {
            log.debug("메시지 첨부 파일 삭제: count={}", attachments.size());
          binaryContentRepository.deleteAll(attachments);
        }

        messageRepository.delete(removeMessage);
        log.info("메시지 삭제 성공: messageId={}", id);
    }

    @Override
    public MessageDto findByID(UUID id) {
        return messageMapper.toDto(messageRepository.findById(id).orElseThrow(() ->
            new MessageNotFoundException(id)));
    }

    @Override
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor, Pageable pageable) {
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
        log.info("메시지 수정 시도: messageId={}", id);
        Message target = messageRepository.findById(id).orElseThrow(() -> {
            log.warn("수정 실패: 존재하지 않는 메시지 ID={}", id);
            return new MessageNotFoundException(id);
        });
        target.updateContent(newContent);
        log.debug("메시지 수정 완료: messageId={}", id);
        return messageMapper.toDto(target);
    }
}
