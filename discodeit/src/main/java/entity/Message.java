package entity;

import java.util.UUID;

public class Message {
    private UUID id;
    private String email;
    private Integer UserNumeber;
    private Long createdAt;
    private Long updateAt;

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Integer getUserNumeber() {
        return UserNumeber;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public Message(UUID id, String email, Integer userNumeber, Long createdAt, Long updateAt) {
        this.id = id;
        this.email = email;
        UserNumeber = userNumeber;
        this.createdAt = createdAt;
        this.updateAt = updateAt;


    }
}
