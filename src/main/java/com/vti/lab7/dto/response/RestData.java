package com.vti.lab7.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestData<T> {

	private int status;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String error;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Object message;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T data;

}