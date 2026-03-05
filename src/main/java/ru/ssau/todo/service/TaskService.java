package ru.ssau.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.entity.TaskStatus;
import ru.ssau.todo.exception.TaskNotFoundException;
import ru.ssau.todo.repository.TaskRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository repository;

    public Task create(Task task) {
        long activeCount = repository.countActiveTasksByUserId(task.getCreatedBy());

        if (task.getStatus() == TaskStatus.OPEN ||
                task.getStatus() == TaskStatus.IN_PROGRESS) {

            if (activeCount >= 10) {
                throw new IllegalStateException("User cannot have more than 10 active tasks");
            }
        }

        task.setCreatedAt(LocalDateTime.now());
        return repository.create(task);
    }

    public Task getById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public List<Task> findAll(LocalDateTime from, LocalDateTime to, long userId) {
        return repository.findAll(from, to, userId);
    }

    public void update(Task task) {
        repository.update(task);
    }

    public void delete(long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        if (Duration.between(task.getCreatedAt(), LocalDateTime.now()).toMinutes() < 5) {
            throw new IllegalStateException("Cannot delete task created less than 5 minutes ago");
        }

        repository.deleteById(id);
    }
}