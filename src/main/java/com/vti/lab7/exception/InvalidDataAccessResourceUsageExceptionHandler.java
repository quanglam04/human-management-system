package com.vti.lab7.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.vti.lab7.dto.response.ErrorResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestControllerAdvice
@RequiredArgsConstructor
public class InvalidDataAccessResourceUsageExceptionHandler {
	private final MessageSource messageSource;

	private String getMessage(String key) {
		return messageSource.getMessage(key, null, "Default message", LocaleContextHolder.getLocale());
	}
	
	@ExceptionHandler(InvalidDataAccessResourceUsageException.class)
	public ResponseEntity<Object> handleAll(InvalidDataAccessResourceUsageException exception) {
	    String errorMessage = exception.getMessage(); 
	    String message = getMessage("Exception.message"); 
	    
	    int code = 404;
	    String moreInformation = "http://localhost:8080/api/v1/exception/404";

	    ErrorResponse response = new ErrorResponse(message, errorMessage, null, code, moreInformation);
	    log.error(errorMessage, exception);
	    
	    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}


}
