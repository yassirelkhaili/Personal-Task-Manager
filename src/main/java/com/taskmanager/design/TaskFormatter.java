package com.taskmanager.design;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.taskmanager.models.Task;
import com.taskmanager.enums.Priority;
import com.taskmanager.enums.Status;

/**
 * TaskFormatter handles all output formatting and templates for terminal
 * interaction.
 * Provides consistent formatting for task-related operations and user interface
 * elements.
 */
public class TaskFormatter {

  // ANSI Color codes for terminal formatting
  public final String RESET = "\u001B[0m";
  public final String BLACK = "\u001B[30m";
  public final String RED = "\u001B[31m";
  public final String GREEN = "\u001B[32m";
  public final String YELLOW = "\u001B[33m";
  public final String BLUE = "\u001B[34m";
  public final String PURPLE = "\u001B[35m";
  public final String CYAN = "\u001B[36m";
  public final String WHITE = "\u001B[37m";
  private final String GRAY = "\033[90m";

  // Background colors
  public final String BLACK_BG = "\u001B[40m";
  public final String RED_BG = "\u001B[41m";
  public final String GREEN_BG = "\u001B[42m";
  public final String YELLOW_BG = "\u001B[43m";
  public final String BLUE_BG = "\u001B[44m";
  public final String PURPLE_BG = "\u001B[45m";
  public final String CYAN_BG = "\u001B[46m";
  public final String WHITE_BG = "\u001B[47m";
  private final String MAGENTA_BG = "\033[45m";

  // Text styles
  public final String BOLD = "\u001B[1m";
  public final String UNDERLINE = "\u001B[4m";
  public final String ITALIC = "\u001B[3m";

  private final String SEPARATOR = "─".repeat(60);
  private final String DOUBLE_SEPARATOR = "═".repeat(60);
  private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  /**
   * Formats the application header with branding
   */
  public String formatHeader() {
    return String.format("""
        %s%s
        %s                     TASK MANAGER v1.0           %s
        %s%s
        %s""",
        CYAN + BOLD, DOUBLE_SEPARATOR,
        CYAN, RESET,
        CYAN + BOLD, DOUBLE_SEPARATOR,
        RESET);
  }

  /**
   * Formats a success message with green styling
   */
  public String formatSuccess(String message) {
    return String.format("%s%s%s \n", GREEN + BOLD, message, RESET);
  }

  /**
   * Formats an error message with red styling
   */
  public String formatError(String message) {
    return String.format("%sError: %s%s", RED + BOLD, message, RESET);
  }

  /**
   * Formats a warning message with yellow styling
   */
  public String formatWarning(String message) {
    return String.format("%sWarning: %s%s", YELLOW + BOLD, message, RESET);
  }

  /**
   * Formats an info message with blue styling
   */
  public String formatInfo(String message) {
    return String.format("%s%s%s", BLUE + BOLD, message, RESET);
  }

  /**
   * Formats a single task for display
   */
  public String formatTask(Task task) {
    String status = task.getStatus().getDisplayName();
    String priority = getPriorityBadge(task.getPriority());
    String dueDate = task.getDueDate() != null ? " (Due: " + task.getDueDate().format(DATE_FORMAT) + ")" : "";

    return String.format("%s [%s] %s%s %s%s%s%s",
        status,
        task.getId(),
        priority,
        BOLD, task.getTitle(), RESET,
        ITALIC + dueDate, RESET);
  }

  /**
   * Formats a list of tasks with headers and numbering
   */
  public String formatTaskList(List<Task> tasks, String title) {
    if (tasks.isEmpty()) {
      return formatInfo("No tasks found.");
    }

    StringBuilder sb = new StringBuilder();
    sb.append(formatSectionHeader(title));
    sb.append("\n");

    for (int i = 0; i < tasks.size(); i++) {
      sb.append(String.format("%s%2d.%s %s\n",
          CYAN, i + 1, RESET, formatTask(tasks.get(i))));
    }

    sb.append(SEPARATOR);
    sb.append(String.format("\n%sTotal: %d tasks%s", BOLD, tasks.size(), RESET));

    return sb.toString();
  }

