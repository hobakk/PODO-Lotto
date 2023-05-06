package com.example.sixnumber.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sixnumber.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Boolean existsUserByEmail(String email);
	Boolean existsUserByNickname(String nickname);
	Optional<User> findByEmail(String email);
}
