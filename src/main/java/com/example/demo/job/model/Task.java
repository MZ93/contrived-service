package com.example.demo.job.model;

import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@Getter
public class Task {
  private String name;
  private String command;
  private List<String> requires = List.of();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Task task = (Task) o;
    return Objects.equals(name, task.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
