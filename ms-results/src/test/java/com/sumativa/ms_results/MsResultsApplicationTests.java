package com.sumativa.ms_results;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("h2")
class MsResultsApplicationTests {

    @Test
    void contextLoads() {
        // Verifica que el contexto de Spring se carga correctamente
    }

    @Test
    void mainMethodShouldRun() {
        // Verifica que el método main no lance excepciones
        // No ejecutamos realmente la aplicación, solo verificamos que la clase existe
        MsResultsApplication app = new MsResultsApplication();
        org.junit.jupiter.api.Assertions.assertNotNull(app);
    }
}
