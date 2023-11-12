package com.example.tasks.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String status;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "completed_date")
    private LocalDate completedDate;

    private String priority;

    private String assignee;
}
