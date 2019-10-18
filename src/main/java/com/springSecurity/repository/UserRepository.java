package com.springSecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.springSecurity.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {
	public User findByEmail(String email);

    @Query(value = "Select MD5(MD5(:password))", nativeQuery = true)
    public String encryptPassword(@Param(value = "password") String password);
	
}
