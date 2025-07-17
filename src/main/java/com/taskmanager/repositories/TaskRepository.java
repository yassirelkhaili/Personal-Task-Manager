package com.taskmanager.repositories;

import com.taskmanager.interfaces.TaskRepositoryInterface;
import com.taskmanager.models.Task;
import com.taskmanager.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.IOException;

public class TaskRepository implements TaskRepositoryInterface {
  private final Map<String, Task> tasks = new HashMap<>();
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final File dataFile;

  public TaskRepository() {
    dataFile = Utils.getTasksFile(); 
    loadTasks();
  }

  private void loadTasks() {
    try {
      if (dataFile.exists() && dataFile.length() > 0) {
        List<Task> taskList = objectMapper.readValue(dataFile, new TypeReference<List<Task>>() {});
        for (Task task : taskList) {
          tasks.put(task.getId(), task);
        }
        System.out.println("Loaded " + tasks.size() + " tasks from " + dataFile);
      }
    } catch (IOException e) {
      System.err.println("Error occured loading data: " + e.getMessage());
    }
  }

  @Override
  public void save(Task task) {
    tasks.put(task.getId(), task);
  }

  @Override
  public Task findById(String id) {
    return tasks.get(id);
  }

  @Override
  public void deleteById(String id) {
    tasks.remove(id);
  }

  @Override
  public List<Task> findAll() {
    return new ArrayList<>(tasks.values());
  }
}
