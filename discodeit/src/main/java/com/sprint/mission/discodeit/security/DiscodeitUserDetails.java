package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
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
    List<GrantedAuthority> authorities = new ArrayList<>();
    Role role = userDto.role();

    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));

    // 역할 계층 반영: ADMIN > CHANNEL_MANAGER > USER
    if (role == Role.ADMIN) {
      authorities.add(new SimpleGrantedAuthority("ROLE_CHANNEL_MANAGER"));
      authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
    } else if (role == Role.CHANNEL_MANAGER) {
      authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
    }

    return authorities;
  }

  @Override
  public String getUsername() {
    return userDto.username();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof DiscodeitUserDetails)) return false;
    DiscodeitUserDetails that = (DiscodeitUserDetails) o;
    return Objects.equals(getUsername(), that.getUsername());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getUsername());
  }
}