package service.jcf;

import entity.User;
import service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    private final Map<UUID, User> data ;

    //생성자에서 초기화
    public JCFUserService(){
        this.data = new HashMap<>();
    }

    //멘토링 조언
    /*
    @Override
    public boolean addUser(User user) {
        //boolean guess = true;
        //boolean flag = data.containsKey(user.getId());
        //user객체를 다시 만들어줘야함
        //

        if(data.containsKey(user.getId())){
            System.out.println("이미 존재하는 유저입니다!");
            return false;
        }
        data.put(user.getId(),user);
        return true;

        서비스에서 중요한 데이터를 표현한게 엔티티

        adduser(데이터 개별적으로 생성) >>

        항상 올바른 데이터를 전달해주지 않을 수도 있기 때문에
        검증하는 단계를 서비스에서 진행

       실제로 만들어진 user 엔티티를 리턴해주는게 좋다
         */
    //예외처리 다른방법
        //boolean flag = data.add(user);
        //if(flag){ return user;}
        //else throw new Exception(); <<이런 방법도 있다 라고 참고만!
        // 익셉션을 활용하는게 더 좋음.

    //CREATE 생성
    @Override
    public User addUser(String displayName, String email, String phoneNumber){
        boolean flag = data.values().stream()
                .anyMatch(user -> user.getDisplayName().equals(displayName));

        if (flag) {
            throw new IllegalStateException("이미 존재하는 유저입니다!!");
        }
        //중복된 유저가 아니라면 입력받은 데이터를 USER 엔티티에 추가!
        User user = new User(displayName, email, phoneNumber);
        data.put(user.getId(), user);
        return user;
    }

    //READ 조회
    @Override
    public User getUser(UUID id) {
        return data.get(id);
    }

    @Override
    public List<User> getAllUser() {
        return new ArrayList<>(data.values());
    }

    //Update 업데이트
    @Override
    public void updateUser(UUID id, String displayName, String email, String phoneNumber) {
        User user = data.get(id);
        if(user!=null){
            user.updateUser(id,displayName,email,phoneNumber);
        }
    }

    @Override
    public void deleteUser(UUID id) {
       User user = data.remove(id);
        System.out.println("======= (DELETE)삭제된 유저 ======= \n" + user);

    }
}
