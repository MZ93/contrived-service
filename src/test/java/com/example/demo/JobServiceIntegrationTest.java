package com.example.demo;

import static com.example.demo.testutils.TaskUtils.createDependentTask;
import static com.example.demo.testutils.TaskUtils.createIndependentTask;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.job.model.Job;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class JobServiceIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void testJsonResponse() throws Exception {

    var task1 = createIndependentTask("task1");
    var task2 = createDependentTask("task2", "task3");
    var task3 = createDependentTask("task3", "task1");
    var task4 = createDependentTask("task4", "task2", "task3");

    String tasks;
    tasks = objectMapper.writeValueAsString(new Job(List.of(task1, task2, task3, task4)));

    var result =
        mockMvc
            .perform(
                post("/api/job/rank-tasks").content(tasks).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    var expected = objectMapper.writeValueAsString(List.of(task1, task3, task2, task4));

    Assertions.assertEquals(expected, result);
  }

  @Test
  void testTextResponse() throws Exception {

    var task1 = createIndependentTask("task1");
    var task2 = createDependentTask("task2", "task3");
    var task3 = createDependentTask("task3", "task1");
    var task4 = createDependentTask("task4", "task2", "task3");

    var tasks = objectMapper.writeValueAsString(new Job(List.of(task1, task2, task3, task4)));

    var result =
        mockMvc
            .perform(
                post("/api/job/create-script")
                    .content(tasks)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    var expected =
        task1.getCommand()
            + System.lineSeparator()
            + task3.getCommand()
            + System.lineSeparator()
            + task2.getCommand()
            + System.lineSeparator()
            + task4.getCommand();
    Assertions.assertEquals(expected, result);
  }
}
