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
        //생성할 때 중복성 검사를 명확하게 구현해보면 좋을듯?
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
    public User findById(UUID userid) {
        return data.get(userid);
    }

    //전체
    /* 키값 UUID, User / values는 뷰를 돌려줌. Map과 연결되어 서로 영향력
       맵 안 유저를 복사해 방어적 구조를 위해 만들게 됨.
       findAll은 내부 Map,List 디펜션을 위해 복사본을 만들어 조회기능만 제공한다는게 맞는 거 같은데..
        return List.of(); // 디펜션 의미가 없음
        List<User> data.values(); // 런타임 걸림
        data.values(); // 내부 노출 -> findAll() 노출 방지
     */
    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    /*  return List<User> data.values();
        data.values()는 Collection이지 List가 아니다.
        그래서 List로 반환하려면 반드시 새 List를 만들어야 한다 */
    }

    //삭제
    @Override
    public void delete(UUID userId) {
        User user = data.remove(userId);
        /*  원래는 user = data.get(userId);
            data.remove(userId) : 중복되는 느낌?
            remove도 어쨌든 값을 받아와서 처리하기에 두 번 구태여 쓸 필요 없을듯 */

        if (user == null) {
            throw new NotFoundException("User not found. id=" + userId);
        }
        /*인덱스 값들은 위에서 유저 고유값을 이미 하나만 뽑았기 때문에 그 유저의 고유값 하나만을 제거하고 전체 영향 없음.
          인덱스 자체가 고유값들의 목록이기에 찾아서 1개만 제거*/
        emailIndex.remove(user.getEmail());
        phoneIndex.remove(user.getPhoneNumber());
    }

    //등록여부 확인
    @Override
    public boolean exitsById(UUID userId) {
        return data.containsKey(userId);
    }
}
