package com.vti.lab7.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.vti.lab7.constant.ErrorMessage;
import com.vti.lab7.dto.response.ErrorResponse;
import com.vti.lab7.exception.custom.*;

import org.springframework.validation.ObjectError;
import org.springframework.validation.FieldError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import static com.vti.lab7.constant.ErrorMessage.*;

@Log4j2
@RestControllerAdvice
public class RestExceptionHandler {

	@Autowired
	private MessageSource messageSource;

	private String getMessage(String key) {
		return getMessage(key, null);
	}

	private String getMessage(String key, Object[] params) {
		return messageSource.getMessage(key, params, "Default message", LocaleContextHolder.getLocale());
	}

	private String getMoreInformationUrl(int code) {
		return "http://localhost:8080/api/v1/exception/" + code;
	}

	// Default exception
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleAll(Exception exception) {
		String message = getMessage(ERR_EXCEPTION_GENERAL);
		String detailMessage = exception.getLocalizedMessage();
		int code = 500;
		String moreInformation = getMoreInformationUrl(code);

		ErrorResponse response = new ErrorResponse(message, detailMessage, null, code, moreInformation);
		log.error(detailMessage, exception);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException exception) {
	    String message = ErrorMessage.ERR_UNAUTHORIZED;
	    String detailMessage = "Sai tài khoản hoặc mật khẩu";
	    int code = 401;
	    String moreInformation = "http://localhost:8080/api/v1/exception/401";

	    ErrorResponse response = new ErrorResponse(message, detailMessage, null, code, moreInformation);
	    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	}


