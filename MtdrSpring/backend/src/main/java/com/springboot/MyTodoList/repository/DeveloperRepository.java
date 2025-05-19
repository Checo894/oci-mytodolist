package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Developer;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeveloperRepository extends JpaRepository<Developer, Long> {
    Optional<Developer> findByEmail(String email);
    Optional<Developer> findByPhoneNumber(String phoneNumber);
}
