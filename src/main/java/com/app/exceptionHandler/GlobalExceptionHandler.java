package com.app.exceptionHandler;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.app.exception.ActivatedAlreadyException;
import com.app.exception.AppException;
import com.app.exception.AuthenticationFailedException;
import com.app.exception.BlockedAlreadyException;
import com.app.exception.CardNotFoundException;
import com.app.exception.InvalidRefreshTokenException;
import com.app.exception.NotEnoughBalanceException;
import com.app.exception.NotYourCardException;
import com.app.exception.UserNotFoundException;
import com.app.exception.ValidationValueException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<?> handleInvalidRefreshTokenException(InvalidRefreshTokenException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorBody(ex));
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<?> handleAuthenticationFailedException(AuthenticationFailedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getErrorBody(ex));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorBody(ex));
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<?> handleCardNotFoundException(CardNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorBody(ex));
    }

    @ExceptionHandler(NotYourCardException.class)
    public ResponseEntity<?> handleNotYourCardException(NotYourCardException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorBody(ex));
    }

    @ExceptionHandler(NotEnoughBalanceException.class)
    public ResponseEntity<?> handleNotEnoughBalanceException(NotEnoughBalanceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorBody(ex));
    }

    @ExceptionHandler(BlockedAlreadyException.class)
    public ResponseEntity<?> handleBlockedAlreadyException(BlockedAlreadyException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorBody(ex));
    }

    @ExceptionHandler(ActivatedAlreadyException.class)
    public ResponseEntity<?> handleActivatedAlreadyException(ActivatedAlreadyException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorBody(ex));
    }

    @ExceptionHandler(ValidationValueException.class)
    public ResponseEntity<?> handleValidationException(ValidationValueException ex) {
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorBody(ex));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        StringBuilder loggerMessage = new StringBuilder();
        loggerMessage.append(ex.getMessage()).append("\n").append(ex.getClass().toString());
        for (StackTraceElement elem : ex.getStackTrace()) {
            loggerMessage.append(elem.toString()).append("\n");
        }
        logger.error(loggerMessage.toString());
        
        StringBuilder message = new StringBuilder();
        message.append(ex.getMessage()).append("\n").append(ex.getClass().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Exception: " + message);
    }

    private Map<String, Object> getErrorBody(AppException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("Error", ex.getMessage());
        body.put("ErrorCode", ex.getErrorCode());
        return body;
    }

    private Map<String, Object> getErrorBody(ValidationValueException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("ValidationErrors", ex.getValidationErrors());
        body.put("ErrorCode", ex.getErrorCode());
        body.put("Error", ex.getMessage());
        return body;
    }
}
