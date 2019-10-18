package com.springSecurity.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.springSecurity.config.MyUserDetails;
import com.springSecurity.entity.User;

@Service
public class UserDetails {
	public MyUserDetails getUserDetails() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication.getDetails() instanceof MyUserDetails) {
			return (MyUserDetails) authentication.getDetails();
		}
		if (authentication.getPrincipal() instanceof MyUserDetails) {
			return (MyUserDetails) authentication.getPrincipal();
		}
		return null;
	}

	public User getCurrentUser() {
		return getUserDetails().getUser();
	}

}
