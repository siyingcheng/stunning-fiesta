package com.simon.stunningfiesta.system.exception;


import com.simon.stunningfiesta.artifact.ArtifactNotFoundException;
import com.simon.stunningfiesta.system.Result;
import com.simon.stunningfiesta.system.StatusCode;
import com.simon.stunningfiesta.wizard.WizardNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionHandleAdvice {

    @ExceptionHandler(ArtifactNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Result handleArtifactNotFoundException(ArtifactNotFoundException ex) {
        return Result.of(false)
                .withCode(StatusCode.NOT_FOUND)
                .withMessage(ex.getMessage());
    }

    @ExceptionHandler(WizardNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Result handleWizardNotFoundException(WizardNotFound ex) {
        return Result.of(false)
                .withCode(StatusCode.NOT_FOUND)
                .withMessage(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Result handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .collect(Collectors.toMap(
                        objectError -> ((FieldError) objectError).getField(),
                        ObjectError::getDefaultMessage));
        return Result.of(false)
                .withCode(StatusCode.INVALID_ARGUMENT)
                .withMessage("Provided arguments are invalid, see data for details.")
                .withData(errorMap);
    }
}
