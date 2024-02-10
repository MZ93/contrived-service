package com.example.demo.job.implementation.service;

import com.example.demo.job.exceptions.TaskSortException;
import com.example.demo.job.interfaces.service.TaskExecutionOrderService;
import com.example.demo.job.model.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.springframework.stereotype.Service;

/**
 * This class' implementation is based on <a
 * href="https://en.wikipedia.org/wiki/Topological_sorting">...</a>
 */
@Service
public class TaskExecutionOrderServiceImpl implements TaskExecutionOrderService {

  public List<Task> getTasksInExecutionOrder(List<Task> tasks) {

    Graph<Task, DefaultEdge> tasksGraph = buildTasksGraph(tasks);

    List<Task> tasksInExecutionOrder = new ArrayList<>();
    var iterator = new TopologicalOrderIterator<>(tasksGraph);
    iterator.forEachRemaining(tasksInExecutionOrder::add);

    return tasksInExecutionOrder;
  }

  private Graph<Task, DefaultEdge> buildTasksGraph(List<Task> tasks) {

    Graph<Task, DefaultEdge> tasksGraph = new DirectedAcyclicGraph<>(DefaultEdge.class);
    populateGraphVertices(tasksGraph, tasks);
    createEdges(tasksGraph, tasks);

    return tasksGraph;
  }

  private void populateGraphVertices(Graph<Task, DefaultEdge> graph, List<Task> tasks) {
    tasks.forEach(graph::addVertex);
  }

  private void createEdges(Graph<Task, DefaultEdge> graph, List<Task> tasks) {
    try {
      createIndependentEdges(graph, tasks);
      createDependentEdges(graph, tasks);
    } catch (IllegalArgumentException e) {
      throw new TaskSortException(
          "Cycle in your tasks, job can't complete. Reorder tasks and remove cyclic "
              + "task dependency");
    }
  }

  private void createIndependentEdges(Graph<Task, DefaultEdge> graph, List<Task> tasks) {
    Task previous = null;
    for (Task task : tasks) {
      if (task.getRequires().isEmpty()) {
        if (previous != null) {
          graph.addEdge(previous, task);
        }
        previous = task;
      }
    }
    if (previous == null) {
      throw new TaskSortException(
          "No start task provided. Include at least one start task  without a dependent task.");
    }
  }

  private void createDependentEdges(Graph<Task, DefaultEdge> graph, List<Task> tasks) {
    Map<String, Task> taskMap = new HashMap<>();
    for (var task : tasks) {
      taskMap.put(task.getName(), task);
    }

    tasks.forEach(
        task -> {
          if (!task.getRequires().isEmpty()) {
            for (var dependentTask : task.getRequires()) {
              Task requiredTask = findTaskByName(taskMap, dependentTask);
              graph.addEdge(requiredTask, task);
            }
          }
        });
  }

  private Task findTaskByName(Map<String, Task> taskMap, String taskName) {
    if (taskMap.containsKey(taskName)) {
      return taskMap.get(taskName);
    } else {
      throw new TaskSortException("Task " + taskName + " not found. Include missing task.");
    }
  }
}
