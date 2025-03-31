package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.ToDoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
@EnableTransactionManagement
public interface ToDoItemRepository extends JpaRepository<ToDoItem, Long> {

    List<ToDoItem> findByStatus(String status);

    List<ToDoItem> findByProgressGreaterThanEqual(double progress);

    // 🔄 Nuevos métodos con isActive = true
    List<ToDoItem> findByIsActiveTrue();

    List<ToDoItem> findByStatusAndIsActiveTrue(String status);

    List<ToDoItem> findByProgressGreaterThanEqualAndIsActiveTrue(double progress);
}
