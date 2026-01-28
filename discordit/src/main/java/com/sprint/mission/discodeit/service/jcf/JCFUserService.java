package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService2;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//구현체 생성
public class JCFUserService implements UserService2 {
    private final List<User> data = new ArrayList<>();
    //User만 담을 수 있는 전용 창고느낌,  관리용 list data에 저장하는 시스템 구축
    //ArrayList 데이터를 쌓아두기만 한상태 중복없이 구분하려면 고유id필요

    @Override
    //create(메서드) 저장,만들다
    // 매개변수(이름,이메일,폰번호)=> 사용자 생성을 위한 기초데이터 객체 User /캡슐화
    public User create(String displayName, String email, String phoneNumber) {

        try {
            User user = new User(displayName, email, phoneNumber);
            data.add(user);
            System.out.println("유저 등록 성공 : " + displayName);
            return user;

        } catch (Exception e) {
            System.out.println("유저 등록 실패 : " );
            return null;
        }
        // 리스트에 넣는도중 메모리가 찼거나 예상못한 에러가 나타날 수 있기때문에 try catch를 사용함(보호,위험대비)

    }
    // get가져오다 set설정하다
    // *for문 User 객체를 담고있는 data 리스트를 가져와서 하나하나 for문으로 꺼내서 User타입의 user안에 넣는다
    // 만약에 해당 user의 id값과 매개변수인 id값이 같을때 해당 user를 반환한다
    // 해당유저가 없을시 null(값이 없음) 반환한다
    @Override
    public User find(UUID id) {
        for(User user : data) {
            if (user.getId() == id){
                return user;
            }
        }
                return null;
    }

   //위에서 선언한 데이터 리스트를 반환한다
    @Override
    public List<User> findAll() {return new ArrayList<>(data);}

    //JavaApplication수정한 내용들이 update에 매개변수로 들어간다
    @Override
    public void update(UUID id,String displayName, String email, String phoneNumber) {
         for (User user : data) {
             if (user.getId() == id) {
                 user.update(displayName, email, phoneNumber);
                 //user.update는 Class User에 있는 update 메소드를 호출함
             }
         }
     }
     //데이터를 삭제할건데 조건에 맞으면
    //데이터안에 있는 유저객체들을 user로 선언을 하고 매개변수로 받은 id가 데이터안에있는 해당유저의 아이디와 같을시 삭제한다.
    //삭제여부를 bool안에 저장한다
     public boolean delete(UUID id) {
        boolean bool = data.removeIf(user -> id == user.getId());

        return bool;
    }
}


