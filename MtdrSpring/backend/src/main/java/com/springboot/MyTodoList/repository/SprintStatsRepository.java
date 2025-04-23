package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.SprintStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SprintStatsRepository extends JpaRepository<SprintStats, Long> {
}
