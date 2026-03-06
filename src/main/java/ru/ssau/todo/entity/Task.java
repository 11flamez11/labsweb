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

    public Task() {
    }

}