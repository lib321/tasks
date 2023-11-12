package com.example.tasks.service;

import com.example.tasks.entity.Task;
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

    private List<Task> tasks = new ArrayList<>();
    private final String filePath = "json/tasks.json";

    public TaskService() {
        loadTasks();
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public Task getTaskById(Long id) {
        return tasks.stream()
                .filter(task -> task.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Task> getFilteredTasks(String filter) {
        if (filter != null) {
            if (filter.equals("new")) {
                return tasks.stream()
                        .filter(task -> task.getStatus().equals("new"))
                        .collect(Collectors.toList());
            } else {
                return tasks.stream()
                        .filter(task -> task.getStatus().equals("done"))
                        .collect(Collectors.toList());
            }
        } else {
            return tasks;
        }
    }

    private Long generateUniqueId() {
        if (!tasks.isEmpty()) {
            List<Task> tasks1 = getTasks();
            return tasks1.get(tasks1.size() - 1).getId() + 1;
        } else {
            return 1L;
        }
    }

    public void addTask(String title, String assignee, String description, String priority) {
        Task task = new Task();
        task.setId(generateUniqueId());
        task.setTitle(title);
        task.setAssignee(assignee);
        task.setStatus("new");
        task.setCreationDate(LocalDate.now());
        task.setDescription(description);
        task.setPriority(priority);
        tasks.add(task);
        saveTasks();
    }

    public void completeTask(Long taskId) {
        Task task = getTaskById(taskId);
        task.setStatus("done");
        task.setCompletedDate(LocalDate.now());
    }

    public void editTask(Long taskId, String text) {
        Task task = getTaskById(taskId);
        task.setDescription(text);
    }

    public void deleteTask(Long taskId) {
        Task task = getTaskById(taskId);
        List<Task> tasksList = getTasks();
        tasksList.remove(task);
        saveTasks();
    }

    public Map<String, Integer> getStatistics() {
        long total = tasks.size();
        long completedTasks = tasks.stream()
                .filter(task -> task.getStatus().equals("done")).count();
        long newTasks = tasks.stream()
                .filter(task -> task.getStatus().equals("new")).count();
        double percentageCompleted = (completedTasks * 100.0) / total;
        double percentageNew = (newTasks * 100.0) / total;
        Map<String, Integer> statistics = new HashMap<>();
        int roundedCompletedPercentage = (int) Math.round(percentageCompleted);
        int roundedNewPercentage = (int) Math.round(percentageNew);
        statistics.put("done", roundedCompletedPercentage);
        statistics.put("new", roundedNewPercentage);
        return statistics;
    }

    private void loadTasks() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            objectMapper.registerModule(new JavaTimeModule());

            File file = new File(filePath);

            if (!file.exists()) {
                tasks = new ArrayList<>();
                saveTasks();
            } else {
                CollectionType typeReference =
                        TypeFactory.defaultInstance().constructCollectionType(List.class, Task.class);

                tasks = objectMapper.readValue(file, typeReference);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveTasks() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            objectMapper.registerModule(new JavaTimeModule());

            objectMapper.writeValue(new File(filePath), tasks);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
