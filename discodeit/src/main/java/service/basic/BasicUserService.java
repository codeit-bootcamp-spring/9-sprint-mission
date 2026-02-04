package service.basic;

import entity.User;
import repository.UserRepository;
import repository.file.FileUserRepository;
import service.UserService;

import java.util.List;
import java.util.NoSuchElementException;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void addUser(User user) {
        try{
            if(userRepository.getUser(user.getUsername())!=null){
                throw new IllegalStateException("이미 존재하는 회원");

            }
        }catch(NoSuchElementException e){
            userRepository.addUser(user);
        }



    }

    @Override
    public User getUser(String username) {
        User user = userRepository.getUser(username);
        if(user==null){
            throw new NoSuchElementException("회원이 없습니다.: " + username);
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public boolean updateUser(User user) {
        return userRepository.updateUser(user);
    }

    @Override
    public boolean deleteUser(String Username) {
        return userRepository.deleteUser(Username);
    }
}
