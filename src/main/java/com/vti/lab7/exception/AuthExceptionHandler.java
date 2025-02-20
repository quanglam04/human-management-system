package com.vti.lab7.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.vti.lab7.dto.response.ErrorResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@Component
@RequiredArgsConstructor
public class AuthExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

	private final MessageSource messageSource;

	private String getMessage(String key) {
		return messageSource.getMessage(key, null, "Default message", LocaleContextHolder.getLocale());
	}

	// Spring Security
	// 401 unauthorized
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
			throws IOException, ServletException {

		String message = getMessage("AuthenticationException.message");
		String detailMessage = exception.getLocalizedMessage();
		int code = 401;
		String moreInformation = "http://localhost:8080/api/v1/exception/401";

		ErrorResponse errorResponse = new ErrorResponse(message, detailMessage, null, code, moreInformation);
		log.error(detailMessage, exception);

		// convert object to json
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(errorResponse);

		// return json
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(code);
		response.getWriter().write(json);
	}

	// Spring Security
	// 403 Forbidden
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception)
			throws IOException, ServletException {

		String message = getMessage("AccessDeniedException.message");
		String detailMessage = exception.getLocalizedMessage();
		int code = 403;
		String moreInformation = "http://localhost:8080/api/v1/exception/403";

		ErrorResponse errorResponse = new ErrorResponse(message, detailMessage, null, code, moreInformation);
		log.error(detailMessage, exception);

		// convert object to json
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(errorResponse);

		// return json
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(code);
		response.getWriter().write(json);
	}

}
