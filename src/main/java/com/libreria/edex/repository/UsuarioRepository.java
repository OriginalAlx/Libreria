package com.libreria.edex.repository;

import com.libreria.edex.model.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByTokenVerificacion(String tokenVerificacion);

    Optional<Usuario> findByTokenResetPassword(String tokenResetPassword);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
