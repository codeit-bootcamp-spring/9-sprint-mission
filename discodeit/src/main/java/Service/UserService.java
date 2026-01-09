package Service;

import entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    //생성
    User create(String displayName, String email, String phoneNumber);

    //수정
    User update(UUID userId, String displayName, String email, String phoneNumber);

    //단건 조회
    User findById(UUID userId);

    //전체 조회
    List<User> findAll();

    //삭제
    void delete(UUID userId);

    //등록여부 확인
    boolean exitsById(UUID userId);
}
