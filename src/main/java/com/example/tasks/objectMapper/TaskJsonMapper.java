package com.example.tasks.objectMapper;

import com.example.tasks.entity.Task;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class TaskJsonMapper {
    private final ObjectMapper objectMapper;

    public TaskJsonMapper() {
        this.objectMapper = new ObjectMapper();
    }

    public List<Task> readTasksFromFile(String fileName) {
        try {
            return objectMapper.readValue(new File(fileName), new TypeReference<>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void writeTasksToFile(List<Task> tasks, String fileName) {
        try {
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.writeValue(new File(fileName), tasks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
