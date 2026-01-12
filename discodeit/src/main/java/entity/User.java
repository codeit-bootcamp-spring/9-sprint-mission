package entity;

import java.util.UUID;

public class User {

    private final UUID id = UUID.randomUUID();
    private final long createdAt;
    private long updatedAt;

    private String name = "";
    private String email = "";
    private String phoneNumber = "";

    public User(String name, String phoneNumber, String email){
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.createdAt = this.updatedAt = System.currentTimeMillis();
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getName() {
        return name;
    }

    public void updateUpdateAt(){
        this.updatedAt = System.currentTimeMillis();
    }

    public void UpdateName(String name){
        this.name = name;
    }

    public void UpdatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void UpdateEmail(String email) {
        this.email = email;
    }
}
