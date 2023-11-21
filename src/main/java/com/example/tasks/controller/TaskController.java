package com.example.tasks.controller;

import com.example.tasks.entity.Task;
import com.example.tasks.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class TaskController {

    public final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/tasks")
    public String getTasks(@RequestParam (required = false) String filter,
                           Model model) {
        List<Task> tasks = taskService.getFilteredTasks(filter);
        model.addAttribute("tasks", tasks);
        return "main_page";
    }

    @GetMapping("/tasks/details/{taskId}")
    public String getTaskDetails(@PathVariable Long taskId, Model model) {
        Task task = taskService.getTaskById(taskId);
        model.addAttribute("taskDetails", task);
        return "task_details";
    }

    @GetMapping("/tasks/complete")
    public String completeTask(@RequestParam Long taskId) {
        taskService.completeTask(taskId);
        return "redirect:/tasks";
    }

    @GetMapping("/tasks/delete")
    public String removeTask(@RequestParam Long taskId, Model mode) {
        Task task = taskService.getTaskById(taskId);
        mode.addAttribute("task", task);
        return "removeTask";
    }

    @PostMapping("/tasks/delete")
    public String removeTaskAction(@RequestParam Long taskId) {
        taskService.deleteTask(taskId);
        return "redirect:/tasks";
    }

    @GetMapping("/tasks/details/{taskId}/edit")
    public String editTask(Model model, @PathVariable Long taskId) {
        Task task = taskService.getTaskById(taskId);
        model.addAttribute("task", task);
        return "edit";
    }

    @PostMapping("/tasks/details/{taskId}/edit")
    public String editTaskAction(String text, @PathVariable Long taskId) {
        taskService.editTask(taskId, text);
        return "redirect:/tasks";
    }

    @GetMapping("/tasks/create")
    public String addTask(@RequestParam String title,
                          @RequestParam String assignee,
                          @RequestParam String description,
                          @RequestParam String priority) {
        taskService.addTask(title, assignee, description, priority);
        return "redirect:/tasks";
    }

    @GetMapping("/tasks/statistics")
    public String showStatistics(Model model) {
        Map<String, Integer> statistics = taskService.getStatistics();
        model.addAttribute("percentageCompleted", statistics.get("done"));
        model.addAttribute("percentageNew", statistics.get("new"));
        return "statistics";
    }
}
