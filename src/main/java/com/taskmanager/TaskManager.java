package com.taskmanager;

import java.util.Scanner;

import com.taskmanager.errors.TaskManagerException;
import com.taskmanager.services.TaskService;
import com.taskmanager.services.TaskService.TaskData;
import com.taskmanager.design.TaskFormatter;
import com.taskmanager.enums.Priority;
import com.taskmanager.enums.Status;
import com.taskmanager.enums.Category;
import com.taskmanager.models.Task;
import java.util.List;

public class TaskManager {
  private TaskFormatter taskFormatter;
  private TaskService taskService;
  private Scanner scanner;
  private boolean running;

  public TaskManager() {
    loadFormatter();
    loadScanner();
    loadTaskService();
    running = true;

  }

  private void loadFormatter() {
    taskFormatter = new TaskFormatter();
  }

  private void loadTaskService() {
    taskService = new TaskService();
  }

  private void loadScanner() {
    scanner = new Scanner(System.in);
  }

  public void run() throws TaskManagerException {
    // Display the header
    System.out.println(taskFormatter.formatHeader());

    // Show welcome message
    System.out.println(taskFormatter.formatSuccess("Task Manager initialized successfully!"));

    // Display available commands
    System.out.println(taskFormatter.formatHelpMenu());

    while (running) {
      // Show initial prompt
      System.out.print(taskFormatter.formatPrompt("Enter command"));
      String input = scanner.nextLine().trim();

      if (!input.isEmpty()) {
        processCommand(input);
      }
    }

    // Clean up
    scanner.close();
    System.out.println(taskFormatter.formatInfo("Goodbye!"));
  }

  private void processCommand(String input) throws TaskManagerException {
    String[] parts = input.split("\\s+");
    String command = parts[0].toLowerCase();

    switch (command) {
      case "help" -> {
        System.out.println(taskFormatter.formatHelpMenu());
      }
      case "list" -> {
        List<Task> taskList = taskService.readAvailableTasks();
        if (Utils.isNullOrEmpty(taskList)) {
          System.out.println(taskFormatter.formatInfo("No tasks available yet."));
          break;
        }
        System.out.println(taskFormatter.formatTaskList(taskList, "Current Tasks:"));
      }
      case "exit" -> {
        System.out.println(taskFormatter.formatSuccess("Exiting Task Manager..."));
        running = false;
      }
      case "add" -> {
        if (parts.length > 1) {
          String title = String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
          taskService.createTask(new TaskData(title));
          System.out.println(taskFormatter.formatSuccess("Task '" + title + "' added successfully!"));
        } else {
          System.out.println(taskFormatter.formatError("Please provide a task title"));
        }
      }
      case "complete" -> {
        if (parts.length > 1) {
          taskService.updateTask(parts[1], new TaskData(null, null, null, null, Status.COMPLETED));
          System.out.println(taskFormatter.formatInfo("Completed task " + parts[1]));
        } else {
          System.out.println(taskFormatter.formatError("Please provide a task ID"));
        }
      }
      case "cancel" -> {
        if (parts.length > 1) {
          taskService.updateTask(parts[1], new TaskData(null, null, null, null, Status.CANCELLED));
          System.out.println(taskFormatter.formatInfo("Cancelled task " + parts[1]));
        } else {
          System.out.println(taskFormatter.formatError("Please provide a task ID"));
        }
      }
      case "delete" -> {
        if (parts.length > 1) {
          taskService.deleteTask(parts[1]);
          System.out.println(taskFormatter.formatWarning("Deleted task " + parts[1]));
        } else {
          System.out.println(taskFormatter.formatError("Please provide a task ID"));
        }
      }
      case "edit" -> {
        if (parts.length > 1) {
          String taskId = parts[1];
          editTask(taskId);
        } else {
          System.out.println(taskFormatter.formatError("Please provide a task ID"));
        }
      }
      default -> {
        System.out.println(taskFormatter.formatError("Unknown command: " + command));
        System.out.println(taskFormatter.formatInfo("Type 'help' to see available commands"));
      }
    }
  }

