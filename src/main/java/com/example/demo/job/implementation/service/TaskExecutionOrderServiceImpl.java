package com.example.demo.job.implementation.service;

import com.example.demo.job.exceptions.TaskSortException;
import com.example.demo.job.model.Task;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.job.interfaces.service.TaskExecutionOrderService;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.graph.GraphCycleProhibitedException;
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
    } catch (GraphCycleProhibitedException e) {
      throw new TaskSortException(
          "Cycle in your tasks, job can't complete. Reorder tasks and remove cyclic "
              + "task dependency");
    }
  }

  private void createIndependentEdges(Graph<Task, DefaultEdge> graph, List<Task> tasks) {
    var previous =
        tasks.stream()
            .filter(task1 -> task1.getRequires().isEmpty())
            .findFirst()
            .orElseThrow(
                () ->
                    new TaskSortException(
                        "No start task found! Include at least one task without a "
                            + "dependent task."));

    var independentTasks =
        tasks.stream().filter(task -> task.getRequires().isEmpty()).skip(1).toList();

    for (var task : independentTasks) {
      graph.addEdge(previous, task);
      previous = task;
    }
  }

  private void createDependentEdges(Graph<Task, DefaultEdge> graph, List<Task> tasks) {
    tasks.stream()
        .filter(task -> !task.getRequires().isEmpty())
        .forEach(
            task -> {
              for (var dependentTask : task.getRequires()) {
                Task requiredTask = findTaskByName(graph, dependentTask);
                graph.addEdge(requiredTask, task);
              }
            });
  }

  private Task findTaskByName(Graph<Task, DefaultEdge> graph, String taskName) {
    return graph.vertexSet().stream()
        .filter(task -> task.getName().equals(taskName))
        .findFirst()
        .orElseThrow(
            () ->
                new TaskSortException(
                    "Task dependency on a task with name: "
                        + taskName
                        + " not found. Include missing task."));
  }
}
