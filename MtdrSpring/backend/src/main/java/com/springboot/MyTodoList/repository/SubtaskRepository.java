package com.springboot.MyTodoList.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.springboot.MyTodoList.model.Subtask;

@Repository
@Transactional
@EnableTransactionManagement
public interface SubtaskRepository extends JpaRepository<Subtask, Long> {

    List<Subtask> findByMainTaskId(Long mainTaskId);

    List<Subtask> findByMainTaskIdAndCompleted(Long mainTaskId, boolean completed);

    List<Subtask> findByMainTaskIdAndIsActiveTrue(Long mainTaskId);

    List<Subtask> findByMainTaskIdAndCompletedAndIsActiveTrue(Long mainTaskId, boolean completed);

    List<Subtask> findActiveSubtask();

    List<Subtask> findByAssignedDeveloperIdAndIsActiveTrue(Long developerId);

    List<Subtask> findByTitle(String title);

    List<Subtask> findByTitleAndAssignedDeveloperId(String title, Long developerId);


}
