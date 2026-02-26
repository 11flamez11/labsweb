package ru.ssau.todo.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class Task {
    private Long id;
    private String title;
    private TaskStatus status;
    private Long createdBy;
    private LocalDateTime createdAt;

    // Пустой конструктор
    public Task() {
    }

    // Конструктор с параметрами для удобства
    public Task(String title, TaskStatus status, Long createdBy) {
        this.title = title;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
    }
}