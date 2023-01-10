package com.synchrony.userapp.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class MyUserDetails implements UserDetails {

  /**
   * serialVersionUID which is used for deserialization.
   */
  private static final long serialVersionUID = 1L;

  private String password;

  private String username;

  /**
   *
   * @param username
   * @param password
   */
  public MyUserDetails(final String username, final String password) {
    super();
    this.username = username;
    this.password = password;
  }

  /**
   * Returns the authorities granted to the user.
   * @return the authorities, sorted by natural key .
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }

  /**
   * Returns the password used to authenticate the user.
   * @return the password
   */
  @Override
  public String getPassword() {
    return password;
  }

  /**
   * Returns the username used to authenticate the user.
   *
   * @return the username.
   */
  @Override
  public String getUsername() {
    return username;
  }

  /**
   * Indicates whether the user's account has expired.
   *  An expired account cannot be authenticated.
   *
   * @return true if the user's account is valid, false if no longer valid.
   */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /**
   * Indicates whether the user is locked or unlocked. A locked user cannot be
   * authenticated.
   * @return true if the user is not locked,false otherwise
   */
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  /**
   * Indicates whether the user's credentials (password) has expired. Expired
   * credentials prevent authentication.
   * @return trueif the user's credentials are valid,
   * false if no longer valid (ie expired)
   */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /**
   * Indicates whether the user is enabled or disabled.
   *  A disabled user cannot be authenticated.
   * @return true if the user is enabled, false otherwise
   */
  @Override
  public boolean isEnabled() {
    return true;
  }

}
