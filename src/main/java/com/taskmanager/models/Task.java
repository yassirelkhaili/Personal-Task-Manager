package com.taskmanager.models;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import com.taskmanager.enums.Priority;
import com.taskmanager.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.taskmanager.enums.Category;

import com.taskmanager.errors.TaskManagerException;

public class Task {
  // Core Identity
  private String id; // UUID for unique identification
  private String title; // Required: Task name (max 100 chars)
  private String description; // Optional: Detailed info (max 500 chars)

  // Classification
  private Priority priority; // LOW, MEDIUM, HIGH, URGENT
  private Status status; // PENDING, IN_PROGRESS, COMPLETED, CANCELLED
  private Category category; // WORK, PERSONAL, STUDY, HEALTH, etc.

  // Time Management
  private LocalDateTime createdAt; // When task was created
  private LocalDateTime updatedAt; // Last modification time
  private LocalDate dueDate; // Optional: Deadline
  private LocalDateTime completedAt; // When task was completed

  // Constructors
  public Task() {
    this.id = UUID.randomUUID().toString();
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
    this.status = Status.PENDING;
    this.priority = Priority.MEDIUM;
  }

  public Task(String title) {
    this();
    this.title = title;
  }

  public Task(String title, String description, Priority priority, Category category) {
    this(title);
    this.description = description;
    this.priority = priority;
    this.category = category;
  }

  // Core Identity Getters and Setters
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) throws TaskManagerException {
    if (title != null && title.length() > 100) {
      throw new TaskManagerException("Title cannot exceed 100 characters");
    }
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) throws TaskManagerException {
    if (description != null && description.length() > 200) {
      throw new TaskManagerException("Description cannot exceed 200 characters");
    }
    this.description = description;
  }

  // Classification Getters and Setters
  public Priority getPriority() {
    return priority;
  }

  public void setPriority(Priority priority) {
    this.priority = priority;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;

    // Auto-set completion time and progress when completed
    if (status == Status.COMPLETED) {
      this.completedAt = LocalDateTime.now();
    } else if (status == Status.CANCELLED) {
      this.completedAt = null;
    }
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  // Time Management Getters and Setters
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public LocalDateTime getDueDate() {
    return dueDate;
  }

  public void setDueDate(LocalDateTime dueDate) {
    this.dueDate = dueDate;
  }

  public LocalDateTime getCompletedAt() {
    return completedAt;
  }

  public void setCompletedAt(LocalDateTime completedAt) {
    this.completedAt = completedAt;
  }

  @JsonIgnore
  public boolean isOverdue() {
    return dueDate != null && LocalDateTime.now().isAfter(dueDate) && status != Status.COMPLETED;
  }

  @JsonIgnore
  public boolean isCompleted() {
    return status == Status.COMPLETED;
  }

  public boolean isDueSoon(int hours) {
    if (dueDate == null)
      return false;
    return LocalDateTime.now().plusHours(hours).isAfter(dueDate) && !isCompleted();
  }

  public void markAsCompleted() throws TaskManagerException {
    setStatus(Status.COMPLETED);
  }

  public void markAsInProgress() throws TaskManagerException {
    setStatus(Status.IN_PROGRESS);
  }

  // equals and hashCode
  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || this.getClass() != o.getClass())
      return false;
    Task task = (Task) o;
    return Objects.equals(this.id, task.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }
}
