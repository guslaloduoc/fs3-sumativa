#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.repository;

import ${package}.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad User
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por email (ignorando mayúsculas/minúsculas)
     * @param email Email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Verifica si existe un usuario con el email dado (ignorando mayúsculas/minúsculas)
     * @param email Email a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByEmailIgnoreCase(String email);
}
