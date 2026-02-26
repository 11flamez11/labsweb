package ru.ssau.todo.controller;

import org.springframework.web.bind.annotation.*;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.repository.TaskRepository;
import ru.ssau.todo.exception.TaskNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    // Конструктор для внедрения зависимости
    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskRepository.create(task);
    }

    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @GetMapping("/search")
    public List<Task> searchTasks(
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam long userId) {
        return taskRepository.findAll(from, to, userId);
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task task) throws Exception {
        task.setId(id);
        taskRepository.update(task);
        return task;
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(id);
    }

    @GetMapping("/active-count")
    public long getActiveTasksCount(@RequestParam long userId) {
        return taskRepository.countActiveTasksByUserId(userId);
    }
}