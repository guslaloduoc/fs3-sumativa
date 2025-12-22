#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.ms_usuarios.repository;

import ${package}.ms_usuarios.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Role
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Busca un rol por nombre
     * @param name Nombre del rol (ej: ADMIN, LAB_TECH, DOCTOR)
     * @return Optional con el rol si existe
     */
    Optional<Role> findByName(String name);
}
