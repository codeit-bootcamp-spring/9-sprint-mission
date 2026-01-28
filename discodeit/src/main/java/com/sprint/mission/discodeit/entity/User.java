package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.UUID;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class User extends BaseEntity {
    private String displayName;
    private String email;
    private String password;
    private String phoneNumber;
    private UUID profileId;

    public User(String displayName, String email, String password, String phoneNumber) {
        super();
        this.displayName = displayName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }
}