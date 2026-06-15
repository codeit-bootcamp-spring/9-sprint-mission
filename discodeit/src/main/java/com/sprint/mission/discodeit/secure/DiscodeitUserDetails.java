package com.sprint.mission.discodeit.secure;

import com.sprint.mission.discodeit.dto.data.UserDto;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {

  private final UserDto userDto;
  private final String password;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(
        new SimpleGrantedAuthority("ROLE_" + userDto.role().name().toUpperCase())
    );
  }

  @Override
  public String getUsername() {
    return userDto.username();
  }

  @Override
  public String getPassword() {
    return password;
  }

  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DiscodeitUserDetails)) {
      return false;
    }
    DiscodeitUserDetails that = (DiscodeitUserDetails) o;
    return Objects.equals(userDto.id(), that.userDto.id());
  }

  @Override
  public int hashCode() {
    return Objects.hash(userDto.id());
  }

}