  /**
   * Formats detailed task information
   */
  public String formatTaskDetails(Task task) {
    StringBuilder sb = new StringBuilder();
    sb.append(formatSectionHeader("Task Details"));
    sb.append("\n");

    sb.append(String.format("%sID:%s           %s\n", BOLD, RESET, task.getId()));
    sb.append(String.format("%sTitle:%s        %s\n", BOLD, RESET, task.getTitle()));
    sb.append(String.format("%sDescription:%s  %s\n", BOLD, RESET,
        task.getDescription() != null ? task.getDescription() : "No description"));
    sb.append(String.format("%sStatus:%s       %s\n", BOLD, RESET, task.getStatus().getDisplayName()));
    sb.append(String.format("%sPriority:%s     %s\n", BOLD, RESET,
        getPriorityBadge(task.getPriority())));
    sb.append(String.format("%sCreated:%s      %s\n", BOLD, RESET,
        task.getCreatedAt().format(DATE_FORMAT)));

    if (task.getDueDate() != null) {
      sb.append(String.format("%sDue Date:%s     %s\n", BOLD, RESET,
          task.getDueDate().format(DATE_FORMAT)));
    }

    if (task.getCompletedAt() != null) {
      sb.append(String.format("%sCompleted:%s    %s\n", BOLD, RESET,
          task.getCompletedAt().format(DATE_FORMAT)));
    }

    sb.append(SEPARATOR);
    return sb.toString();
  }

  /**
   * Formats a section header with styling
   */
  public String formatSectionHeader(String title) {
    return String.format("%s%s %s %s%s",
        BLUE + BOLD, SEPARATOR.substring(0, 20), title,
        SEPARATOR.substring(0, 20), RESET);
  }

  /**
   * Formats a help menu with commands
   */
  public String formatHelpMenu() {
    StringBuilder sb = new StringBuilder();
    sb.append(formatSectionHeader("Available Commands"));
    sb.append("\n\n");

    Map<String, String> commands = Map.of(
        "add <title>", "Add a new task",
        "list", "Show all tasks",
        "complete <id>", "Mark task as completed",
        "delete <id>", "Delete a task",
        "edit <id>", "Edit an existing task",
        "help", "Show this help menu",
        "exit", "Exit the application");

    commands.forEach((command, description) -> {
      sb.append(String.format("  %s%-15s%s %s\n",
          GREEN + BOLD, command, RESET, description));
    });

    sb.append("\n").append(SEPARATOR);
    return sb.toString();
  }

  /**
   * Formats a prompt for user input
   */
  public String formatPrompt(String message) {
    return String.format("%s%s>%s ", CYAN + BOLD, message, RESET);
  }

  /**
   * Formats a confirmation prompt
   */
  public String formatConfirmPrompt(String message) {
    return String.format("%s%s (y/N):%s ", YELLOW + BOLD, message, RESET);
  }

  /**
   * Formats statistics and summary information
   */
  public String formatStats(List<Task> tasks) {
    long totalTasks = tasks.size();
    long completedTasks = tasks.stream()
        .filter(t -> t.getStatus() == Status.COMPLETED)
        .count();
    long pendingTasks = tasks.stream()
        .filter(t -> t.getStatus() == Status.PENDING)
        .count();
    long inProgressTasks = tasks.stream()
        .filter(t -> t.getStatus() == Status.IN_PROGRESS)
        .count();

    Map<Priority, Long> priorityCount = tasks.stream()
        .collect(Collectors.groupingBy(Task::getPriority, Collectors.counting()));

    StringBuilder sb = new StringBuilder();
    sb.append(formatSectionHeader("Task Statistics"));
    sb.append("\n\n");

    sb.append(String.format("%sTotal Tasks:%s    %d\n", BOLD, RESET, totalTasks));
    sb.append(String.format("%s├─ Completed:%s   %s%d%s\n", BOLD, RESET, GREEN, completedTasks, RESET));
    sb.append(String.format("%s├─ In Progress:%s %s%d%s\n", BOLD, RESET, YELLOW, inProgressTasks, RESET));
    sb.append(String.format("%s└─ Pending:%s     %s%d%s\n", BOLD, RESET, RED, pendingTasks, RESET));

    sb.append("\n").append(String.format("%sPriority Breakdown:%s\n", BOLD, RESET));
    sb.append(String.format("%s├─ High:%s        %d\n", BOLD, RESET,
        priorityCount.getOrDefault(Priority.HIGH, 0L)));
    sb.append(String.format("%s├─ Medium:%s      %d\n", BOLD, RESET,
        priorityCount.getOrDefault(Priority.MEDIUM, 0L)));
    sb.append(String.format("%s└─ Low:%s         %d\n", BOLD, RESET,
        priorityCount.getOrDefault(Priority.LOW, 0L)));

    if (totalTasks > 0) {
      double completionRate = (double) completedTasks / totalTasks * 100;
      sb.append(String.format("\n%sCompletion Rate:%s %.1f%%\n", BOLD, RESET, completionRate));
    }

    sb.append("\n").append(SEPARATOR);
    return sb.toString();
  }