	// Forbidden handler
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException exception) {
		String message = getMessage(ERR_FORBIDDEN);
		String detailMessage = exception.getLocalizedMessage();
		int code = 403;
		String moreInformation = getMoreInformationUrl(code);

		ErrorResponse response = new ErrorResponse(message, detailMessage, null, code, moreInformation);
		log.error(detailMessage, exception);
		return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
	}

	// Not found url handler
	@ExceptionHandler(NoHandlerFoundException.class)
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException exception) {
		String message = getMessage(ERR_NO_HANDLER_FOUND) + exception.getHttpMethod() + " " + exception.getRequestURL();
		String detailMessage = exception.getLocalizedMessage();
		int code = 404;
		String moreInformation = getMoreInformationUrl(code);

		ErrorResponse response = new ErrorResponse(message, detailMessage, null, code, moreInformation);
		log.error(detailMessage, exception);
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	// Not support HTTP Method
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
			HttpRequestMethodNotSupportedException exception) {
		String message = getMessageFromHttpRequestMethodNotSupportedException(exception);
		String detailMessage = exception.getLocalizedMessage();
		int code = 405;
		String moreInformation = getMoreInformationUrl(code);

		ErrorResponse response = new ErrorResponse(message, detailMessage, null, code, moreInformation);
		log.error(detailMessage, exception);
		return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
	}

	private String getMessageFromHttpRequestMethodNotSupportedException(
			HttpRequestMethodNotSupportedException exception) {
		String message = exception.getMethod() + " " + getMessage(ERR_METHOD_NOT_SUPPORTED);
		for (HttpMethod method : exception.getSupportedHttpMethods()) {
			message += method + " ";
		}
		return message;
	}

	// Not support media type
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException exception) {
		String message = getMessageFromHttpMediaTypeNotSupportedException(exception);
		String detailMessage = exception.getLocalizedMessage();
		int code = 415;
		String moreInformation = getMoreInformationUrl(code);

		ErrorResponse response = new ErrorResponse(message, detailMessage, null, code, moreInformation);
		log.error(detailMessage, exception);
		return new ResponseEntity<>(response, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}

	private String getMessageFromHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exception) {
		String message = exception.getContentType() + " " + getMessage(ERR_MEDIA_TYPE_NOT_SUPPORTED);
		for (MediaType method : exception.getSupportedMediaTypes()) {
			message += method + ", ";
		}
		return message.substring(0, message.length() - 2);
	}

	// BindException: This exception is thrown when fatal binding errors occur.
	// MethodArgumentNotValidException: This exception is thrown when argument
	// annotated with @Valid failed validation:
	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
		String message = getMessage(ERR_ARGUMENT_NOT_VALID);
		String detailMessage = exception.getLocalizedMessage();
		// error
		Map<String, String> errors = new HashMap<>();
		for (ObjectError error : exception.getBindingResult().getAllErrors()) {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		}
		int code = 400;
		String moreInformation = getMoreInformationUrl(code);

		ErrorResponse response = new ErrorResponse(message, detailMessage, errors, code, moreInformation);
		log.error(detailMessage + "\n" + errors.toString(), exception);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	// bean validation error
	@SuppressWarnings("rawtypes")
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException exception) {
		String message = getMessage(ERR_CONSTRAINT_VIOLATION);
		String detailMessage = exception.getLocalizedMessage();
		// error
		Map<String, String> errors = new HashMap<>();
		for (ConstraintViolation violation : exception.getConstraintViolations()) {
			String fieldName = violation.getPropertyPath().toString();
			String errorMessage = violation.getMessage();
			errors.put(fieldName, errorMessage);
		}
		int code = 400;
		String moreInformation = getMoreInformationUrl(code);

		ErrorResponse response = new ErrorResponse(message, detailMessage, errors, code, moreInformation);
		log.error(detailMessage + "\n" + errors.toString(), exception);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	// MissingServletRequestPartException: This exception is thrown when when the
	// part of a multipart request not found
	// MissingServletRequestParameterException: This exception is thrown when
	// request missing parameter:
	@ExceptionHandler(MissingServletRequestParameterException.class)
	protected ResponseEntity<Object> handleMissingServletRequestParameter(
			MissingServletRequestParameterException exception) {
		String message = exception.getParameterName() + " " + getMessage(ERR_MISSING_PARAMETER);
		String detailMessage = exception.getLocalizedMessage();
		int code = 400;
		String moreInformation = getMoreInformationUrl(code);

		ErrorResponse response = new ErrorResponse(message, detailMessage, null, code, moreInformation);
		log.error(detailMessage, exception);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	// TypeMismatchException: This exception is thrown when try to set bean property
	// with wrong type.
	// MethodArgumentTypeMismatchException: This exception is thrown when method
	// argument is not the expected type:
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
		String message = exception.getName() + " " + getMessage(ERR_TYPE_MISMATCH)
				+ exception.getRequiredType().getName();
		String detailMessage = exception.getLocalizedMessage();
		int code = 400;
		String moreInformation = getMoreInformationUrl(code);

		ErrorResponse response = new ErrorResponse(message, detailMessage, null, code, moreInformation);
		log.error(detailMessage, exception);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException exception) {
		String message = getMessage(ERR_NO_RESOURCE_FOUND);
		String detailMessage = exception.getLocalizedMessage();
		int code = 404;
		String moreInformation = getMoreInformationUrl(code);

		ErrorResponse response = new ErrorResponse(message, detailMessage, null, code, moreInformation);
		log.error(detailMessage, exception);
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
		String message = getMessage(ERR_HTTP_MESSAGE_NOT_READABLE);
		String detailMessage = exception.getLocalizedMessage();
		int code = 400;
		String moreInformation = getMoreInformationUrl(code);

		ErrorResponse response = new ErrorResponse(message, detailMessage, null, code, moreInformation);
		log.error(detailMessage, exception);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<Object> handleNotFoundException(NotFoundException ex) {
		String message = getMessage(ex.getMessage(), ex.getParams());
		String detailMessage = ex.getLocalizedMessage();
		int code = HttpStatus.NOT_FOUND.value();
		String moreInformation = getMoreInformationUrl(code);

		ErrorResponse response = new ErrorResponse(message, detailMessage, null, code, moreInformation);
		log.error(detailMessage, ex);
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
		String message = getMessage(ex.getMessage(), ex.getParams());
		String detailMessage = ex.getLocalizedMessage();
		int code = HttpStatus.BAD_REQUEST.value();
		String moreInformation = getMoreInformationUrl(code);

		ErrorResponse response = new ErrorResponse(message, detailMessage, null, code, moreInformation);
		log.error(detailMessage, ex);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex) {
		String message = getMessage(ERR_UNAUTHORIZED);
		String detailMessage = ex.getLocalizedMessage();
		int code = HttpStatus.UNAUTHORIZED.value();
		String moreInformation = getMoreInformationUrl(code);

		ErrorResponse response = new ErrorResponse(message, detailMessage, null, code, moreInformation);
		log.error(detailMessage, ex);
		return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<Object> handleForbiddenException(ForbiddenException ex) {
		String message = getMessage(ERR_ACCESS_DENIED);
		String detailMessage = ex.getLocalizedMessage();
		int code = HttpStatus.FORBIDDEN.value();
		String moreInformation = getMoreInformationUrl(code);

		ErrorResponse response = new ErrorResponse(message, detailMessage, null, code, moreInformation);
		log.error(detailMessage, ex);
		return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<Object> handleConflictException(ConflictException exception) {
		String message = getMessage(exception.getMessage(), exception.getParams());
		String detailMessage = exception.getLocalizedMessage();
		int code = 409;
		String moreInformation = getMoreInformationUrl(code);

		ErrorResponse response = new ErrorResponse(message, detailMessage, null, code, moreInformation);
		log.error(detailMessage, exception);
		return new ResponseEntity<>(response, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
		String message = getMessage(ERR_DATA_INTEGRITY);
		String detailMessage = exception.getLocalizedMessage();
		int code = 409;
		String moreInformation = getMoreInformationUrl(code);

		ErrorResponse response = new ErrorResponse(message, detailMessage, null, code, moreInformation);
		log.error(detailMessage, exception);
		return new ResponseEntity<>(response, HttpStatus.CONFLICT);
	}

}