package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.data.UserDto;

import java.util.Collection;
import java.util.List;

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
    String roleName = userDto.role() != null ? userDto.role().name() : "USER";
    return List.of(
        new SimpleGrantedAuthority("ROLE_" + roleName)
    );
  }

  @Override
  public String getUsername() {
    return userDto.username();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}