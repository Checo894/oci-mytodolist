package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.DeveloperStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeveloperStatsRepository extends JpaRepository<DeveloperStats, Long> {
}
