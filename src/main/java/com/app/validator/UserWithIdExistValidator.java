package com.app.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.app.repository.UserRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class UserWithIdExistValidator implements ConstraintValidator<UserWithIdExist, Integer> {
    @Autowired
    private UserRepository userRepo;

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext ) {
        return value == null || userRepo.existsById(value);
    }
}
