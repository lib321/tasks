package com.example.tasks.repository;

import com.example.tasks.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Task findTaskById(Long id);

    List<Task> findTaskByStatus(String filter);

    Long countByStatusIgnoreCase(String filter);
}
