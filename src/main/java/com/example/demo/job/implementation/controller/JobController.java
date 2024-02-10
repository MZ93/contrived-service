package com.example.demo.job.implementation.controller;

import com.example.demo.job.exceptions.TaskSortException;
import com.example.demo.job.interfaces.service.TaskExecutionOrderService;
import com.example.demo.job.model.Job;
import com.example.demo.job.model.Task;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
public class JobController {

  private final TaskExecutionOrderService taskBuilderService;

  @PostMapping("/rank-tasks")
  public ResponseEntity<List<Task>> handleJobExecutionRequest(@RequestBody Job job) {
    return ResponseEntity.ok(taskBuilderService.getTasksInExecutionOrder(job.getTasks()));
  }

  @PostMapping("/create-script")
  public ResponseEntity<String> handleJobExecutionRequestAsText(@RequestBody Job job) {
    return ResponseEntity.ok(
        taskBuilderService.getTasksInExecutionOrder(job.getTasks()).stream()
            .map(Task::getCommand)
            .collect(Collectors.joining(System.lineSeparator())));
  }

  @ExceptionHandler(TaskSortException.class)
  public ResponseEntity<String> handleTaskSortException(TaskSortException exception) {
    return ResponseEntity.status(exception.getHttpStatusCode()).body(exception.getMessage());
  }
}
