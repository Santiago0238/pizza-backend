package com.taller.proye01.exception;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class Error {
	private String mensaje;
	private String error;
	private int status;
	private Date date;
	
}
