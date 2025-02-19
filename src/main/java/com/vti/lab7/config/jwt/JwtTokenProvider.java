package com.vti.lab7.config.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.vti.lab7.config.CustomUserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

	private static final String CLAIM_TYPE = "type";
	private static final String TYPE_ACCESS = "access";
	private static final String TYPE_REFRESH = "refresh";
	private static final String CARD_NUMBER_KEY = "card-number";
	private static final String AUTHORITIES_KEY = "auth";

	@Value("${jwt.secret:76947ef7-7af1-4745-bfda-ab2d5cb09290}")
	private String SECRET_KEY;

	@Value("${jwt.access.expiration_time:60}")
	private int EXPIRATION_TIME_ACCESS_TOKEN;

	@Value("${jwt.refresh.expiration_time:1440}")
	private int EXPIRATION_TIME_REFRESH_TOKEN;

	public String generateToken(CustomUserDetails userDetails, Boolean isRefreshToken) {
		String authorities = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));
		Map<String, Object> claim = new HashMap<>();
		claim.put(CLAIM_TYPE, isRefreshToken ? TYPE_REFRESH : TYPE_ACCESS);
		claim.put(AUTHORITIES_KEY, authorities);
		if (isRefreshToken) {
			return Jwts.builder().setClaims(claim).setSubject(String.valueOf(userDetails.getUserId()))
					.setIssuedAt(new Date(System.currentTimeMillis()))
					.setExpiration(new Date(System.currentTimeMillis() + (EXPIRATION_TIME_REFRESH_TOKEN * 60L * 1000L)))
					.signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
		}
		return Jwts.builder().setClaims(claim).setSubject(String.valueOf(userDetails.getUserId()))
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + (EXPIRATION_TIME_ACCESS_TOKEN * 60L * 1000L)))
				.signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
			return true;
		} catch (SignatureException ex) {
			log.error("Invalid JWT signature");
		} catch (MalformedJwtException ex) {
			log.error("Invalid JWT token");
		} catch (ExpiredJwtException ex) {
			log.error("Expired JWT token");
		} catch (UnsupportedJwtException ex) {
			log.error("Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			log.error("JWT claims string is empty");
		}
		return false;
	}

	public String extractSubjectFromJwt(String token) {
		return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
	}

	public String extractClaimCardNumber(String token) {
		Object cardNumber = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody()
				.get(CARD_NUMBER_KEY);
		if (cardNumber != null) {
			return cardNumber.toString();
		}
		return null;
	}

}
