package ru.ssau.todo.repository;

import org.springframework.stereotype.Repository;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.entity.TaskStatus;
import ru.ssau.todo.exception.TaskNotFoundException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Profile;

@Repository
@Profile("inmemory")
public class TaskInMemoryRepository implements TaskRepository {

    private final Map<Long, Task> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Task create(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        long newId = idGenerator.getAndIncrement();

        Task taskToSave = new Task();
        taskToSave.setId(newId);
        taskToSave.setTitle(task.getTitle());
        taskToSave.setStatus(task.getStatus());
        taskToSave.setCreatedBy(task.getCreatedBy());
        taskToSave.setCreatedAt(task.getCreatedAt() != null ? task.getCreatedAt() : LocalDateTime.now());

        storage.put(newId, taskToSave);
        return taskToSave;
    }

    @Override
    public Optional<Task> findById(long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Task> findAll(LocalDateTime from, LocalDateTime to, long userId) {
        return storage.values().stream()
                .filter(task -> task.getCreatedBy() == userId)
                .filter(task -> {
                    LocalDateTime createdAt = task.getCreatedAt();
                    return (from == null || !createdAt.isBefore(from)) &&
                            (to == null || !createdAt.isAfter(to));
                })
                .collect(Collectors.toList());
    }

    @Override
    public void update(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        Long taskId = task.getId();
        if (taskId == null || !storage.containsKey(taskId)) {
            throw new TaskNotFoundException(taskId);
        }

        Task existingTask = storage.get(taskId);
        task.setCreatedAt(existingTask.getCreatedAt());

        storage.put(taskId, task);
    }

    @Override
    public void deleteById(long id) {
        storage.remove(id);
    }

    @Override
    public long countActiveTasksByUserId(long userId) {
        return storage.values().stream()
                .filter(task -> task.getCreatedBy() == userId)
                .filter(task -> task.getStatus() == TaskStatus.OPEN ||
                        task.getStatus() == TaskStatus.IN_PROGRESS)
                .count();
    }
}