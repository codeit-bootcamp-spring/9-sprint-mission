package entity;

import java.util.UUID;

public class Channel {
    private UUID id;
    private String displayName;
    private String email;
    private String phoneNumber;
    private Long createdAt;
    private Long updatedAt;

    public Channel(String displayName, String email, String phoneNumber) {
        this.id = UUID.randomUUID();
        this.displayName = displayName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;

    }
}
