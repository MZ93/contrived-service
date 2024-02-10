package com.example.demo.testutils;

import com.example.demo.job.model.Task;
import java.util.List;

public class TaskUtils {

  public static Task createIndependentTask(String taskName) {
    var task = new Task();
    task.setName(taskName);
    task.setCommand(taskName + " command");
    task.setRequires(List.of());
    return task;
  }

  public static Task createDependentTask(String taskName, String... dependsOn) {
    var task = new Task();
    task.setName(taskName);
    task.setCommand(taskName + " command");
    task.setRequires(List.of(dependsOn));
    return task;
  }
}
