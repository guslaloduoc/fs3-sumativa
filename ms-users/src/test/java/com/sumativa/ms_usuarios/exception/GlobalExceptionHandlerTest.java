package com.sumativa.ms_usuarios.exception;

import com.sumativa.ms_usuarios.dto.ErrorResponseDto;
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
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/api/users/1");
    }

    @Test
    void handleValidationException_shouldReturn400WithFieldErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError1 = new FieldError("user", "email", "El email es obligatorio");
        FieldError fieldError2 = new FieldError("user", "fullName", "El nombre es obligatorio");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleValidationException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertNotNull(response.getBody().getFieldErrors());
        assertEquals(2, response.getBody().getFieldErrors().size());
    }

    @Test
    void handleValidationException_shouldHandleSingleError() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError = new FieldError("user", "password", "El password es obligatorio");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(fieldError));

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleValidationException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getFieldErrors().containsKey("password"));
    }

    @Test
    void handleIllegalArgumentException_shouldReturn404_whenNotFound() {
        IllegalArgumentException ex = new IllegalArgumentException("Usuario no encontrado con id: 999");

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleIllegalArgumentException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Not Found", response.getBody().getError());
    }

    @Test
    void handleIllegalArgumentException_shouldReturn404_whenNotFoundEnglish() {
        IllegalArgumentException ex = new IllegalArgumentException("User not found with id: 999");

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleIllegalArgumentException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleIllegalArgumentException_shouldReturn404_whenNoExiste() {
        IllegalArgumentException ex = new IllegalArgumentException("El usuario no existe");

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleIllegalArgumentException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleIllegalArgumentException_shouldReturn400_forOtherErrors() {
        IllegalArgumentException ex = new IllegalArgumentException("El email ya está registrado");

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleIllegalArgumentException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
    }

    @Test
    void handleIllegalArgumentException_shouldIncludeMessage() {
        String errorMessage = "Dominio de email no autorizado";
        IllegalArgumentException ex = new IllegalArgumentException(errorMessage);

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleIllegalArgumentException(ex, request);

        assertEquals(errorMessage, response.getBody().getMessage());
    }

    @Test
    void handleNoSuchElementException_shouldReturn404() {
        NoSuchElementException ex = new NoSuchElementException("Elemento no encontrado");

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleNoSuchElementException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Not Found", response.getBody().getError());
    }

    @Test
    void handleNoSuchElementException_shouldUseDefaultMessage_whenNull() {
        NoSuchElementException ex = new NoSuchElementException();

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleNoSuchElementException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Recurso no encontrado", response.getBody().getMessage());
    }

    @Test
    void handleGenericException_shouldReturn500() {
        Exception ex = new RuntimeException("Error inesperado");

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleGenericException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("Ha ocurrido un error inesperado en el servidor", response.getBody().getMessage());
    }

    @Test
    void handleGenericException_shouldNotExposeInternalMessage() {
        Exception ex = new NullPointerException("Sensitive internal error");

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleGenericException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().getMessage().contains("Sensitive"));
    }

    @Test
    void allHandlers_shouldIncludePath() {
        IllegalArgumentException ex = new IllegalArgumentException("Test error");

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleIllegalArgumentException(ex, request);

        assertEquals("/api/users/1", response.getBody().getPath());
    }

    @Test
    void allHandlers_shouldIncludeTimestamp() {
        IllegalArgumentException ex = new IllegalArgumentException("Test error");

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleIllegalArgumentException(ex, request);

        assertNotNull(response.getBody().getTimestamp());
    }
}
