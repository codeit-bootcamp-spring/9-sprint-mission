package service.jcf;

import entity.User;
import exception.LoginFailedException;
import exception.UserNotFoundException;
import service.Usersevice;

import java.util.ArrayList;
import java.util.List;

public class JCFUserService implements Usersevice {

    private final List<User> data = new ArrayList<>();

    @Override
    public User addUser(User user) {
        data.add(user);
        return user;
    }

    @Override
    public List<User> getAllUser() {
        return data;
    }

    @Override
    public void deleteUser(String userId) {
        boolean removed = data.removeIf(user -> user.getUserId().equals(userId));
        if (!removed) {
            throw new UserNotFoundException("삭제 실패: 존재하지 않는 사용자입니다.");
        }
    }

    // 🔐 로그인
    @Override
    public User login(String identifier, String userNumber) {

        for (User user : data) {

            boolean idMatch =
                    user.getUserId().equals(identifier)
                            || user.getEmail().equals(identifier);

            boolean numberMatch =
                    user.getUserNumber().equals(userNumber);

            if (idMatch && numberMatch) {
                return user;
            }
        }

        throw new LoginFailedException();
    }

    // 아이디로 조회
    @Override
    public User getUserByUserId(String userId) {
        for (User user : data) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        throw new UserNotFoundException(
                "회원 조회 실패: 아이디에 해당하는 사용자가 없습니다."
        );
    }

    // 이메일로 조회
    @Override
    public User getUserByEmail(String email) {
        for (User user : data) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        throw new UserNotFoundException(
                "회원 조회 실패: 이메일에 해당하는 사용자가 없습니다."
        );
    }
}
