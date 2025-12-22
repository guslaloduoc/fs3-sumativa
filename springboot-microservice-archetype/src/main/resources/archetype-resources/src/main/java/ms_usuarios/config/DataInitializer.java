#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.ms_usuarios.config;

import ${package}.ms_usuarios.entity.Role;
import ${package}.ms_usuarios.entity.User;
import ${package}.ms_usuarios.repository.RoleRepository;
import ${package}.ms_usuarios.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Set;

/**
 * Inicializador de datos para modo desarrollo (perfil 'dev')
 * Inserta roles y usuarios de prueba en H2
 */
@Configuration
@Profile("dev")  // Solo se ejecuta con perfil dev
@Slf4j
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(RoleRepository roleRepository, UserRepository userRepository) {
        return args -> {
            log.info("========================================");
            log.info("Inicializando datos de prueba (modo dev)");
            log.info("========================================");

            // Crear roles
            Role roleAdmin = roleRepository.save(new Role("ADMIN"));
            Role roleLabTech = roleRepository.save(new Role("LAB_TECH"));
            Role roleDoctor = roleRepository.save(new Role("DOCTOR"));

            log.info("✓ Roles creados: ADMIN, LAB_TECH, DOCTOR");

            // Crear usuarios de prueba
            User admin = new User();
            admin.setFullName("Administrador Sistema");
            admin.setEmail("admin@hospital.cl");
            admin.setPasswordHash("admin123");
            admin.setEnabled(true);
            admin.setRoles(Set.of(roleAdmin));
            userRepository.save(admin);

            User doctor = new User();
            doctor.setFullName("Dr. Juan Pérez");
            doctor.setEmail("juan.perez@hospital.cl");
            doctor.setPasswordHash("doctor123");
            doctor.setEnabled(true);
            doctor.setRoles(Set.of(roleDoctor));
            userRepository.save(doctor);

            User labTech = new User();
            labTech.setFullName("María González");
            labTech.setEmail("maria.gonzalez@hospital.cl");
            labTech.setPasswordHash("lab123");
            labTech.setEnabled(true);
            labTech.setRoles(Set.of(roleLabTech));
            userRepository.save(labTech);

            log.info("✓ Usuarios creados: admin, doctor, lab tech");
            log.info("========================================");
            log.info("Datos de prueba listos!");
            log.info("========================================");
            log.info("");
            log.info("Credenciales de prueba:");
            log.info("  Admin:    admin@hospital.cl / admin123");
            log.info("  Doctor:   juan.perez@hospital.cl / doctor123");
            log.info("  Lab Tech: maria.gonzalez@hospital.cl / lab123");
            log.info("");
            log.info("Endpoints disponibles:");
            log.info("  POST http://localhost:8081/api/users/login");
            log.info("  GET  http://localhost:8081/api/users");
            log.info("  Consola H2: http://localhost:8081/h2-console");
            log.info("========================================");
        };
    }
}
