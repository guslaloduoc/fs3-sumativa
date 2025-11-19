package com.sumativa.ms_usuarios.exception;

import com.sumativa.ms_usuarios.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Manejador global de excepciones para ms-users
 * Centraliza el manejo de errores y proporciona respuestas consistentes
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja errores de validación de Bean Validation (@Valid)
     * Retorna 400 Bad Request con detalles de los campos inválidos
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Error de validación en los datos enviados",
                request.getRequestURI()
        );
        errorResponse.setFieldErrors(fieldErrors);

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Maneja IllegalArgumentException
     * Utilizado para errores de lógica de negocio
     * Retorna 400 Bad Request por defecto
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        // Determinar si es un error 404 (Not Found) o 400 (Bad Request)
        HttpStatus status = ex.getMessage().toLowerCase().contains("no encontrad")
                || ex.getMessage().toLowerCase().contains("not found")
                || ex.getMessage().toLowerCase().contains("no existe")
            ? HttpStatus.NOT_FOUND
            : HttpStatus.BAD_REQUEST;

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * Maneja NoSuchElementException
     * Retorna 404 Not Found
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponseDto> handleNoSuchElementException(
            NoSuchElementException ex,
            HttpServletRequest request) {

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage() != null ? ex.getMessage() : "Recurso no encontrado",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Maneja cualquier otra excepción no contemplada
     * Retorna 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Ha ocurrido un error inesperado en el servidor",
                request.getRequestURI()
        );

        // En producción, NO exponer el mensaje de excepción interno
        // Solo en desarrollo para debugging
        // errorResponse.setMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
