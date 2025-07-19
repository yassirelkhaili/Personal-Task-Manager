package com.taskmanager.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
  private LocalDateTime dueDate; // Optional: Deadline
  private LocalDateTime completedAt; // When task was completed
  private Integer estimatedHours; // Optional: Time estimate

  // Organization
  private List<String> tags; // Optional: Custom tags ["urgent", "meeting"]
  private String assignedTo; // Optional: Who is responsible
  private String projectId; // Optional: Group related tasks

  // Progress Tracking
  private Integer progressPercentage; // 0-100% completion
  private String notes; // Optional: Additional notes

  // Constructors
  public Task() {
    this.id = UUID.randomUUID().toString();
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
    this.status = Status.PENDING;
    this.priority = Priority.MEDIUM;
    this.progressPercentage = 0;
    this.tags = new ArrayList<>();
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
    updateTimestamp();
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) throws TaskManagerException {
    if (description != null && description.length() > 200) {
      throw new TaskManagerException("Description cannot exceed 200 characters");
    }
    this.description = description;
    updateTimestamp();
  }

  // Classification Getters and Setters
  public Priority getPriority() {
    return priority;
  }

  public void setPriority(Priority priority) {
    this.priority = priority;
    updateTimestamp();
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
    updateTimestamp();

    // Auto-set completion time and progress when completed
    if (status == Status.COMPLETED) {
      this.completedAt = LocalDateTime.now();
      this.progressPercentage = 100;
    } else if (status == Status.CANCELLED) {
      this.completedAt = null;
    }
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
    updateTimestamp();
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
    updateTimestamp();
  }

  public LocalDateTime getCompletedAt() {
    return completedAt;
  }

  public void setCompletedAt(LocalDateTime completedAt) {
    this.completedAt = completedAt;
  }

  public Integer getEstimatedHours() {
    return estimatedHours;
  }

  public void setEstimatedHours(Integer estimatedHours) throws TaskManagerException {
    if (estimatedHours != null && estimatedHours < 0) {
      throw new TaskManagerException("Estimated hours cannot be negative");
    }
    this.estimatedHours = estimatedHours;
    updateTimestamp();
  }

  // Organization Getters and Setters
  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags != null ? tags : new ArrayList<>();
    updateTimestamp();
  }

  public String getAssignedTo() {
    return assignedTo;
  }

  public void setAssignedTo(String assignedTo) {
    this.assignedTo = assignedTo;
    updateTimestamp();
  }

  public String getProjectId() {
    return projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
    updateTimestamp();
  }

  // Progress Tracking Getters and Setters
  public Integer getProgressPercentage() {
    return progressPercentage;
  }

  public void setProgressPercentage(Integer progressPercentage) throws TaskManagerException {
    if (progressPercentage != null && (progressPercentage < 0 || progressPercentage > 100)) {
      throw new TaskManagerException("Progress percentage must be between 0 and 100");
    }
    this.progressPercentage = progressPercentage;
    updateTimestamp();

    // Auto-update status based on progress
    if (progressPercentage != null && progressPercentage == 100 && status != Status.COMPLETED) {
      setStatus(Status.COMPLETED);
    } else if (progressPercentage != null && progressPercentage > 0 && status == Status.PENDING) {
      setStatus(Status.IN_PROGRESS);
    }
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
    updateTimestamp();
  }

  // Utility methods
  private void updateTimestamp() {
    this.updatedAt = LocalDateTime.now();
  }

  public void addTag(String tag) {
    if (tag != null && !tag.trim().isEmpty()) {
      if (this.tags == null) {
        this.tags = new ArrayList<>();
      }
      if (!this.tags.contains(tag.trim())) {
        this.tags.add(tag.trim());
        updateTimestamp();
      }
    }
  }

  public void removeTag(String tag) {
    if (this.tags != null && tag != null) {
      this.tags.remove(tag.trim());
      updateTimestamp();
    }
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
    setProgressPercentage(100);
  }

  public void markAsInProgress() throws TaskManagerException {
    setStatus(Status.IN_PROGRESS);
    if (progressPercentage == null || progressPercentage == 0) {
      setProgressPercentage(1);
    }
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

  // toString
  @Override
  public String toString() {
    return "Task{" +
        "id='" + id + '\'' +
        ", title='" + title + '\'' +
        ", priority=" + priority +
        ", status=" + status +
        ", category=" + category +
        ", progressPercentage=" + progressPercentage +
        ", dueDate=" + dueDate +
        ", assignedTo='" + assignedTo + '\'' +
        '}';
  }
}
