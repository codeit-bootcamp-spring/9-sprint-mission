package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.MessageDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;



    @Override
    public Message create(MessageDto.CreateMessage createMessage) {
        if (!channelRepository.existsById(createMessage.channelId())) {
            throw new NoSuchElementException("Channel not found with id " + createMessage.channelId());
        }
        if (!userRepository.existsById(createMessage.authorId())) {
            throw new NoSuchElementException("Author not found with id " + createMessage.authorId());
        }
        Message message;
        if(createMessage.attachmentId()==null){
            message = new Message(
                    createMessage.content(),
                    createMessage.channelId(),
                    createMessage.authorId()
            );
        }else{
            message = new Message(
                    createMessage.content(),
                    createMessage.channelId(),
                    createMessage.authorId(),
                    createMessage.attachmentId()
            );
        }
        messageRepository.save(message);
        return message;
    }

    @Override
    public MessageDto.findMessage find(UUID messageId) {
        Message message= messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));


        List<MessageDto.fileInfo> binaryContentList = new ArrayList<>();
        if(message.getAttachmentId()!=null){
            for(UUID id:message.getAttachmentId()){
                binaryContentRepository.findById(id)
                        .ifPresent(binary->{
                            binaryContentList.add(new MessageDto.fileInfo(
                                    binary.getId(),
                                    binary.getFileName(),
                                    binary.getFilePath()
                            ));
                        });
            }

        }
        return new MessageDto.findMessage(
                message.getId(),
                message.getContent(),
                message.getAuthorId(),
                binaryContentList
        );

    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId){
        if(!channelRepository.existsById(channelId)){
            throw new NoSuchElementException("체널이 없습니다.");
        }
         return   messageRepository.findAll().stream()
                    .filter(m->m.getChannelId().equals(channelId))
                    .toList();


    }

    @Override
    public Message update(MessageDto.UpdateMessage updateMessage) {
        Message message = messageRepository.findById(updateMessage.Id())
                .orElseThrow(() -> new NoSuchElementException(updateMessage.Id() + "찾을수없습니다."));
        message.update(updateMessage.newContent(),updateMessage.newAttachmentId());
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID messageId) {
       Message message =  messageRepository.findById(messageId)
                       .orElseThrow(()->new NoSuchElementException("메시지가 없습니다."));
//
       if(message.getAttachmentId()!=null){
           for(UUID id:message.getAttachmentId()){
               binaryContentRepository.deleteById(id);
           }
      }



        messageRepository.deleteById(messageId);
    }
}
