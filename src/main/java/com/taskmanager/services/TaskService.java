package com.taskmanager.services;

import com.taskmanager.repositories.TaskRepository;
import com.taskmanager.enums.Priority;
import com.taskmanager.errors.TaskManagerException;
import com.taskmanager.enums.Category;
import com.taskmanager.models.Task;

public class TaskService {

  private final TaskRepository taskRepository = new TaskRepository();

  public record CreateTaskData(String title, String description, Priority priority, Category category) {
    public CreateTaskData(String title) {
      this(title, null, Priority.MEDIUM, null);
    }
  }

  /**
   * Creates a new task with the provided data and saves it to the repository.
   * 
   * This method validates the task data, creates a new Task object with the
   * specified information, and persists it using the repository. The task
   * will be assigned a unique ID and creation timestamp automatically.
   * 
   * @param taskData the data object containing task information including title,
   *                 description, priority, and category. Title is required,
   *                 other fields are optional
   * @throws TaskManagerException     if validation fails (e.g., empty title,
   *                                  invalid description length, invalid
   *                                  priority)
   * @throws IllegalArgumentException if taskData is null
   * @see CreateTaskData
   * @see Task#Task(String)
   * @since 1.0
   */
  public void createTask(CreateTaskData taskData) throws TaskManagerException {
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
   * This method removes the task from the repository and returns a boolean
   * indicating whether the deletion was successful. If the task with the
   * specified ID does not exist, the method returns false.
   * 
   * @param taskId the unique identifier of the task to be deleted, must not be
   *               null
   * @return true if the task was successfully deleted, false if the task was not
   *         found
   * @throws IllegalArgumentException if taskId is null
   * @throws TaskManagerException     if an error occurs during deletion
   * @see TaskRepository#deleteById(String)
   * @since 1.0
   */
  public void deleteTask(String taskId) throws TaskManagerException {
    if (taskId == null || taskId.trim().isEmpty()) {
      throw new TaskManagerException("Task ID cannot be null or empty");
    }
    this.taskRepository.deleteById(taskId);
  }
}
