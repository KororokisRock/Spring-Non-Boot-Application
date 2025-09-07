package com.app.validator;

import java.time.LocalDate;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.app.dto.FilterPageCardDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


@Component
public class FilterPageCardValidator implements ConstraintValidator<FilterPageCardValid, FilterPageCardDTO> {
    
    private static final Logger logger = LoggerFactory.getLogger(FilterPageCardValidator.class);

    private static final String sortByRegex = "id|card_number|owner_id|validity_period|status|balance";
    private static final String directionSortRegex = "asc|desc";
    

    @Override
    public boolean isValid(FilterPageCardDTO object, ConstraintValidatorContext constraintValidatorContext ) {
        String username = object.getUsername();
        logger.info("FilterPageCardValidator Username: {}", username);
        if (username != null) {
            if (username.length() > 50) {
                return false;
            }
        }

        logger.info("FilterPageCardValidator Direction sort: {}", object.getDirectionSort());
        if (!Pattern.compile(directionSortRegex, Pattern.CASE_INSENSITIVE).matcher(object.getDirectionSort()).matches()) {
            return false;
        }

        logger.info("FilterPageCardValidator sort by: {}", object.getSortBy());
        if (!Pattern.compile(sortByRegex, Pattern.CASE_INSENSITIVE).matcher(object.getSortBy()).matches()) {
            return false;
        }

        logger.info("FilterPageCardValidator page: {}", object.getPage());
        if (object.getPage() < 0) {
            return false;
        }

        logger.info("FilterPageCardValidator size: {}", object.getSize());
        if (object.getSize() <= 0) {
            return false;
        }


        String cardNumber = object.getCardNumber();
        logger.info("FilterPageCardValidator cardnumber: {}", cardNumber);
        if (cardNumber != null) {
            if (!Pattern.compile("^[0-9]{0,16}$").matcher(object.getCardNumber()).matches()) {
                return false;
            }
        }

        LocalDate minEndDate = object.getMinEndDate();
        logger.info("FilterPageCardValidator min end date: {}", minEndDate);
        if (minEndDate != null) {
            if (LocalDate.now().isAfter(minEndDate)) {
                return false;
            }
        }

        LocalDate maxEndDate = object.getMaxEndDate();
        logger.info("FilterPageCardValidator max end date: {}", maxEndDate);
        if (maxEndDate != null) {
            if (LocalDate.now().isAfter(maxEndDate)) {
                logger.info("FilterPageCardValidator max end date: 1");
                return false;
            }
            if (minEndDate != null && minEndDate.isAfter(maxEndDate)) {
                logger.info("FilterPageCardValidator max end date: 2");
                return false;
            }
        }

        logger.info("FilterPageCardValidator min and max balance compare (min >= max): {}", object.getMinBalance() >= object.getMaxBalance());
        if (object.getMinBalance() > object.getMaxBalance()) {
            return false;
        }
        
        return true;
    }
}