  private void editTask(String taskId) {
    try {
      Task task = taskService.findTaskById(taskId);
      if (task == null) {
        System.out.println(taskFormatter.formatError("Task with ID " + taskId + " not found"));
        return;
      }

      System.out.println(taskFormatter.formatTaskDetails(task));
      System.out.println(taskFormatter.formatInfo("Leave fields empty to keep current values"));

      // Collect input
      System.out.print(taskFormatter.formatPrompt("New title (" + task.getTitle() + ")"));
      String newTitle = scanner.nextLine().trim();

      System.out.print(taskFormatter.formatPrompt("New description (" +
          (task.getDescription() != null ? task.getDescription() : "none") + ")"));
      String newDescription = scanner.nextLine().trim();

      System.out.println(taskFormatter.formatInfo("Priority options: LOW, MEDIUM, HIGH, URGENT"));
      System.out.print(taskFormatter.formatPrompt("New priority (" + task.getPriority() + ")"));
      String priorityInput = scanner.nextLine().trim();

      System.out.println(taskFormatter
          .formatInfo("Category options: WORK, PERSONAL, STUDY, HEALTH, FITNESS, SHOPPING, TRAVEL, OTHER"));
      System.out.print(taskFormatter.formatPrompt("New category (" +
          (task.getCategory() != null ? task.getCategory() : "none") + ")"));
      String categoryInput = scanner.nextLine().trim();

      System.out.println(taskFormatter.formatInfo("Status options: PENDING, IN_PROGRESS, COMPLETED, CANCELLED"));
      System.out.print(taskFormatter.formatPrompt("New status (" + task.getStatus() + ")"));
      String statusInput = scanner.nextLine().trim();

      // Build TaskData with only the fields that were changed
      String finalTitle = newTitle.isEmpty() ? null : newTitle;
      String finalDescription = newDescription.isEmpty() ? null
          : (newDescription.equals("none") ? null : newDescription);

      Priority finalPriority = null;
      if (!priorityInput.isEmpty()) {
        try {
          finalPriority = Priority.valueOf(priorityInput.toUpperCase());
        } catch (IllegalArgumentException e) {
          System.out.println(taskFormatter.formatWarning("Invalid priority. Keeping current value."));
        }
      }

      Category finalCategory = null;
      if (!categoryInput.isEmpty()) {
        try {
          if (categoryInput.equalsIgnoreCase("none")) {
            finalCategory = null;
          } else {
            finalCategory = Category.valueOf(categoryInput.toUpperCase());
          }
        } catch (IllegalArgumentException e) {
          System.out.println(taskFormatter.formatWarning("Invalid category. Keeping current value."));
        }
      }

      Status finalStatus = null;
      if (!statusInput.isEmpty()) {
        try {
          finalStatus = Status.valueOf(statusInput.toUpperCase());
        } catch (IllegalArgumentException e) {
          System.out.println(taskFormatter.formatWarning("Invalid status. Keeping current value."));
        }
      }

      // Check if any changes were made
      boolean hasChanges = finalTitle != null || finalDescription != null ||
          finalPriority != null || finalCategory != null || finalStatus != null;

      if (hasChanges) {
        TaskData updateData = new TaskData(finalTitle, finalDescription, finalPriority, finalCategory, finalStatus);
        taskService.updateTask(taskId, updateData);

        System.out.println(taskFormatter.formatSuccess("Task updated successfully!"));
        // Get the updated task to display
        Task updatedTask = taskService.findTaskById(taskId);
        System.out.println(taskFormatter.formatTaskDetails(updatedTask));
      } else {
        System.out.println(taskFormatter.formatInfo("No changes made to the task."));
      }

    } catch (TaskManagerException e) {
      System.out.println(taskFormatter.formatError("Error editing task: " + e.getMessage()));
    } catch (Exception e) {
      System.out.println(taskFormatter.formatError("Unexpected error: " + e.getMessage()));
    }
  }

}