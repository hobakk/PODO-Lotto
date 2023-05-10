package com.example.sixnumber.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.type.UserRole;

public interface UserRepository extends JpaRepository<User, Long> {

	Boolean existsUserByEmail(String email);
	Boolean existsUserByNickname(String nickname);
	List<User> findByRole(UserRole role);
	Optional<User> findByEmail(String email);
}
