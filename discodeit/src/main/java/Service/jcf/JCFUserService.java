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

    //생성
    @Override
    public User create(String displayName, String email, String phoneNumber) {
       User user = new User(displayName, email, phoneNumber);
       data.put(user.getId(), user);
       return user;
    }

    //수정
    @Override
    public User update(UUID userId, String displayName, String email, String phoneNumber) {
        User user = data.get(userId);
        if (user == null) {
            throw new NotFoundException("User not found. id= " + userId);
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
       맵 안 유저를 복사해 새로운 리스트 생성 후 돌려줌 /
        return List.of(); // 로직깨짐
        List<User> data.values(); // 런타임
        data.values(); // 내부 노출 -> findAll() 노출 방지
     */
    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    //삭제 - 호출한 쪽에서 매번 null체크 피하기 위해서 예외처리 사용
    @Override
    public void delete(UUID userId) {
        if (!data.containsKey(userId)) {
            throw new NotFoundException("User not found. id =" + userId);
        }
        data.remove(userId);
    }

    //등록여부 확인
    @Override
    public boolean exitsById(UUID userId) {
        return data.containsKey(userId);
    }
}
