package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.BinaryContent;
import com.sprint.mission.discodeit.dto.FileUploadRequest;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository; // 추가됨

    @Override
    public MessageResponse create(MessageCreateRequest request) {
        // [검증] 채널과 작성자가 존재하는지 확인
        if (!channelRepository.existsById(request.channelId())) {
            throw new NoSuchElementException("채널을 찾을 수 없습니다: " + request.channelId());
        }
        if (!userRepository.existsById(request.authorId())) {
            throw new NoSuchElementException("작성자를 찾을 수 없습니다: " + request.authorId());
        }

        // 1. 메시지 저장
        Message message = new Message(request.content(), request.channelId(), request.authorId());
        messageRepository.save(message);

        // 2. [선택] 첨부파일이 있다면 저장 (여러 개 가능)
        if (request.attachments() != null) {
            for (FileUploadRequest fileReq : request.attachments()) {
                BinaryContent attachment = new BinaryContent(
                        UUID.randomUUID(),
                        fileReq.data(),
                        fileReq.fileName(),
                        null,              // 프로필용 아님
                        message.getId(),   // 이 메시지에 첨부됨
                        Instant.now()
                );
                binaryContentRepository.save(attachment);
            }
        }

        return toResponse(message);
    }

    @Override
    public MessageResponse find(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("메시지를 찾을 수 없습니다: " + messageId));
        return toResponse(message);
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        // 전체 메시지 중 해당 채널의 메시지만 필터링
        return messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MessageResponse update(MessageUpdateRequest request) {
        Message message = messageRepository.findById(request.id())
                .orElseThrow(() -> new NoSuchElementException("메시지를 찾을 수 없습니다."));

        message.update(request.content());
        messageRepository.save(message);

        return toResponse(message);
    }

    @Override
    public void delete(UUID messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new NoSuchElementException("메시지를 찾을 수 없습니다.");
        }

        // 1. 관련된 첨부파일(BinaryContent) 먼저 삭제
        List<BinaryContent> attachments = binaryContentRepository.findAll().stream()
                .filter(bc -> messageId.equals(bc.getMessageId())) // 이 메시지의 파일들 찾기
                .toList();

        for (BinaryContent attachment : attachments) {
            binaryContentRepository.deleteById(attachment.getId());
        }

        // 2. 메시지 삭제
        messageRepository.deleteById(messageId);
    }

    // [변환기] Message -> MessageResponse DTO
    private MessageResponse toResponse(Message message) {
        // 이 메시지에 달린 첨부파일들의 ID 목록 조회
        List<UUID> attachmentIds = binaryContentRepository.findAll().stream()
                .filter(bc -> message.getId().equals(bc.getMessageId()))
                .map(BinaryContent::getId)
                .collect(Collectors.toList());

        return new MessageResponse(
                message.getId(),
                message.getContent(),
                message.getChannelId(),
                message.getAuthorId(),
                attachmentIds,
                message.getCreatedAt()
        );
    }
}