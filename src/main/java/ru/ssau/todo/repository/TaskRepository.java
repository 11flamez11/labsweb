package ru.ssau.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.ssau.todo.entity.Task;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query(value = """
            SELECT * FROM task
            WHERE created_by = :userId
            AND (:from IS NULL OR created_at >= :from)
            AND (:to IS NULL OR created_at <= :to)
            """, nativeQuery = true)
    List<Task> findTasks(LocalDateTime from, LocalDateTime to, long userId);

    @Query("""
            SELECT COUNT(t)
            FROM Task t
            WHERE t.createdBy.id = :userId
            AND (t.status = 'OPEN' OR t.status = 'IN_PROGRESS')
            """)
    long countActiveTasksByUserId(long userId);
}