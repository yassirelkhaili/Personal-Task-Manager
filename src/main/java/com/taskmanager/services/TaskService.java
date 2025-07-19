package com.taskmanager.services;

import com.taskmanager.repositories.TaskRepository;
import com.taskmanager.enums.Priority;
import com.taskmanager.errors.TaskManagerException;
import com.taskmanager.enums.Category;
import com.taskmanager.enums.Status;
import com.taskmanager.models.Task;
import java.util.List;

public class TaskService {

  private final TaskRepository taskRepository = new TaskRepository();

  public record TaskData(String title, String description, Priority priority, Category category, Status status) {
    public TaskData(String title) {
      this(title, null, Priority.MEDIUM, null, Status.PENDING);
    }

    public TaskData(Status status) {
      this(null, null, null, null, status);
    }
  }

  /**
   * Creates a new task with the provided data and saves it to the repository.
   * 
   * @param taskData the data object containing task information including title,
   *                 description, priority, and category. Title is required,
   *                 other fields are optional
   * @throws TaskManagerException     if validation fails (e.g., empty title,
   *                                  invalid description length, invalid
   *                                  priority)
   * @throws IllegalArgumentException if taskData is null
   * @since 1.0
   */
  public void createTask(TaskData taskData) throws TaskManagerException {
    Task task = new Task(taskData.title());

    if (taskData.description() != null) {
      task.setDescription(taskData.description());
    }
    if (taskData.priority() != null) {
      task.setPriority(taskData.priority());
    }
    if (taskData.category() != null) {
      task.setCategory(taskData.category());
    }
    this.taskRepository.save(task);
  }

  /**
   * Deletes a specific task by its unique identifier.
   *
   * @param taskId the unique identifier of the task to be deleted, must not be
   *               null
   * @return true if the task was successfully deleted, false if the task was not
   *         found
   * @throws IllegalArgumentException if taskId is null
   * @throws TaskManagerException     if an error occurs during deletion
   */
  public void deleteTask(String taskId) throws TaskManagerException {
    if (taskId == null || taskId.trim().isEmpty()) {
      throw new TaskManagerException("Task ID cannot be null or empty");
    }
    this.taskRepository.deleteById(taskId);
  }

  /**
   * Updates an existing task with the provided data.
   * 
   * @param taskId   the unique identifier of the task to be deleted, must not be
   *                 null
   * @param taskData the data object containing task information including title,
   *                 description, priority, and category. Title is required,
   *                 other fields are optional
   * @throws IllegalArgumentException if taskId is null
   * @throws TaskManagerException     if an error occurs during deletion
   */
  public void updateTask(String taskId, TaskData taskData) throws TaskManagerException {
    if (taskId == null || taskId.trim().isEmpty()) {
      throw new TaskManagerException("Task ID cannot be null or empty");
    }
    if (taskData == null) {
      throw new TaskManagerException("Task data cannot be null");
    }

    Task existingTask = taskRepository.findById(taskId);
    if (existingTask == null) {
      throw new TaskManagerException("Task with ID '" + taskId + "' not found");
    }

    if (taskData.title() != null) {
      existingTask.setTitle(taskData.title());
    }

    if (taskData.description() != null) {
      existingTask.setDescription(taskData.description());
    }
    if (taskData.priority() != null) {
      existingTask.setPriority(taskData.priority());
    }
    if (taskData.category() != null) {
      existingTask.setCategory(taskData.category());
    }

    if (taskData.status() != null) {
      existingTask.setStatus(taskData.status());
    }

    this.taskRepository.save(existingTask);
  }

  /**
   * Retrieves all tasks from the repository.
   * 
   * @return a list of all tasks in the system
   * @throws TaskManagerException if there's an error accessing the repository
   */
  public List<Task> readAvailableTasks() throws TaskManagerException {
    return taskRepository.fetchAll();
  }

  /**
   * Finds a task by its unique ID.
   * 
   * @param taskId the unique ID (UUID) of the task to find
   * @return the task if found
   * @throws TaskManagerException if task not found or repository error
   */
  public Task findTaskById(String taskId) throws TaskManagerException {
    if (taskId == null || taskId.trim().isEmpty()) {
      throw new TaskManagerException("Task ID cannot be null or empty");
    }

    return taskRepository.findById(taskId);
  }
}
