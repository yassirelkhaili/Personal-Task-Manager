package com.taskmanager;

import java.io.File;

public abstract class Utils {
  public static final String DATA_DIRECTORY = "src/main/java/com/taskmanager/data";
  public static final String TASKS_FILENAME = "tasks.json";

  public static File getTasksFile() {
    File dataDir = new File(DATA_DIRECTORY);
    if (!dataDir.exists()) {
      dataDir.mkdirs();
    }
    return new File(dataDir, TASKS_FILENAME);
  }
}