package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.dto.data.UserDto;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
@Getter
@RequiredArgsConstructor

public class DiscodeitUserDetails implements UserDetails {
  private final UserDto userDto;
  private final String password;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + userDto.role().name()));
  }

  @Override
  public String getUsername() {
    return userDto.username();
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || getClass() != object.getClass()) return false;

    DiscodeitUserDetails that = (DiscodeitUserDetails) object;
    return Objects.equals(this.userDto.id(), that.userDto.id());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.userDto.id());
  }
  public UUID getId() {
    return this.userDto.id();
  }
}
