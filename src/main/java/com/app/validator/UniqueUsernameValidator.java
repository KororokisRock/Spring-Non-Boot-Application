package com.app.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.app.repository.UserRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


@Component
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

    @Autowired
    UserRepository userRepo;

    private static final Logger logger = LoggerFactory.getLogger(UniqueUsernameValidator.class);
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext ) {
        if (value == null) {
            return true;
        }
        logger.info("Current field: " + value);
        boolean existsInDb = userRepo.existsByUsername(value);
        logger.info("Exist in db: " + existsInDb);
        return !existsInDb;
    }
}
