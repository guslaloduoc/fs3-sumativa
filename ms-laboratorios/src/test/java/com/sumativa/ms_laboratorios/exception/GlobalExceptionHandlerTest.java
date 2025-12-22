package com.sumativa.ms_laboratorios.exception;

import com.sumativa.ms_laboratorios.dto.ErrorResponseDto;
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

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/api/laboratorios");
    }

    @Test
    void handleValidationException_shouldReturnBadRequest() {
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);

        FieldError fieldError = new FieldError("laboratorio", "nombre", "El nombre es obligatorio");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(fieldError));

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleValidationException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertNotNull(response.getBody().getFieldErrors());
        assertTrue(response.getBody().getFieldErrors().containsKey("nombre"));
    }

    @Test
    void handleValidationException_shouldHandleMultipleErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);

        FieldError fieldError1 = new FieldError("laboratorio", "nombre", "El nombre es obligatorio");
        FieldError fieldError2 = new FieldError("laboratorio", "direccion", "La direccion es muy larga");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleValidationException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(2, response.getBody().getFieldErrors().size());
    }

    @Test
    void handleIllegalArgumentException_shouldReturn404_whenNotFoundMessage() {
        IllegalArgumentException ex = new IllegalArgumentException("Laboratorio no encontrado con id: 1");

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleIllegalArgumentException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("no encontrado"));
    }

    @Test
    void handleIllegalArgumentException_shouldReturn404_whenNotFoundEnglish() {
        IllegalArgumentException ex = new IllegalArgumentException("Resource not found");

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleIllegalArgumentException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleIllegalArgumentException_shouldReturn404_whenNoExisteMessage() {
        IllegalArgumentException ex = new IllegalArgumentException("El recurso no existe");

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleIllegalArgumentException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleIllegalArgumentException_shouldReturn400_forOtherErrors() {
        IllegalArgumentException ex = new IllegalArgumentException("El nombre ya existe");

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleIllegalArgumentException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    void handleNoSuchElementException_shouldReturn404() {
        NoSuchElementException ex = new NoSuchElementException("Elemento no encontrado");

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleNoSuchElementException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Elemento no encontrado", response.getBody().getMessage());
    }

    @Test
    void handleNoSuchElementException_shouldReturn404_withDefaultMessage() {
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
        assertEquals("Ha ocurrido un error inesperado en el servidor", response.getBody().getMessage());
    }

    @Test
    void handleGenericException_shouldNotExposeInternalMessage() {
        Exception ex = new RuntimeException("Database connection failed: password=secret");

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleGenericException(ex, request);

        assertFalse(response.getBody().getMessage().contains("secret"));
        assertFalse(response.getBody().getMessage().contains("Database"));
    }
}
