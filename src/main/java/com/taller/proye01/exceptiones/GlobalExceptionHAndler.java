package com.taller.proye01.exceptiones;


import java.util.Date;

import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHAndler {
	
	@ExceptionHandler(ArithmeticException.class)
	public ResponseEntity<Error> divisionPorCero(Exception ex){
		
		Error erro = new Error();
		erro.setMensaje(ex.getMessage());
		erro.setError("Error dicvison por cero");
		erro.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		erro.setDate(new Date());
		
		return ResponseEntity.internalServerError().body(erro);	
	}

	@ExceptionHandler(NumberFormatException.class)
	public ResponseEntity<Error> CaracteresErorr(Exception ex){
		
		Error erro = new Error();
		erro.setMensaje(ex.getMessage());
		erro.setError("Error , caracteres mal definidos ");
		erro.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		erro.setDate(new Date());
		
		return ResponseEntity.internalServerError().body(erro);
			
	}
	@ExceptionHandler(PSQLException.class)
	public ResponseEntity<Error> ErrorConsulta(Exception ex){
		Error erro = new Error();
		erro.setMensaje(ex.getMessage());
		erro.setError("Error , Clave foranea no relacionada");
		erro.setStatus(HttpStatus.BAD_REQUEST.value());
		erro.setDate(new Date());
		
		return ResponseEntity.badRequest().body(erro);	
	}
	
	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<Error> ErrorEnLaBD(Exception ex){
		Error erro = new Error();
		erro.setMensaje(ex.getMessage());
		erro.setError("Error , Base de datos no encontrada ");
		erro.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		erro.setDate(new Date());
		
		return ResponseEntity.badRequest().body(erro);	
	}
	

}
