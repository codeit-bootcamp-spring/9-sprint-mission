package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUpdatableEntity implements Serializable {

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private UUID profileId;

    // UserStatus와 1:1 양방향 매핑
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private UserStatus status;

    @Serial
    private static final long serialVersionUID = 1L;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void update(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void updateProfile(UUID profileId) {
        this.profileId = profileId;
    }

    public boolean isOnline() {
        return status != null && status.isOnline();
    }

    public void setStatus(UserStatus status) {
        this.status = status;
        if (status.getUser() != this) {
            status.setUser(this);
        }
    }
}