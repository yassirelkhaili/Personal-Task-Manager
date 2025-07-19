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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
        if (parts.length > 1) {
          String taskId = parts[1];
          Task task = taskService.findTaskById(taskId);
          System.out.println(taskFormatter.formatTaskDetails(task));
        } else {
          List<Task> taskList = taskService.readAvailableTasks();
          if (Utils.isNullOrEmpty(taskList)) {
            System.out.println(taskFormatter.formatInfo("No tasks available yet."));
            break;
          }
          System.out.println(taskFormatter.formatTaskList(taskList, "Current Tasks:"));
        }
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
          TaskData taskData = collectTaskInput(null);
          if (taskData.title() != null) {
            taskService.createTask(taskData);
            System.out.println(taskFormatter.formatSuccess("Task '" + taskData.title() + "' added successfully!"));
          } else {
            System.out.println(taskFormatter.formatError("Task title is required"));
          }
        }
      }
      case "complete" -> {
        if (parts.length > 1) {
          String taskId = parts[1];
          taskService.updateTask(taskId, new TaskData(null, null, null, null, Status.COMPLETED, null));
          System.out.println(taskFormatter.formatInfo("Completed task " + taskId));
        } else {
          System.out.println(taskFormatter.formatError("Please provide a task ID"));
        }
      }
      case "cancel" -> {
        if (parts.length > 1) {
          String taskId = parts[1];
          taskService.updateTask(taskId, new TaskData(null, null, null, null, Status.CANCELLED, null));
          System.out.println(taskFormatter.formatInfo("Cancelled task " + taskId));
        } else {
          System.out.println(taskFormatter.formatError("Please provide a task ID"));
        }
      }
      case "delete" -> {
        if (parts.length > 1) {
          String taskId = parts[1];
          taskService.deleteTask(taskId);
          System.out.println(taskFormatter.formatWarning("Deleted task " + taskId));
        } else {
          System.out.println(taskFormatter.formatError("Please provide a task ID"));
        }
      }
      case "edit" -> {
        if (parts.length > 1) {
          String taskId = parts[1];
          Task task = taskService.findTaskById(taskId);
          if (task == null) {
            System.out.println(taskFormatter.formatError("Task with ID " + taskId + " not found"));
          } else {
            TaskData updateData = collectTaskInput(task);

            boolean hasChanges = updateData.title() != null || updateData.description() != null ||
                updateData.priority() != null || updateData.category() != null || updateData.status() != null;

            if (hasChanges) {
              taskService.updateTask(taskId, updateData);
              System.out.println(taskFormatter.formatSuccess("Task updated successfully!"));
              Task updatedTask = taskService.findTaskById(taskId);
              System.out.println(taskFormatter.formatTaskDetails(updatedTask));
            } else {
              System.out.println(taskFormatter.formatInfo("No changes made to the task."));
            }
          }
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

  private TaskData collectTaskInput(Task existingTask) {
    boolean isEdit = existingTask != null;

    if (isEdit) {
      System.out.println(taskFormatter.formatTaskDetails(existingTask));
      System.out.println(taskFormatter.formatInfo("Leave fields empty to keep current values"));
    }

    // Title
    String titlePrompt = isEdit ? "New title (" + existingTask.getTitle() + ")" : "Task title";
    System.out.print(taskFormatter.formatPrompt(titlePrompt));
    String titleInput = scanner.nextLine().trim();
    String finalTitle = titleInput.isEmpty() ? null : titleInput;

    // Description
    String currentDesc = isEdit && existingTask.getDescription() != null ? existingTask.getDescription() : "none";
    String descPrompt = isEdit ? "New description (" + currentDesc + ")" : "Description (optional)";
    System.out.print(taskFormatter.formatPrompt(descPrompt));
    String descInput = scanner.nextLine().trim();
    String finalDescription = descInput.isEmpty() ? null : (descInput.equals("none") ? null : descInput);

    // Priority
    Priority currentPriority = isEdit ? existingTask.getPriority() : null;
    System.out.println(taskFormatter.formatInfo("Priority options: LOW, MEDIUM, HIGH, URGENT"));
    String priorityPrompt = isEdit ? "New priority (" + currentPriority + ")" : "Priority (optional)";
    System.out.print(taskFormatter.formatPrompt(priorityPrompt));
    String priorityInput = scanner.nextLine().trim();

    // Due Date
    LocalDate finalDueDate = null;
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String currentDueDate = isEdit && existingTask.getDueDate() != null
        ? existingTask.getDueDate().format(dateFormatter)
        : "none";

    String dueDatePrompt = isEdit ? "New due date (" + currentDueDate + ") [yyyy-MM-dd]"
        : "Due date (optional) [yyyy-MM-dd]";
    System.out.print(taskFormatter.formatPrompt(dueDatePrompt));
    String dueDateInput = scanner.nextLine().trim();

    if (!dueDateInput.isEmpty()) {
      try {
        if (!dueDateInput.equalsIgnoreCase("none")) {
          finalDueDate = LocalDate.parse(dueDateInput, dateFormatter);
        }
      } catch (DateTimeParseException e) {
        System.out.println(taskFormatter.formatWarning("Invalid date format. Expected yyyy-MM-dd."));
      }
    }

    Priority finalPriority = null;
    if (!priorityInput.isEmpty()) {
      try {
        finalPriority = Priority.valueOf(priorityInput.toUpperCase());
      } catch (IllegalArgumentException e) {
        System.out.println(taskFormatter.formatWarning("Invalid priority. Keeping current value."));
      }
    }

    // Category
    Category currentCategory = isEdit ? existingTask.getCategory() : null;
    String currentCatDisplay = currentCategory != null ? currentCategory.toString() : "none";
    System.out.println(taskFormatter.formatInfo(
        "Category options: WORK, PERSONAL, STUDY, HEALTH, FITNESS, SHOPPING, TRAVEL, OTHER"));
    String categoryPrompt = isEdit ? "New category (" + currentCatDisplay + ")" : "Category (optional)";
    System.out.print(taskFormatter.formatPrompt(categoryPrompt));
    String categoryInput = scanner.nextLine().trim();

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

    // Status
    Status finalStatus = null;
    if (isEdit) {
      System.out.println(taskFormatter.formatInfo("Status options: PENDING, IN_PROGRESS, COMPLETED, CANCELLED"));
      System.out.print(taskFormatter.formatPrompt("New status (" + existingTask.getStatus() + ")"));
      String statusInput = scanner.nextLine().trim();

      if (!statusInput.isEmpty()) {
        try {
          finalStatus = Status.valueOf(statusInput.toUpperCase());
        } catch (IllegalArgumentException e) {
          System.out.println(taskFormatter.formatWarning("Invalid status. Keeping current value."));
        }
      }
    }

    return new TaskData(finalTitle, finalDescription, finalPriority, finalCategory, finalStatus, finalDueDate);
  }

}