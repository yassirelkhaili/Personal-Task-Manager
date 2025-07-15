package com.taskmanager.errors;

public class TaskManagerException extends Exception {

  public TaskManagerException() {
    super();
  }

  public TaskManagerException(String message) {
    super(message);
  }

  public TaskManagerException(String message, Throwable cause) {
    super(message, cause);
  }

  public TaskManagerException(Throwable cause) {
    super(cause);
  }
}