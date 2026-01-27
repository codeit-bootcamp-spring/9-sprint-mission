package main.java.com.sprint.mission.discodeit.service.file;


//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.service.UserService;

import main.java.com.sprint.mission.discodeit.entity.User;
import main.java.com.sprint.mission.discodeit.repository.file.FileUserRepository;
import main.java.com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileUserService implements UserService {
    private final FileUserRepository userRepository;

    public FileUserService() {
        this.userRepository = new FileUserRepository();
    }

    @Override
    public User create(String name, String email, String phoneNumber) {
        User user = new User(name, email, phoneNumber);

        return userRepository.save(user);
    }

    @Override
    public User findId(UUID Id) {
        return userRepository.findId(Id)
                .orElseThrow(() -> new NoSuchElementException("없는 유저입니다"));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }



    @Override
    public User update(UUID Id, String name, String email, String phoneNumber) {
        User user = findId(Id);
        user.update(name, email, phoneNumber);

        return userRepository.save(user);
    }

    @Override
    public void delete(UUID Id) {
        findId(Id);
        userRepository.deleteById(Id);
    }
}
