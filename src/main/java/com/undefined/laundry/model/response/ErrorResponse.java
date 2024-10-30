package com.undefined.laundry.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
	private String message;
	private int status;
	private long timestamp;
}
