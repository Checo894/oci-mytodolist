package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.DeveloperStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing DeveloperStats data from the database.
 */

@Repository
public interface DeveloperStatsRepository extends JpaRepository<DeveloperStats, Long> {
}
