package com.maheshbabu11.spring_custom_annotations.service;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "error_log")
@Getter
@Setter
@NoArgsConstructor
public class ErrorLog {

    @Id
    @Column(name = "error_log_id")
    private Long errorLogId;

    @Column(name = "exception_message", length = Integer.MAX_VALUE)
    private String exceptionMessage;

    @Column(name = "exception_stack_trace", length = Integer.MAX_VALUE)
    private String exceptionStackTrace;

}

