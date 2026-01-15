package Service.jcf;

import Service.UserService;
import entity.User;
import exception.NotFoundException;

import java.util.*;

public class JCFUserService implements UserService {
    /*JCF 저장소
    ## List를 저장소로 쓸 경우 코드가 반복되면서 복잡도가 증가 : 결과 제공 시 사용
    ## 캡슐화를 위해서 >> 외부는 내부를 몰라도 됨
    ## List는 순서, id 없을 때, 단순 로그, 히스토리 / Map을 쓰는 이유는 id 기반 조회가 많기에 키값 활용
     */
    private final Map<UUID, User> data = new HashMap<>();

    //중복 검사 인덱스
    private final Set<String> emailIndex = new HashSet<>();
    private final Set<String> phoneIndex = new HashSet<>();

    //생성
    @Override
    public User create(String displayName, String email, String phoneNumber) {
        if (emailIndex.contains(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
        if (phoneIndex.contains(phoneNumber)) {
            throw new IllegalArgumentException("Phone number already exists: " + phoneNumber);
        }

       User user = new User(displayName, email, phoneNumber);

       data.put(user.getId(), user);
       emailIndex.add(email);
       phoneIndex.add(phoneNumber);

       return user;
    }

    //수정
    @Override
    public User update(UUID userId, String displayName, String email, String phoneNumber) {
        User user = data.get(userId);
        if (user == null) {
            throw new NotFoundException("User not found. id=" + userId);
        }

        String oldEmail = user.getEmail();
        String oldPhone = user.getPhoneNumber();

        // 이메일 바뀌는 경우에 중복 검사 + 인덱스 갱신
        if (email != null && !oldEmail.equals(email)) {
            if (emailIndex.contains(email)) {
                throw new IllegalArgumentException("Email already exists: " + email);
            }
            emailIndex.remove(oldEmail);
            emailIndex.add(email);
        }

        // 번호 바뀌는 경우에 중복 검사 + 인덱스 갱신
        if (phoneNumber != null && !oldPhone.equals(phoneNumber)) {
            if (phoneIndex.contains(phoneNumber)) {
                throw new IllegalArgumentException("Phone number already exists: " + phoneNumber);
            }
            phoneIndex.remove(oldPhone);
            phoneIndex.add(phoneNumber);
        }
        user.update(displayName, email, phoneNumber);
        return user;
    }

    //단건
    @Override
    public User findById(UUID userId) {
        return data.get(userId);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    //삭제
    @Override
    public void delete(UUID userId) {
        User user = data.remove(userId);

        if (user == null) {
            throw new NotFoundException("User not found. id=" + userId);
        }

//        boolean emailRemoved = false;
//        boolean phoneRemoved = false;
//
//        try {
//            emailRemoved = emailIndex.remove(user.getEmail());
//            phoneRemoved = phoneIndex.remove(user.getPhoneNumber());
//        } catch (Exception e) {
//            data.put(userId, user);
//
//            if (emailRemoved) emailIndex.add(user.getEmail());
//            if (phoneRemoved) phoneIndex.add(user.getPhoneNumber());
//
//            throw e; }
        emailIndex.remove(user.getEmail());
        phoneIndex.remove(user.getPhoneNumber());
    }

    //등록여부 확인
    @Override
    public boolean existsById(UUID userId) {
        return data.containsKey(userId);
    }
}
