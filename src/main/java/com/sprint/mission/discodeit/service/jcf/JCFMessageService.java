package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@Profile("jcf")
@RequiredArgsConstructor
public class JCFMessageService implements MessageService {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    @Transactional
    public MessageDto create(MessageCreateRequest request, List<MultipartFile> attachments) {
        Channel channel = channelRepository.findById(request.channelId())
            .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));
        User author = userRepository.findById(request.authorId())
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        boolean hasContent = request.content() != null && !request.content().isBlank();
        boolean hasAttachments = attachments != null && attachments.stream().anyMatch(f -> !f.isEmpty());
        if (!hasContent && !hasAttachments) throw new IllegalArgumentException("메시지 내용이나 첨부파일이 필요합니다.");

        List<BinaryContent> savedAttachments = new ArrayList<>();
        if (attachments != null) {
            for (MultipartFile file : attachments) {
                if (!file.isEmpty()) {
                    try {
                        byte[] fileBytes = file.getBytes();
                        BinaryContent binaryContent = new BinaryContent(
                            file.getOriginalFilename(),
                            file.getSize(),
                            file.getContentType() != null ? file.getContentType() : "application/octet-stream"
                        );
                        binaryContentRepository.save(binaryContent);
                        binaryContentStorage.put(binaryContent.getId(), fileBytes);
                        savedAttachments.add(binaryContent);
                    } catch (IOException e) {
                        throw new RuntimeException("파일 저장 중 오류 발생", e);
                    }
                }
            }
        }

        Message message = new Message(channel, author, request.content() != null ? request.content() : "");
        savedAttachments.forEach(message::addAttachment);
        messageRepository.save(message);

        return messageMapper.toDto(message);
    }

    @Override
    public List<MessageDto> findAllByChannel_Id(UUID channelId) {
        channelRepository.findById(channelId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
        return messageRepository.findAllWithAttachmentsAndAuthorByChannel(channelId).stream()
            .map(messageMapper::toDto)
            .toList();
    }

    @Override
    @Transactional
    public MessageDto update(UUID messageId, MessageUpdateRequest request) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));
        message.updateContent(request.newContent());
        messageRepository.save(message);
        return messageMapper.toDto(message);
    }

    @Override
    @Transactional
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));
        binaryContentRepository.deleteAll(message.getAttachments());
        messageRepository.delete(message);
    }

    @Override
    public Slice<MessageDto> getMessages(UUID channelId, Pageable pageable) {
        Slice<Message> slice = messageRepository.findByChannel_Id(channelId, pageable);
        return slice.map(messageMapper::toDto);
    }

}