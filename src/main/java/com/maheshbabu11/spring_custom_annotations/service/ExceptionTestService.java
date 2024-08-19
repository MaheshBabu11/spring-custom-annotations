package com.maheshbabu11.spring_custom_annotations.service;

import com.maheshbabu11.spring_custom_annotations.annotations.ErrorHandler;
import org.springframework.stereotype.Service;

@Service
public class ExceptionTestService {

    @ErrorHandler
    public void testExceptionLogging(){
        throw new RuntimeException("Exception occurred");
    }
}
