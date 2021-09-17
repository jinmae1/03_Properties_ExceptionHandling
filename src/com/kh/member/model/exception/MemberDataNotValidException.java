package com.kh.member.model.exception;

public class MemberDataNotValidException extends MemberException {

	public MemberDataNotValidException() {
		super();
	}

	public MemberDataNotValidException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MemberDataNotValidException(String message, Throwable cause) {
		super(message, cause);
	}

	public MemberDataNotValidException(String message) {
		super(message);
	}

	public MemberDataNotValidException(Throwable cause) {
		super(cause);
	}

}
