package ru.ssau.todo.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.entity.TaskStatus;
import ru.ssau.todo.exception.TaskNotFoundException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("jdbc")
@RequiredArgsConstructor
public class TaskJdbcRepository implements TaskRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Task> rowMapper = (rs, rowNum) -> {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setTitle(rs.getString("title"));
        task.setStatus(TaskStatus.valueOf(rs.getString("status")));
        task.setCreatedBy(rs.getLong("created_by"));
        task.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return task;
    };

    @Override
    public Task create(Task task) {
        String sql = """
                INSERT INTO task(title, status, created_by, created_at)
                VALUES (?, ?, ?, ?)
                RETURNING id
                """;

        Long id = jdbcTemplate.queryForObject(
                sql,
                Long.class,
                task.getTitle(),
                task.getStatus().name(),
                task.getCreatedBy(),
                task.getCreatedAt()
        );

        task.setId(id);
        return task;
    }

    @Override
    public Optional<Task> findById(long id) {
        String sql = "SELECT * FROM task WHERE id = ?";
        List<Task> tasks = jdbcTemplate.query(sql, rowMapper, id);
        return tasks.stream().findFirst();
    }

    @Override
    public List<Task> findAll(LocalDateTime from, LocalDateTime to, long userId) {
        String sql = """
                SELECT * FROM task
                WHERE created_by = ?
                AND (? IS NULL OR created_at >= ?)
                AND (? IS NULL OR created_at <= ?)
                """;

        return jdbcTemplate.query(
                sql,
                rowMapper,
                userId,
                from, from,
                to, to
        );
    }

    @Override
    public void update(Task task) {
        String sql = """
                UPDATE task
                SET title = ?, status = ?
                WHERE id = ?
                """;

        int updated = jdbcTemplate.update(
                sql,
                task.getTitle(),
                task.getStatus().name(),
                task.getId()
        );

        if (updated == 0) {
            throw new TaskNotFoundException(task.getId());
        }
    }

    @Override
    public void deleteById(long id) {
        jdbcTemplate.update("DELETE FROM task WHERE id = ?", id);
    }

    @Override
    public long countActiveTasksByUserId(long userId) {
        String sql = """
                SELECT COUNT(*) FROM task
                WHERE created_by = ?
                AND status IN ('OPEN', 'IN_PROGRESS')
                """;

        return jdbcTemplate.queryForObject(sql, Long.class, userId);
    }
}