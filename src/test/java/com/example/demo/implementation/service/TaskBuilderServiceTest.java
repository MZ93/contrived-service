package com.example.demo.implementation.service;

import static com.example.demo.testutils.TaskUtils.createDependentTask;
import static com.example.demo.testutils.TaskUtils.createIndependentTask;

import com.example.demo.job.exceptions.TaskSortException;
import com.example.demo.job.implementation.service.TaskExecutionOrderServiceImpl;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TaskBuilderServiceTest {

  @Autowired private TaskExecutionOrderServiceImpl taskBuilderService;

  @Test
  public void testTwoIndependentTasks() {
    var firstTask = createIndependentTask("firstTask");
    var secondTask = createIndependentTask("secondTask");
    var tasks = taskBuilderService.getTasksInExecutionOrder(List.of(firstTask, secondTask));

    Assertions.assertEquals(firstTask, tasks.get(0));
    Assertions.assertEquals(secondTask, tasks.get(1));
  }

  @Test
  public void testSingleIndependentTask() {
    var firstTask = createIndependentTask("task");
    var tasks = taskBuilderService.getTasksInExecutionOrder(List.of(firstTask));

    Assertions.assertEquals(firstTask, tasks.get(0));
  }

  @Test
  public void testEmptyList() {
    Assertions.assertThrows(
        TaskSortException.class, () -> taskBuilderService.getTasksInExecutionOrder(List.of()));
  }

  @Test
  public void testIndependentAndDependentTask() {
    var initialTask = createIndependentTask("task1");
    var dependentTask = createDependentTask("task2", "task1");

    var tasks = taskBuilderService.getTasksInExecutionOrder(List.of(dependentTask, initialTask));

    Assertions.assertEquals(initialTask, tasks.get(0));
    Assertions.assertEquals(dependentTask, tasks.get(1));
  }

  @Test
  public void testDependentTaskWithNoPresentDependent() {
    var initialTask = createIndependentTask("task1");
    var dependentTask = createDependentTask("task2", "notPresent");

    Assertions.assertThrows(
        TaskSortException.class,
        () -> taskBuilderService.getTasksInExecutionOrder(List.of(initialTask, dependentTask)));
  }

  @Test
  public void testDependentAndIndependentTasks() {
    var task1 = createIndependentTask("task1");
    var task2 = createDependentTask("task2", "task3");
    var task3 = createDependentTask("task3", "task1");
    var task4 = createDependentTask("task4", "task2", "task3");

    var tasks = taskBuilderService.getTasksInExecutionOrder(List.of(task1, task2, task3, task4));

    Assertions.assertEquals(task1, tasks.get(0));
    Assertions.assertEquals(task3, tasks.get(1));
    Assertions.assertEquals(task2, tasks.get(2));
    Assertions.assertEquals(task4, tasks.get(3));
  }

  @Test
  public void testCyclicTasks() {
    var task1 = createIndependentTask("task1");
    var task2 = createDependentTask("task2", "task3", "task1");
    var task3 = createDependentTask("task3", "task2");

    Assertions.assertThrows(
        TaskSortException.class,
        () -> taskBuilderService.getTasksInExecutionOrder(List.of(task1, task2, task3)));
  }

  @Test
  public void testAddDependentTaskBeforeDependency() {
    var task1 = createIndependentTask("task1");
    var task2 = createDependentTask("task2", "task3");
    var task3 = createDependentTask("task3", "task1");

    var tasks = taskBuilderService.getTasksInExecutionOrder(List.of(task1, task2, task3));

    Assertions.assertEquals(task1, tasks.get(0));
    Assertions.assertEquals(task3, tasks.get(1));
    Assertions.assertEquals(task2, tasks.get(2));
  }
}
