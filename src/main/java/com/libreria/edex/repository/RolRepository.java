package com.libreria.edex.repository;

import com.libreria.edex.model.Rol;
import com.libreria.edex.model.enums.RolNombre;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombre(RolNombre nombre);
}
