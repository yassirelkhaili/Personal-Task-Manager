package com.taskmanager;

import java.util.Scanner;

import com.taskmanager.errors.TaskManagerException;
import com.taskmanager.services.TaskService;
import com.taskmanager.services.TaskService.TaskData;
import com.taskmanager.design.TaskFormatter;
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
          System.out.println(taskFormatter.formatInfo("Complete task " + parts[1] + " (not implemented yet)"));
        } else {
          System.out.println(taskFormatter.formatError("Please provide a task ID"));
        }
      }
      case "delete" -> {
        if (parts.length > 1) {
          System.out.println(taskFormatter.formatWarning("Delete task " + parts[1] + " (not implemented yet)"));
        } else {
          System.out.println(taskFormatter.formatError("Please provide a task ID"));
        }
      }
      case "edit" -> {
        if (parts.length > 1) {
          System.out.println(taskFormatter.formatInfo("Edit task " + parts[1] + " (not implemented yet)"));
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
}