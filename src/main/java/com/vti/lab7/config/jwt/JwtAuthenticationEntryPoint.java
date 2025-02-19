package com.vti.lab7.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vti.lab7.constant.ErrorMessage;
import com.vti.lab7.util.BeanUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		MessageSource messageSource = BeanUtil.getBean(MessageSource.class);
		String message = messageSource.getMessage(ErrorMessage.ERR_UNAUTHORIZED, null, LocaleContextHolder.getLocale());
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getOutputStream().write((new ObjectMapper().writeValueAsBytes(message)));
	}
}
