package entity;

import java.util.UUID;

public class Message {
    private UUID id;
    private String displayName;
    private String email;
    private String phoneNumber;
    private Long createdAt;
    private Long updateAt;

    public Message(String displayName, String email, String phoneNumber) {
        this.id = UUID.randomUUID();
        this.displayName = displayName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.createdAt = System.currentTimeMillis();
        this.updateAt = System.currentTimeMillis();
    }
}
