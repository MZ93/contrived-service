package com.example.demo.job.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class TaskSortException extends RuntimeException {

  @Getter private final HttpStatusCode httpStatusCode;
  private final String exceptionReason;

  public TaskSortException(String exceptionReason) {
    this.exceptionReason = exceptionReason;
    httpStatusCode = HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value());
  }

  @Override
  public String getMessage() {
    return "Failed to process task list. Please fix the request. Reason: " + exceptionReason;
  }
}
