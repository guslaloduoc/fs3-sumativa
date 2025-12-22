package com.sumativa.ms_laboratorios.config;

import com.sumativa.ms_laboratorios.entity.Asignacion;
import com.sumativa.ms_laboratorios.entity.Laboratorio;
import com.sumativa.ms_laboratorios.repository.AsignacionRepository;
import com.sumativa.ms_laboratorios.repository.LaboratorioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private LaboratorioRepository laboratorioRepository;

    @Mock
    private AsignacionRepository asignacionRepository;

    @InjectMocks
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        // Reset mocks before each test
    }

    @Test
    void run_shouldNotInitializeData_whenDataAlreadyExists() throws Exception {
        when(laboratorioRepository.count()).thenReturn(5L);

        dataInitializer.run();

        verify(laboratorioRepository).count();
        verify(laboratorioRepository, never()).save(any(Laboratorio.class));
        verify(asignacionRepository, never()).save(any(Asignacion.class));
    }

    @Test
    void run_shouldInitializeData_whenNoDataExists() throws Exception {
        when(laboratorioRepository.count()).thenReturn(0L);
        when(laboratorioRepository.save(any(Laboratorio.class)))
                .thenAnswer(invocation -> {
                    Laboratorio lab = invocation.getArgument(0);
                    lab.setId((long) (Math.random() * 100));
                    return lab;
                });
        when(asignacionRepository.save(any(Asignacion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        dataInitializer.run();

        verify(laboratorioRepository).count();
        verify(laboratorioRepository, times(3)).save(any(Laboratorio.class));
        verify(asignacionRepository, times(5)).save(any(Asignacion.class));
    }

    @Test
    void run_shouldCreate3Laboratorios() throws Exception {
        when(laboratorioRepository.count()).thenReturn(0L);
        when(laboratorioRepository.save(any(Laboratorio.class)))
                .thenAnswer(invocation -> {
                    Laboratorio lab = invocation.getArgument(0);
                    lab.setId(1L);
                    return lab;
                });
        when(asignacionRepository.save(any(Asignacion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        dataInitializer.run();

        verify(laboratorioRepository, times(3)).save(any(Laboratorio.class));
    }

    @Test
    void run_shouldCreate5Asignaciones() throws Exception {
        when(laboratorioRepository.count()).thenReturn(0L);
        when(laboratorioRepository.save(any(Laboratorio.class)))
                .thenAnswer(invocation -> {
                    Laboratorio lab = invocation.getArgument(0);
                    lab.setId(1L);
                    return lab;
                });
        when(asignacionRepository.save(any(Asignacion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        dataInitializer.run();

        verify(asignacionRepository, times(5)).save(any(Asignacion.class));
    }

    @Test
    void run_shouldHandleRunWithArguments() throws Exception {
        when(laboratorioRepository.count()).thenReturn(0L);
        when(laboratorioRepository.save(any(Laboratorio.class)))
                .thenAnswer(invocation -> {
                    Laboratorio lab = invocation.getArgument(0);
                    lab.setId(1L);
                    return lab;
                });
        when(asignacionRepository.save(any(Asignacion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        dataInitializer.run("arg1", "arg2");

        verify(laboratorioRepository).count();
    }
}
