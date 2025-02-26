package com.vti.lab7.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

public class JwtUtil {

	public static String extractTokenFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	public static String extractRefreshTokenFromRequest(HttpServletRequest request) {
		return request.getHeader("X-Refresh-Token");
	}

}