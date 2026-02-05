package main.java.com.sprint.mission.discodeit.service;

import main.java.com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService { //인터페이스란 리모컨 전원 버튼의 작동원리는 알 필요없고, "전원 버튼을 누르면 화면이 켜진다"까지만 구현하는 것. 일단 이러한 기능이 필요하다 를 약속하기 위해. 그래서 "생성"은 무엇이고, "수정은"무엇이고,, 를 정의하고 있음
    //생성
    User create(String name, String email, String phoneNumber);
    //수정
    User update(UUID id, String name, String email, String phoneNumber); //수정할 때 id를 수정한다는 것이 아니고 일단 고유번호 id가 필요하니까
    //삭제
    void delete(UUID id); //사용자의 모든 정보(id, email, createdAt 등등 모든 정보를 지워야하는데 id만 지우길래 의아해함. 근데 여기는 interface니까 일단 모든 정보를 가리키는 고유한 id값만 받아와서 다른 클래스에서 delete메소드의 로직을 만들면 됨.
    //예시) 방법 A (id만 사용): 사서에게 "도서 번호 101번 책 지워주세요"라고 말합니다.
    //방법 B (모든 정보 사용): 사서에게 "제목은 '자바의 정석'이고, 저자는 '남궁성'이고, 페이지는 500쪽이고, 2024년에 출판된 그 책 지워주세요"라고 말합니다.
    //다건조회
    List<User> findAll();
    //단건 조회
    User findId(UUID id); //특정 id만 찾음


}