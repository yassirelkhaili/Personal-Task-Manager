package com.taskmanager.enums;

public enum Status {
  PENDING("Pending"), // Default
  IN_PROGRESS("In Progress"),
  COMPLETED("Completed"),
  CANCELLED("Cancelled");

  private final String displayName;

  Status(String displayName) {
    this.displayName = displayName;
  }
 
  // Getter
  public String getDisplayName() {
    return this.displayName;
  };
}