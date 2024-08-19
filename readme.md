
## How to Create Custom Annotations in Spring Boot for Cleaner Code

![Photo by [Emile Perron](https://unsplash.com/@emilep?utm_source=medium&utm_medium=referral) on [Unsplash](https://unsplash.com?utm_source=medium&utm_medium=referral)](https://cdn-images-1.medium.com/max/11014/0*ZlW1rcLRVIUZs-v7)

Have you ever found yourself stuck writing the same lines of code over and over again, wishing there was a way to simplify the process? If you’re working with Spring Boot, you’re likely familiar with how powerful annotations can be in making your code cleaner and more efficient. But what happens when the existing annotations don’t quite fit your needs?

That’s where custom annotations come into play. Imagine being able to create your own annotations that encapsulate repetitive code patterns, making your code not only cleaner but also easier to maintain. In this article, we’re going to explore how you can craft custom annotations in Spring Boot that will streamline your development process and help you avoid the headache of redundant code. Whether you’re looking to enforce best practices, add custom validations, or just simplify your logic, custom annotations might just be the tool you need. Let’s dive in and start making your code work smarter, not harder!

Let me share an example: I faced a situation where I needed to log all the errors in my application to an error log table. It quickly became tiresome to repeatedly add the same logging code to every method that required it. I’ll show you how I solved this issue by using custom annotations.

The idea behind this custom annotation is to eliminate the need for repetitive error-handling code in your methods. Instead of manually wrapping every method in a try-catch block and logging exceptions, you can simply add your custom annotation. This annotation will act as a flag, signalling that any errors occurring within the method should be automatically logged. Under the hood, the annotation will leverage Spring’s Aspect-Oriented Programming (AOP) capabilities to intercept method calls, catch any exceptions, and log them according to your predefined format.

This approach not only reduces boilerplate code but also ensures consistency in how errors are handled and logged across your entire application. By centralizing your error-handling logic within the annotation, you can easily manage and update your logging strategy without touching the individual methods themselves. It’s a powerful way to keep your codebase clean, maintainable, and free from redundant error-handling code.

Let's get started!

I’m assuming you’re integrating this into an existing project. If not, you can head over to [spring.io](https://start.spring.io/) to create a new Spring Boot project.

## Step 1: Add the Spring AOP dependency to your project

You can visit the Maven [*repository](https://mvnrepository.com/artifact/org.springframework/spring-aop) *to find the latest dependency, or you can use the one provided below:

    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-aop</artifactId>
        <version>6.1.12</version>
    </dependency>

## Step 2: Create the Custom Annotation

Define a custom annotation that will be used to mark methods for error handling. Here’s how you can create an ErrorHandler annotation:

    import java.lang.annotation.ElementType;
    import java.lang.annotation.Retention;
    import java.lang.annotation.RetentionPolicy;
    import java.lang.annotation.Target;
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ErrorHandler {
    }

The first step toward creating a custom annotation is to declare it using the ***@interface ***keyword. In this instance ErrorHandler. The next step is to provide metadata about the code, and @Retention and @Target are two important elements that define how and where these annotations can be used.

## @Retention

The @Retention annotation specifies how long an annotation should be retained:

* **RetentionPolicy.SOURCE**: The annotation is retained only in the source code and is discarded by the compiler during compilation. It is not included in the compiled .class files or available at runtime.

* **RetentionPolicy.CLASS**: The annotation is retained in the compiled .class files but is not available at runtime. This is the default behavior if @Retention is not specified.

* **RetentionPolicy.RUNTIME**: The annotation is retained in the compiled .class files and is available at runtime through reflection. This allows the annotation to be used by frameworks and libraries at runtime.

In our ErrorHandler annotation, @Retention(RetentionPolicy.RUNTIME) is used. This means that the annotation will be available at runtime, which is necessary for frameworks or custom logic that needs to inspect the annotation during the application's execution.

## @Target

The @Target annotation specifies the kinds of elements to which the annotation can be applied:

* **ElementType.TYPE**: The annotation can be applied to classes, interfaces, or enums.

* **ElementType.FIELD**: The annotation can be applied to fields (variables).

* **ElementType.METHOD**: The annotation can be applied to methods.

* **ElementType.PARAMETER**: The annotation can be applied to method parameters.

* **ElementType.CONSTRUCTOR**: The annotation can be applied to constructors.

* **ElementType.LOCAL_VARIABLE**: The annotation can be applied to local variables.

* **ElementType.ANNOTATION_TYPE**: The annotation can be applied to other annotations.

* **ElementType.PACKAGE**: The annotation can be applied to package declarations.

For the ErrorHandler annotation, @Target(ElementType.METHOD) specifies that the annotation can only be applied to methods. This means you can use @ErrorHandler to mark methods where you want to apply custom error handling logic, but it cannot be applied to classes, fields, or other elements.

Now lets define the ErrorHandler aspect.

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

* The **@Aspect **annotation marks the class as an Aspect, which allows it to contain advice that will be applied to the join points defined by the pointcuts.

* The **@Pointcut**annotation defines a pointcut named handleException that matches any method annotated with @ErrorHandler. This is the target for the advice.

* The **@AfterThrowing**annotation specifies that the afterThrowing method should be invoked after a method that matches the handleException pointcut throws an exception. The throwing parameter binds the thrown exception to the ex parameter of the advice method.

* The **afterThrowing **method logs the exception message to the console and creates an ErrorLog object, populates it with details from the exception, including a unique ID, stack trace and Saves the ErrorLog object using the errorLogRepository.

* The **getStackTraceAsString **method converts the stack trace of an exception into a string format, which is useful for logging and debugging.

Now lets add this annotation to our method and see how it works.

## **Step 3: Add custom annotation to the required method.**

Lets add our custom annotation to our method and see what happens when an exception occurs in that method.

       @ErrorHandler
        public void testExceptionLogging() {
    
            //simulate an exception
            if (true) {
                throw new RuntimeException("Exception occurred");
            }
        }

As soon as the method is invoked a new Runtime exception gets thrown which causes the the @AfterThrowing advice in ErrorHandlerAspect to be triggered. This in turn prints the exception message to the console and logs the error in the error log table.

![Error Log entry in the table](https://cdn-images-1.medium.com/max/2670/1*Bxj8hqvh9-0iQmUv5u_grg.png)

The complete source code used in the article is available at :
[**GitHub - MaheshBabu11/spring-custom-annotations: This is a demo project to show how to use custom…**](https://github.com/MaheshBabu11/spring-custom-annotations)



In conclusion, custom annotations in Spring Boot, like the **@ErrorHandler** annotation, provide a powerful mechanism for automating repetitive tasks and enhancing code maintainability. By leveraging Spring AOP, you can streamline error handling and logging, ensuring that exceptions are consistently recorded and managed without cluttering your business logic with repetitive try-catch blocks. This approach not only simplifies your code but also centralizes error handling, making it easier to update and maintain. With custom annotations, you can focus more on your application’s core functionality while relying on robust, automated mechanisms to handle cross-cutting concerns like error logging.

Happy Coding 😊!!! Leave a 👏 if you enjoyed reading it.


