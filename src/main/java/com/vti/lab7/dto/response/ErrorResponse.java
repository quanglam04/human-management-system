package com.vti.lab7.dto.response;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
	private String message;
	private String detailMessage;
	private Map<String, String> errors;
	private int code;
	private String moreInformation;

}
