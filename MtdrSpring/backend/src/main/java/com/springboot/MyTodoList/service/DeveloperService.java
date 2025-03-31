package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Developer;
import com.springboot.MyTodoList.repository.DeveloperRepository;
import com.springboot.MyTodoList.util.HashUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeveloperService {

    @Autowired
    private DeveloperRepository developerRepository;

    public Optional<Developer> getById(Long id) {
        return developerRepository.findById(id);
    }

    public Developer getByEmail(String email) {
        return developerRepository.findByEmail(email);
    }

    public Developer createDeveloper(Developer developer) {
        developer.setPasswordHash(HashUtil.sha256(developer.getPasswordHash()));
        return developerRepository.save(developer);
    }

    public boolean authenticate(String email, String passwordPlaintext) {
        Developer developer = developerRepository.findByEmail(email);
        String hashedInput = HashUtil.sha256(passwordPlaintext);
        return developer != null && developer.getPasswordHash().equals(hashedInput);
    }    

    public String getRoleByEmail(String email) {
        Developer developer = developerRepository.findByEmail(email);
        return developer != null ? developer.getRole() : null;
    }
}
