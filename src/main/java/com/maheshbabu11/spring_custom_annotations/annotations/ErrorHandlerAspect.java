package com.maheshbabu11.spring_custom_annotations.annotations;

import com.maheshbabu11.spring_custom_annotations.service.ErrorLog;
import com.maheshbabu11.spring_custom_annotations.service.ErrorLogRepository;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

@Aspect
@Component
public class ErrorHandlerAspect {

    private final ErrorLogRepository errorLogRepository;

    public ErrorHandlerAspect(ErrorLogRepository errorLogRepository) {
        this.errorLogRepository = errorLogRepository;
    }

    @Pointcut("@annotation(com.maheshbabu11.spring_custom_annotations.annotations.ErrorHandler)")
    public void handleException() {
    }

    @AfterThrowing(pointcut = "handleException()", throwing = "ex")
    public void afterThrowing(Exception ex) {
        System.out.println("Exception occurred: " + ex.getMessage());
        ErrorLog errorLog = new ErrorLog();
        errorLog.setErrorLogId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE); // Generate a unique ID
        errorLog.setExceptionMessage(ex.getMessage());
        errorLog.setExceptionStackTrace(getStackTraceAsString(ex));
        errorLogRepository.save(errorLog);
    }

    private String getStackTraceAsString(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }
}
