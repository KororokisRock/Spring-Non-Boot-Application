package com.app.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.app.repository.CardRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class NoOneHasNumberValidator implements ConstraintValidator<NoOneHasNumber, String> {
    @Autowired
    private CardRepository cardRepo;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || !cardRepo.existsByCardNumber(value);
    }
    
}
