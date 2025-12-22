package com.sumativa.ms_results.exception;

import com.sumativa.ms_results.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para GlobalExceptionHandler
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/api/resultados/1");
    }

    // ==================== handleIllegalArgument Tests ====================

    @Test
    void handleIllegalArgument_shouldReturn404_whenMessageContainsNoEncontrado() {
        // Arrange
        IllegalArgumentException ex = new IllegalArgumentException("Resultado no encontrado con ID: 999");

        // Act
        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleIllegalArgument(ex, request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Not Found", response.getBody().getError());
        assertEquals("Resultado no encontrado con ID: 999", response.getBody().getMessage());
        assertEquals("/api/resultados/1", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleIllegalArgument_shouldReturn400_whenMessageDoesNotContainNoEncontrado() {
        // Arrange
        IllegalArgumentException ex = new IllegalArgumentException("El tipo de análisis es requerido");

        // Act
        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleIllegalArgument(ex, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("El tipo de análisis es requerido", response.getBody().getMessage());
    }

    @Test
    void handleIllegalArgument_shouldReturn400_whenMessageIsGeneric() {
        // Arrange
        IllegalArgumentException ex = new IllegalArgumentException("Valor inválido proporcionado");

        // Act
        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleIllegalArgument(ex, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
    }

    // ==================== handleValidationErrors Tests ====================

    @Test
    void handleValidationErrors_shouldReturn400WithFieldErrors() {
        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError1 = new FieldError("resultado", "paciente", "El nombre del paciente es obligatorio");
        FieldError fieldError2 = new FieldError("resultado", "estado", "El estado es obligatorio");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));
        when(ex.getMessage()).thenReturn("Validation failed");

        // Act
        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleValidationErrors(ex, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Validation Error", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("Errores de validación"));
        assertTrue(response.getBody().getMessage().contains("paciente"));
        assertTrue(response.getBody().getMessage().contains("estado"));
    }

    @Test
    void handleValidationErrors_shouldHandleSingleFieldError() {
        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError = new FieldError("resultado", "laboratorioId", "El ID del laboratorio es obligatorio");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(fieldError));
        when(ex.getMessage()).thenReturn("Validation failed");

        // Act
        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleValidationErrors(ex, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("laboratorioId"));
    }

    // ==================== handleGenericException Tests ====================

    @Test
    void handleGenericException_shouldReturn500() {
        // Arrange
        Exception ex = new RuntimeException("Unexpected database error");

        // Act
        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleGenericException(ex, request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("Ocurrió un error inesperado en el servidor", response.getBody().getMessage());
        assertEquals("/api/resultados/1", response.getBody().getPath());
    }

    @Test
    void handleGenericException_shouldReturn500_forNullPointerException() {
        // Arrange
        Exception ex = new NullPointerException("Something was null");

        // Act
        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleGenericException(ex, request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
    }

    @Test
    void handleGenericException_shouldReturn500_forAnyException() {
        // Arrange
        Exception ex = new IllegalStateException("Invalid state");

        // Act
        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleGenericException(ex, request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody().getTimestamp());
    }
}
