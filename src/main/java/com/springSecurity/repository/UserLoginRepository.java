package com.springSecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springSecurity.entity.User;
import com.springSecurity.entity.UserLogin;

@Repository
public interface UserLoginRepository extends JpaRepository<UserLogin, Long> {
	public UserLogin findByUser(User user);
}
