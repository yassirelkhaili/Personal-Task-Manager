package com.taskmanager.repositories;

import com.taskmanager.interfaces.TaskRepositoryInterface;
import com.taskmanager.errors.TaskManagerException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.type.TypeReference;
import com.taskmanager.models.Task;
import com.taskmanager.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.IOException;

public class TaskRepository implements TaskRepositoryInterface {
  private final Map<String, Task> tasks = new HashMap<>();
  private final File dataFile;
  private ObjectMapper objectMapper;

  public TaskRepository() {
    dataFile = Utils.getTasksFile();
    loadObjectMapper();
    loadTasks();
  }

  public void loadObjectMapper() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  private void loadTasks() {
    try {
      if (dataFile.exists() && dataFile.length() > 0) {
        List<Task> taskList = objectMapper.readValue(dataFile, new TypeReference<List<Task>>() {
        });
        for (Task task : taskList) {
          tasks.put(task.getId(), task);
        }
        System.out.println("Loaded " + tasks.size() + " tasks from " + dataFile);
      }
    } catch (IOException e) {
      System.err.println("Error loading tasks: " + e.getMessage());
    }
  }

  public void saveTasks() throws TaskManagerException {
    try {
      objectMapper.writeValue(dataFile, fetchAll());
    } catch (IOException e) {
      throw new TaskManagerException("Failed to save tasks to file", e);
    }
  }

  @Override
  public void save(Task task) throws TaskManagerException {
    if (task == null) {
      throw new TaskManagerException("Task cannot be null");
    }
    if (task.getId() == null || task.getId().trim().isEmpty()) {
      throw new TaskManagerException("Task ID cannot be null or empty");
    }

    tasks.put(task.getId(), task);
    saveTasks();
  }

  @Override
  public Task findById(String id) throws TaskManagerException {
    if (id == null || id.trim().isEmpty()) {
      throw new TaskManagerException("Task ID cannot be null or empty");
    }

    Task task = tasks.get(id);
    if (task == null) {
      throw new TaskManagerException("Task with ID '" + id + "' not found");
    }

    return task;
  }

  @Override
  public void deleteById(String id) throws TaskManagerException {
    Task removedTask = tasks.remove(id);
    if (removedTask == null) {
      throw new TaskManagerException("Task with ID '" + id + "' not found");
    }
    saveTasks();
  }

  @Override
  public List<Task> fetchAll() {
    return new ArrayList<>(tasks.values());
  }
}
