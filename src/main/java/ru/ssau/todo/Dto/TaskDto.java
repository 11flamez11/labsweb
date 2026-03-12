package ru.ssau.todo.Dto;

import lombok.Getter;
import lombok.Setter;
import ru.ssau.todo.entity.TaskStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class TaskDto {

    private Long id;
    private String title;
    private TaskStatus status;
    private Long createdBy;
    private LocalDateTime createdAt;
}