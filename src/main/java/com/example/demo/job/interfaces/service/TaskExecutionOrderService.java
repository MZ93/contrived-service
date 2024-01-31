package com.example.demo.job.interfaces.service;

import com.example.demo.job.model.Task;

import java.util.List;

public interface TaskExecutionOrderService {

  List<Task> getTasksInExecutionOrder(List<Task> tasks);
}
