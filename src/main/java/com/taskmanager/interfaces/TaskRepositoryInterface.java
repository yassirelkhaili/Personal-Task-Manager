package com.taskmanager.interfaces;

import java.util.List;

import com.taskmanager.errors.TaskManagerException;
import com.taskmanager.models.Task;

public interface TaskRepositoryInterface {
  void save(Task task) throws TaskManagerException;

  Task findById(String id) throws TaskManagerException;

  void deleteById(String id) throws TaskManagerException;

  List<Task> findAll();
}
