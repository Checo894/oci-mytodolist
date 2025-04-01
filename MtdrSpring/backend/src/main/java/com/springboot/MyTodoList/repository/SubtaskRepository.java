package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Subtask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
@EnableTransactionManagement
public interface SubtaskRepository extends JpaRepository<Subtask, Long> {

    List<Subtask> findByMainTaskId(Long mainTaskId);

    List<Subtask> findByMainTaskIdAndCompleted(Long mainTaskId, boolean completed);

    // 🔸 Nuevos métodos con isActive = true
    List<Subtask> findByMainTaskIdAndIsActiveTrue(Long mainTaskId);

    List<Subtask> findByMainTaskIdAndCompletedAndIsActiveTrue(Long mainTaskId, boolean completed);

    List<Subtask> findByIsActiveTrue();  // Para obtener todas las subtareas activas

    List<Subtask> findByAssignedDeveloperIdAndIsActiveTrue(Long developerId);

    List<Subtask> findByTitle(String title);

    List<Subtask> findByTitleAndAssignedDeveloperId(String title, Long developerId);


}
