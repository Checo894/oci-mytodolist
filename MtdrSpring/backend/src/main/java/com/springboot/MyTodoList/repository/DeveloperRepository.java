package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Developer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeveloperRepository extends JpaRepository<Developer, Long> {
    Developer findByEmail(String email);
    Developer findByPhoneNumber(String phoneNumber);
}
