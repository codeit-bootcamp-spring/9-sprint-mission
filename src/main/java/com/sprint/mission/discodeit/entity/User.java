//사용자 정보를 표현하는 클래스 /재료

//사용자의 정보를 표현하는 클래스면 일단 정보가 뭐가 있는지를 알아야겠지
//UUID id, username, phone, email
//createdAt,updatedAt 이 두개는 Long으로 하라고 했음
//사용자 생성
//각 필드를 반환하는 getter함수와 각 필드를 수정하는 update함수를 쓰라고 했음

package com.sprint.mission.discodeit.entity;

import java.util.UUID;

//유저 정보 선언(필드)
public class User {
    private final UUID ID;
    private String username;
    private String email;
    private String phone;
    private final Long createdAt;
    private Long updatedAt;

    //생성자
    public User(String username, String email, String phone) {
        this.ID = UUID.randomUUID();
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }
    //getter /필드가 private가 선언됐기때문에 getter을 써야 외부에서 읽을수 있음
    public UUID getId() {return ID;}
    public String getUsername() {return username;}
    public String getEmail() {return email;}
    public String getPhone() {return phone;}
    public Long getCreatedAt() {return createdAt;}
    public Long getUpdatedAt() {return updatedAt;}

    //update /createdAt와 UUID는 고정된거라서 업데이트에 안써도 됨
    public void update(String username, String email, String phone){
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.updatedAt = System.currentTimeMillis(); //수정시점
    }
}