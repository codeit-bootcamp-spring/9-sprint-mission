package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusService userStatusService;
  private final BinaryContentRepository binaryContentRepository;
  private final ChannelRepository channelRepository;

  @Override
  @Transactional
  public Optional<User> create(UserCreateRequest request,
      Optional<BinaryContentCreateRequest> profileRequest) {
    if (userRepository.existsByEmail(request.getEmail())) {
      return Optional.empty();
    }

    UUID profileId = profileRequest.map(pr -> {
      BinaryContent bc = new BinaryContent(pr.bytes(), pr.contentType(), pr.fileName(), pr.size());
      return binaryContentRepository.save(bc).getId();
    }).orElse(null);

    User user = new User(request.getUsername(), request.getEmail(), request.getPassword(),
        request.getPhoneNumber(), profileId);
    User savedUser = userRepository.save(user);
    userStatusService.create(savedUser.getId());

    return Optional.of(savedUser);
  }

  @Override
  @Transactional
  public Optional<User> update(UUID id, UserUpdateRequest request,
      Optional<BinaryContentCreateRequest> profileRequest) {
    return userRepository.findById(id).map(user -> {
      // 명세서 규격(newUsername, newEmail, newPassword) 반영
      if (request.newUsername() != null) {
        user.setUsername(request.newUsername());
      }
      if (request.newEmail() != null) {
        user.setEmail(request.newEmail());
      }
      if (request.newPassword() != null) {
        user.setPassword(request.newPassword());
      }

      profileRequest.ifPresent(pr -> {
        if (user.getProfileId() != null) {
          binaryContentRepository.deleteById(user.getProfileId());
        }
        BinaryContent bc = new BinaryContent(pr.bytes(), pr.contentType(), pr.fileName(),
            pr.size());
        user.setProfileId(binaryContentRepository.save(bc).getId());
      });

      user.recordUpdate();
      return userRepository.save(user);
    });
  }

  @Override
  @Transactional
  public boolean delete(UUID id) {
    return userRepository.findById(id).map(user -> {
      userStatusService.deleteByUserId(id);
      if (user.getProfileId() != null) {
        binaryContentRepository.deleteById(user.getProfileId());
      }

      channelRepository.findAll().forEach(channel -> {
        List<UUID> participants = channel.getParticipantIds();
        if (participants != null && participants.contains(id)) {
          List<UUID> mutableParticipants = new ArrayList<>(participants);
          mutableParticipants.remove(id);
          channel.setParticipantIds(mutableParticipants);
          channelRepository.save(channel);
        }
      });
      userRepository.deleteById(id);
      return true;
    }).orElse(false);
  }

  @Override
  public List<User> findAll() {
    return userRepository.findAll();
  }

  @Override
  public Optional<User> findById(UUID id) {
    return userRepository.findById(id);
  }
}