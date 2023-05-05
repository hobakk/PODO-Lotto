package com.example.sixnumber.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sixnumber.user.entity.Users;

public interface UserRepository extends JpaRepository<Users, Long> {

	Boolean existsUserByEmail(String email);
	Boolean existsUserByNickname(String nickname);
	Optional<Users> findUserByEmail(String email);
}
