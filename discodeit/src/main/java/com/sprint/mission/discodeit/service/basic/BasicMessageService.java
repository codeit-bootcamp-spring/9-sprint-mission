package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;


    @Override //
    public Message create(MessageCreateRequest request) {
        if (!channelRepository.existsById(request.channelId())) {
            throw new NoSuchElementException("Channel not found");
        }
        if (!userRepository.existsById(request.authorId())) {
            throw new NoSuchElementException("Author not found");
        }
        Message message = new Message(
                request.content(),
                request.channelId(),
                request.authorId(),
                request.attachmentIds()
        );
        return messageRepository.save(message);
    }
/* 매개변수로 MessageCreateRequest를 받아서 만약에 request.channelId나 request.authorId가
userRepository.existsById 메서드로 못찾았을때 에러를 반환하고 에러가 없을시 request로 받은 값으로
새 메세지를 생성하고 messageRepository.save메서드의 리턴값을 반환한다
 */
    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message not found"));
    }
//매개변수로 MessageId를 받아서 messageRepository.findById메서드를 호출하고 리턴값으로 반환된
//반환된 메세지가 있으면 그 메세지를 반환하고 없을시 오류를 생성한다

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId);
    }
//매개변수로 받은 channelId를 사용해 특정 채널의 메시지 목록을 조회한다

    @Override
    public Message update(UUID messageId, MessageUpdateRequest request) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message not found"));

        message.update(request.content());
        return messageRepository.save(message);
    }
/* 매개변수로 messageId와 MessageUpdaterequest를 받아온다
messageRepository의 findById메소드에 messageId를 넣고 리턴값으로 반환된 메세지객체를 message에 넣는다
만약 매개변수로 입력한 messageId로 반환된 리턴값이 없으면 오류를 던진다
message의 content를 request로 받아온 content로 update 메소드를 통해 변경한다
messageRepository.save메서드의 리턴값을 반환한다
 */

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message not found"));

        if (message.getAttachmentIds() != null) {
            message.getAttachmentIds().forEach(id -> binaryContentRepository.deleteById(id));
        }

        messageRepository.deleteById(messageId);
    }
}
/* Messageid를 매개변수로 받아서 messageRepository.findById 메서드로 호출하고 리턴값으로 반환된
메세지가 있다면 message에 반환값을 담고 없을시 오류를 던진다
만약 message.getAttachmentIds가 null이 아니면 for문으로 돌면서 하나씩 돌때마다
binaryContentRepository.deleteById메서드를 통해서 지운다
messageRepository.deleteById로 메세지 아이디를 삭제한다
 */