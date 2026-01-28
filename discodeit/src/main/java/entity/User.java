package entity;

import java.io.Serializable;
import java.util.UUID;

import java.time.*;
import java.time.format.DateTimeFormatter;


public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final long createdAt;
    private long updatedAt;

    private String name = "";
    private String email = "";
    private String phoneNumber = "";

    public User(String name, String phoneNumber, String email){
        this.id = UUID.randomUUID();
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

    public void updateAll(String name, String phoneNumber, String email){
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        updateUpdateAt();
    }

    public void updateName(String name){
        this.name = name;
        updateUpdateAt();
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        updateUpdateAt();
    }

    public void updateEmail(String email) {
        this.email = email;
        updateUpdateAt();
    }

    public String toString(){
        String createAtToString = Instant.ofEpochMilli(this.createdAt)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return "USER) UUID: " + this.id + " | name: " + this.name + " | phone num: " + this.phoneNumber + " | e-mail: " + this.email
                + " | Created At: " + createAtToString;
    }

    public void update(String newName, String phoneNumber, String newEmail) {
        if (newName != null)
            this.name = newName;
        if (newEmail != null)
            this.email = newEmail;
        if (phoneNumber != null)
            this.phoneNumber = phoneNumber;
        updateUpdateAt();
    }
}
