package ru.ssau.todo.exception;

public class ClosedTaskException extends RuntimeException  {
    public ClosedTaskException (String message){ super(message);}
    public ClosedTaskException(Long id) {
        super("Task Closed with id " + id);
    }
}
