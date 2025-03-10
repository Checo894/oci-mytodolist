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
}
