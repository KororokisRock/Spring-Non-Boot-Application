package com.app.aspect;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.app.exception.ValidationValueException;

@Aspect
@Component
public class ValidationAspect {

    @Around("@annotation(com.app.annotation.ValidateBindingResult)")
    public Object validateBindingResult(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof BindingResult result) {
                if (result.hasErrors()) {
                    Map<String, String> errors = new HashMap<>();
                    for (FieldError error : result.getFieldErrors()) {
                        errors.put(error.getField(), error.getDefaultMessage());
                    }
                   throw new ValidationValueException(errors);
                }
            }
        }

        return joinPoint.proceed();
    }
}