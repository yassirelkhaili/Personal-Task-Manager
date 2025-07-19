package com.taskmanager.enums;

public enum Category {
    WORK(1, "Work"),
    PERSONAL(2, "Personal"),
    STUDY(3, "Study"),
    HEALTH(4, "Health"),
    FITNESS(5, "Fitness"),
    SHOPPING(6, "Shopping"),
    TRAVEL(7, "Travel"),
    HOME(8, "Home"),
    FINANCE(9, "Finance"),
    SOCIAL(10, "Social"),
    HOBBY(11, "Hobby"),
    OTHER(12, "Other");

    private final int id;
    private final String displayName;

    Category(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    // Getters
    public int getId() {
        return this.id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public String toString() {
        return this.displayName;
    }
}