  /**
   * Formats a progress bar
   */
  public String formatProgressBar(int current, int total, int width) {
    if (total == 0)
      return "";

    int filled = (int) ((double) current / total * width);
    String bar = "█".repeat(filled) + "░".repeat(width - filled);
    double percentage = (double) current / total * 100;

    return String.format("%s[%s%s%s] %.1f%% (%d/%d)%s",
        CYAN, GREEN, bar, CYAN, percentage, current, total, RESET);
  }

  /**
   * Formats a table with headers and rows
   */
  public String formatTable(String[] headers, List<String[]> rows) {
    StringBuilder sb = new StringBuilder();

    // Calculate column widths
    int[] widths = new int[headers.length];
    for (int i = 0; i < headers.length; i++) {
      widths[i] = headers[i].length();
      for (String[] row : rows) {
        if (i < row.length) {
          widths[i] = Math.max(widths[i], stripAnsiCodes(row[i]).length());
        }
      }
      widths[i] += 2; // padding
    }

    // Format header
    sb.append(BOLD);
    for (int i = 0; i < headers.length; i++) {
      sb.append(String.format("%-" + widths[i] + "s", headers[i]));
    }
    sb.append(RESET).append("\n");

    // Header separator
    for (int width : widths) {
      sb.append("─".repeat(width));
    }
    sb.append("\n");

    // Format rows
    for (String[] row : rows) {
      for (int i = 0; i < headers.length && i < row.length; i++) {
        String cell = row[i];
        int padding = widths[i] - stripAnsiCodes(cell).length();
        sb.append(cell).append(" ".repeat(Math.max(0, padding)));
      }
      sb.append("\n");
    }

    return sb.toString();
  }

  /**
   * Formats a loading animation frame
   */
  public String formatLoading(String message, int frame) {
    String[] spinner = { "⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏" };
    return String.format("%s%s %s%s", CYAN, spinner[frame % spinner.length], message, RESET);
  }

  // Helper methods

  private String getPriorityBadge(Priority priority) {
    return switch (priority) {
      case HIGH -> RED_BG + WHITE + " HIGH " + RESET;
      case MEDIUM -> YELLOW_BG + BLACK + " MED " + RESET;
      case LOW -> GREEN_BG + BLACK + " LOW " + RESET;
      case URGENT -> MAGENTA_BG + WHITE + " URGENT " + RESET;
    };
  }

  /**
   * Strips ANSI color codes from text for length calculation
   */
  private String stripAnsiCodes(String text) {
    return text.replaceAll("\u001B\\[[;\\d]*m", "");
  }

  /**
   * Centers text within a given width
   */
  public String centerText(String text, int width) {
    int padding = Math.max(0, (width - stripAnsiCodes(text).length()) / 2);
    return " ".repeat(padding) + text;
  }

  /**
   * Wraps text to fit within specified width
   */
  public String wrapText(String text, int width) {
    if (stripAnsiCodes(text).length() <= width) {
      return text;
    }

    StringBuilder sb = new StringBuilder();
    String[] words = text.split("\\s+");
    int currentLength = 0;

    for (String word : words) {
      int wordLength = stripAnsiCodes(word).length();
      if (currentLength + wordLength + 1 > width) {
        sb.append("\n");
        currentLength = 0;
      } else if (currentLength > 0) {
        sb.append(" ");
        currentLength++;
      }
      sb.append(word);
      currentLength += wordLength;
    }

    return sb.toString();
  }
}