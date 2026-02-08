package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.DTO.UserDTO;
import com.sprint.mission.discodeit.DTO.UserServiceResponseDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.status.UserStatus;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FileUserService implements UserService {
    private final FileUserRepository repository;
    private final FileUserStatusRepository userStatusRepository;

    @Override
    public User createUser(UserDTO.CreateUserDTO dto) {
        User user = new User(dto.userName(), dto.email(), dto.password(), dto.profileImage());
        repository.save(user);
        return user;
    }

    @Override
    public UserServiceResponseDTO.FindUserId findByUserId(UserDTO.FindUserDTO dto) {
        User user = repository.findByUserId(dto.id())
                .orElseThrow(() -> new NoSuchElementException("User with id " + dto.id() + " not found"));
        UserStatus userStatus = userStatusRepository.findByUserId(dto.id())
                .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + dto.id() + " not found"));

        return new UserServiceResponseDTO.FindUserId(
                user.getUserId(),
                userStatus.isOnline(),
                userStatus.getLastUpdatedAt()
        );

    }

    @Override
    public List<UserDTO.FindUserDTO> findAllUser() {
        return repository.findAllUser().stream().map(user -> {
            UserStatus userStatus = userStatusRepository.findByUserId(user.getUserId())
                    .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + user.getUserId() + " not found"));

            return new UserDTO.FindUserDTO(user.getUserId(), userStatus.isOnline(), userStatus.getLastUpdatedAt());
                }
        ).toList();
    }

    @Override
    public User updateUser(UserDTO.updateUserDTO dto) {
        User user = repository.findByUserId(dto.id())
                .orElseThrow(() -> new NoSuchElementException("User with id " + dto.id() + " not found"));

        user.updateUserName(dto.userName());
        user.updateEmail(dto.email());
        user.updatePassword(dto.password());

        return repository.save(user);


    }

    @Override
    public boolean deleteUser(UserDTO.deleteDTO dto) {
        Optional<User> user = repository.findByUserId(dto.id());
        if (user == null) {
            throw new NoSuchElementException("User with id " + dto.id() + " not found");
        }
        repository.deleteById(dto.id());
        return true;
    }
}
