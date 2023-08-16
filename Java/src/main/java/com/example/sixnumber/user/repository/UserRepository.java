package com.example.sixnumber.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.type.Status;
import com.example.sixnumber.user.type.UserRole;

public interface UserRepository extends JpaRepository<User, Long> {
	Boolean existsUserByEmail(String email);
	Boolean existsUserByNickname(String nickname);
	List<User> findAllByRoleAndPaymentDate(UserRole role, String localDateStr);
	Optional<User> findByEmail(String email);
	Optional<User> findByStatusAndEmail(Status status, String email);

	@Query("SELECT u FROM User u WHERE u.status = :status AND u.withdrawExpiration < CURRENT_DATE ")
	List<User> findByStatusAndWithdrawExpiration(@Param("status") Status status);
	@Query("SELECT u FROM User u WHERE u.chargingCount = :num")
	List<User> findUserByUntreated(@Param("num") int num);
}
