package com.springSecurity.config;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springSecurity.entity.User;

@SuppressWarnings("serial")
public class MyUserDetails implements UserDetails {

	private User user;
	private String token;

	@JsonIgnore
	private Collection<? extends GrantedAuthority> authorities;

	public MyUserDetails(User user) {
		this.user = user;
		String role = "ROLE_" + user.getRole().name();
		this.authorities = Arrays.asList(new SimpleGrantedAuthority(role));
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	@JsonIgnore
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {

		return user.getEmail();
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
