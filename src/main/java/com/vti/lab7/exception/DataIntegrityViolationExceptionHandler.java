	package com.vti.lab7.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
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
public class DataIntegrityViolationExceptionHandler {
	private final MessageSource messageSource;

	private String getMessage(String key) {
		return messageSource.getMessage(key, null, "Default message", LocaleContextHolder.getLocale());
	}
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Object> handleAll(Exception exception) {
		String message = getMessage("Exception.message");
		String detailMessage = exception.getLocalizedMessage();
		int code = 400;
		String moreInformation = "http://localhost:8080/api/v1/exception/500";

		ErrorResponse response = new ErrorResponse(message, "Du lieu nhap vao khong hop le", null, code, moreInformation);
		log.error(detailMessage, exception);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

}
