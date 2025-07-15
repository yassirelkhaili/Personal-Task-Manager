package com.taskmanager.enums;

public enum Priority {
    LOW(1, "Low"),
    MEDIUM(2, "Medium"),     // Default
    HIGH(3, "High"), 
    URGENT(4, "Urgent");
    
    private final int level;
    private final String displayName;

    Priority(int level, String displayName) {
      this.displayName = displayName;
      this.level = level;
    }

    // Getters
    public int getLevel() { return this.level; };
    public String getDisplayName() { return this.displayName; };
}