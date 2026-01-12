package entity;

import java.util.UUID;

public class Channel {

    private UUID id;
    private String name;
    private String email;
    private String userNumber;
    private Long createdAt;
    private Long updatedAt;

    public Channel(String name, String description, String email) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.userNumber = description;

        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void update(String name, String description) {
        this.name = name;
        this.userNumber = description;
        this.updatedAt = System.currentTimeMillis();


    }
}






