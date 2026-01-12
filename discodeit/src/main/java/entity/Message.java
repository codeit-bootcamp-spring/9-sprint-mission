package entity;

import java.util.UUID;

public class Message {
    private UUID id;
    private String name;
    private String email;
    private Integer userNumeber;
    private Long createdAt;
    private Long updateAt;

    public UUID getId() {
        return id;
    }

    public String name() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Integer getUserNumeber() {
        return userNumeber;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public Message(UUID id, String email, String name, Integer userNumeber, Long createdAt, Long updateAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.userNumeber = userNumeber;
        this.createdAt = createdAt;
        this.updateAt = updateAt;


    }
}
