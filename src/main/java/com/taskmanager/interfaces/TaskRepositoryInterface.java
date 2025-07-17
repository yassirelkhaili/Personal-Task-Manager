package com.taskmanager.interfaces;

import java.util.List;

import com.taskmanager.models.Task;

public interface TaskRepositoryInterface {
  void save(Task task);

  Task findById(String id);

  void deleteById(String id);

  List<Task> findAll();
}
