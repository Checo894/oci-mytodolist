package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Developer;
import com.springboot.MyTodoList.repository.DeveloperRepository;
import com.springboot.MyTodoList.util.HashUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeveloperService {

    @Autowired
    private DeveloperRepository developerRepository;

    public Optional<Developer> getById(Long id) {
        return developerRepository.findById(id);
    }

    public Developer getByEmail(String email) {
        return developerRepository.findByEmail(email).orElse(null);
    }

    public Developer createDeveloper(Developer developer) {
        developer.setPasswordHash(HashUtil.sha256(developer.getPasswordHash()));
        return developerRepository.save(developer);
    }

    public boolean authenticate(String email, String passwordPlaintext) {
        Developer developer = developerRepository.findByEmail(email).orElse(null);
        String hashedInput = HashUtil.sha256(passwordPlaintext);
        return developer != null && developer.getPasswordHash().equals(hashedInput);
    }    

    public String getRoleByEmail(String email) {
        Developer developer = developerRepository.findByEmail(email).orElse(null);
        return developer != null ? developer.getRole() : null;
    }
    
    public Developer getByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return null;
    
        // Quitar espacios, guiones, y asegurar que empiece con '+'
        String normalized = phoneNumber.replaceAll("[^\\d+]", "");
    
        // En caso de que venga sin el "+"
        if (!normalized.startsWith("+")) {
            normalized = "+" + normalized;
        }
    
        return developerRepository.findByPhoneNumber(normalized).orElse(null);
    }
    
    public Developer partialUpdateDeveloper(Long id, Developer updated) {
        Optional<Developer> devOpt = developerRepository.findById(id);
        if (devOpt.isPresent()) {
            Developer dev = devOpt.get();
            dev.setName(updated.getName());
            dev.setPhoneNumber(updated.getPhoneNumber());
            dev.setEmail(updated.getEmail());
            dev.setRole(updated.getRole());
            return developerRepository.save(dev);
        }
        return null;
    }
    
    public boolean deleteDeveloper(Long id) {
        Optional<Developer> devOpt = developerRepository.findById(id);
        if (devOpt.isPresent()) {
            developerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Developer> getAll() {
        return developerRepository.findAll();
    }
    
}
