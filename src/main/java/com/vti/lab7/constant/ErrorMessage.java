package com.vti.lab7.constant;

public class ErrorMessage {

	public static final String ERR_RESOURCE_NOT_FOUND = "error.resource.not.found";
	public static final String ERR_EXCEPTION_GENERAL = "error.error.general";
	public static final String ERR_FORBIDDEN = "error.forbidden";
	public static final String ERR_NO_RESOURCE_FOUND = "error.no.resource.found";
	public static final String ERR_NO_HANDLER_FOUND = "error.no.handler.found";
	public static final String ERR_METHOD_NOT_SUPPORTED = "error.method.not.supported";
	public static final String ERR_MEDIA_TYPE_NOT_SUPPORTED = "error.media.type.not.supported";
	public static final String ERR_HTTP_MESSAGE_NOT_READABLE = "error.http.message.not.readable";
	public static final String ERR_ARGUMENT_NOT_VALID = "error.argument.not.valid";
	public static final String ERR_CONSTRAINT_VIOLATION = "error.constraint.violation";
	public static final String ERR_MISSING_PARAMETER = "error.missing.parameter";
	public static final String ERR_TYPE_MISMATCH = "error.type.mismatch";
	public static final String ERR_UNAUTHORIZED = "error.unauthorized";
	public static final String ERR_ACCESS_DENIED = "error.access.denied";
	public static final String ERR_NOT_FOUND_EXCEPTION = "error.not.found";
	public static final String ERR_BAD_REQUEST = "error.bad.request";
	public static final String ERR_CONFLICT = "error.conflict";
	public static final String ERR_DATA_INTEGRITY = "error.data.integrity";

	public static final String INVALID_NOT_BLANK_FIELD = "invalid.general.not-blank";
	public static final String INVALID_FORMAT_EMAIL = "invalid.email-format";
	public static final String INVALID_FORMAT_PHONE = "invalid.phone-format";
	public static final String INVALID_TEXT_LENGTH = "invalid.text.length";

	public static class Role {
		public static final String ERR_NOT_FOUND_ID = "error.role.not.found.id";
		public static final String ERR_NOT_FOUND_NAME = "error.role.not.found.name";
	}

	public static class User {
		public static final String ERR_NOT_FOUND_USERNAME = "error.user.not.found.username";
		public static final String ERR_NOT_FOUND_EMAIL = "error.user.not.found.email";
		public static final String ERR_NOT_FOUND_ID = "error.user.not.found.id";
		public static final String ERR_USER_ALREADY_ASSIGNED = "error.user.already.assigned.to.employee";
	}

	public static class Employee {
		public static final String ERR_NOT_FOUND_ID = "error.employee.not.found.id";
	}

	public static class Position {
		public static final String ERR_NOT_FOUND_ID = "error.position.not.found.id";
	}

	public static class Department {
		public static final String ERR_NOT_FOUND_ID = "error.department.not.found.id";
	}

	public static class Permission {
		public static final String ERR_NOT_FOUND_ID = "error.permission.not.found.id";
		public static final String ERR_DUPLICATE_NAME = "error.permission.duplicate.username.email";
	}
}
