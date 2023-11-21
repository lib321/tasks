package com.example.tasks.service;

import com.example.tasks.entity.Task;
import com.example.tasks.objectMapper.TaskJsonMapper;
import com.example.tasks.repository.TaskRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.GeneratedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskJsonMapper taskJsonMapper;

    private final TaskRepository taskRepository;

    private List<Task> tasks;

    private final String filePath = "json/tasks.json";

    @Autowired
    public TaskService(TaskJsonMapper taskJsonMapper, TaskRepository taskRepository) {
        this.taskJsonMapper = taskJsonMapper;
        this.taskRepository = taskRepository;
    }

    public List<Task> getTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) {
        return taskRepository.findTaskById(id);
    }

    public void addTask(String title, String assignee, String description, String priority) {
        Task task = new Task();
        if (tasks != null) {
            tasks = taskJsonMapper.readTasksFromFile(filePath);
        } else {
            tasks = new ArrayList<>();
        }
        task.setTitle(title);
        task.setAssignee(assignee);
        task.setStatus("new");
        task.setCreationDate(LocalDate.now());
        task.setDescription(description);
        task.setPriority(priority);
        tasks.add(task);
        taskRepository.save(task);
        taskJsonMapper.writeTasksToFile(tasks, filePath);
    }

    public void editTask(Long id, String text) {
        Task task = taskRepository.findTaskById(id);
        tasks = taskJsonMapper.readTasksFromFile(filePath);
        task.setDescription(text);
        taskRepository.save(task);
        tasks.removeIf(t -> t.getId().equals(id));
        tasks.add(task);
        taskJsonMapper.writeTasksToFile(tasks, filePath);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
        tasks = taskJsonMapper.readTasksFromFile(filePath);
        tasks.removeIf(t -> t.getId().equals(id));
        taskJsonMapper.writeTasksToFile(tasks, filePath);
    }

    public List<Task> getFilteredTasks(String filter) {
        if (filter != null) {
            return taskRepository.findTaskByStatus(filter);
        } else {
            return taskRepository.findAll();
        }
    }

    public void completeTask(Long id) {
        Task task = taskRepository.findTaskById(id);
        tasks = taskJsonMapper.readTasksFromFile(filePath);
        task.setStatus("done");
        task.setCompletedDate(LocalDate.now());
        taskRepository.save(task);
        tasks.removeIf(t -> t.getId().equals(id));
        tasks.add(task);
        taskJsonMapper.writeTasksToFile(tasks, filePath);
    }

    public Map<String, Integer> getStatistics() {
        long total = taskRepository.count();
        long completedTasks = taskRepository.countByStatusIgnoreCase("Done");
        long newTasks = taskRepository.countByStatusIgnoreCase("New");
        double percentageCompleted = (completedTasks * 100.0) / total;
        double percentageNew = (newTasks * 100.0) / total;
        Map<String, Integer> statistics = new HashMap<>();
        int roundedCompletedPercentage = (int) Math.round(percentageCompleted);
        int roundedNewPercentage = (int) Math.round(percentageNew);
        statistics.put("done", roundedCompletedPercentage);
        statistics.put("new", roundedNewPercentage);
        return statistics;
    }
}
