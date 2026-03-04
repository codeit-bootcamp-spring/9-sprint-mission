package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.io.Serial;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@ToString(callSuper = true)
@Entity
@Table(name = "user_statuses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStatus extends BaseUpdatableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", columnDefinition = "uuid")
    private User user;

    @Column(columnDefinition = "timestamp with time zone", nullable = false)
    private Instant lastActiveAt;

    public UserStatus(User user){
        super();
        this.user = user;
        this.lastActiveAt = Instant.EPOCH;
        System.out.println("UserStatus 생성 - " + this.toString());
    }

    public void updateLastActiveAt(Instant time){
        this.lastActiveAt = time;
    }

    public boolean checkIsLogin(){
        if (this.lastActiveAt  == null){
            return false;
        }
        return lastActiveAt.isAfter(Instant.now().minus(Duration.ofMinutes(5)));
    }
}
