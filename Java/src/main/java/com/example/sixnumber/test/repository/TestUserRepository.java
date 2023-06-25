package com.example.sixnumber.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sixnumber.test.entity.TestUser;

public interface TestUserRepository extends JpaRepository<TestUser, Long> {

}
