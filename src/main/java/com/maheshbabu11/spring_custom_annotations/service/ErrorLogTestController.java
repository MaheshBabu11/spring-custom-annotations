package com.maheshbabu11.spring_custom_annotations.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorLogTestController {
    private final ExceptionTestService exceptionTestService;

    public ErrorLogTestController(ExceptionTestService exceptionTestService) {
        this.exceptionTestService = exceptionTestService;
    }

    @GetMapping("/test")
    public void testErrorLog() {
        exceptionTestService.testExceptionLogging();
    }
}
