package com.taskmanager;

import com.taskmanager.errors.TaskManagerException;

/**
 * Hello world!
 *
 */
public class App {
  public static void main(String[] args) {
    // Main Code Execution
    try {
      System.out.println("🚀 Starting Task Manager Application...");
      throw new TaskManagerException("Random exception String");
    } catch (TaskManagerException e) {
      System.err.println("❌ Error: " + e.getMessage());

      if (e.getCause() != null) {
        System.err.println("   Root cause: " + e.getCause().getMessage());
      }

      System.exit(1);
    } catch (Exception e) {
      System.err.println("❌ Unexpected error: " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    } finally {
      System.out.println("Exiting the Program...");
    }
  }
}
