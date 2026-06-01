package com.sprint.mission.discodeit.dto.data;

import java.util.Collection;
import java.util.Collections;
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

  // 조회용 더미 데이터 생성자
  public DiscodeitUserDetails(String username) {
    this.userDto = new UserDto(null, username, null, null, null, null);
    this.password = "";
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(
        new SimpleGrantedAuthority("ROLE_" + userDto.role())
    );
  }

  @Override
  public String getUsername() {
    return userDto.username();
  }

  @Override
  public boolean isAccountNonExpired() {
    return UserDetails.super.isAccountNonExpired();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DiscodeitUserDetails that = (DiscodeitUserDetails) o;
    return Objects.equals(userDto.username(), that.userDto.username());
  }

  @Override
  public int hashCode() {
    return Objects.hash(userDto.username());
  }
}
