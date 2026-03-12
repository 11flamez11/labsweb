package ru.ssau.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ssau.todo.Dto.TaskDto;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.entity.TaskStatus;
import ru.ssau.todo.entity.User;
import ru.ssau.todo.exception.TaskNotFoundException;
import ru.ssau.todo.repository.TaskRepository;
import ru.ssau.todo.repository.UserRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskDto create(TaskDto dto) {

        User user = userRepository.findById(dto.getCreatedBy())
                .orElseThrow();

        long activeCount = taskRepository.countActiveTasksByUserId(user.getId());

        if (dto.getStatus() == TaskStatus.OPEN || dto.getStatus() == TaskStatus.IN_PROGRESS) {
            if (activeCount >= 10) {
                throw new IllegalStateException("User cannot have more than 10 active tasks");
            }
        }

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setStatus(dto.getStatus());
        task.setCreatedBy(user);
        task.setCreatedAt(LocalDateTime.now());

        taskRepository.save(task);

        return toDto(task);
    }

    public TaskDto getById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        return toDto(task);
    }

    public List<TaskDto> findAll(LocalDateTime from, LocalDateTime to, long userId) {

        return taskRepository.findTasks(from, to, userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        if (Duration.between(task.getCreatedAt(), LocalDateTime.now()).toMinutes() < 5) {
            throw new IllegalStateException("Cannot delete task created less than 5 minutes ago");
        }

        taskRepository.deleteById(id);
    }

    private TaskDto toDto(Task task) {

        TaskDto dto = new TaskDto();

        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setStatus(task.getStatus());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setCreatedBy(task.getCreatedBy().getId());

        return dto;
    }
}