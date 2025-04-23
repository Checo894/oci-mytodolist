package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.DeveloperSprintStats;
import com.springboot.MyTodoList.model.DeveloperSprintStatsId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeveloperSprintStatsRepository extends JpaRepository<DeveloperSprintStats, DeveloperSprintStatsId> {

    List<DeveloperSprintStats> findByDeveloperId(Long developerId);

    List<DeveloperSprintStats> findBySprintId(Long sprintId);

    List<DeveloperSprintStats> findByDeveloperIdAndSprintId(Long developerId, Long sprintId);

}
