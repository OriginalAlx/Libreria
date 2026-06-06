package com.libreria.edex.config;

import com.libreria.edex.model.Rol;
import com.libreria.edex.model.enums.RolNombre;
import com.libreria.edex.repository.RolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initRoles(RolRepository rolRepository) {
        return args -> {
            crearRolSiNoExiste(rolRepository, RolNombre.ADMIN, "Administrador del sistema");
            crearRolSiNoExiste(rolRepository, RolNombre.EMPLEADO, "Empleado / vendedor");
            crearRolSiNoExiste(rolRepository, RolNombre.CLIENTE, "Cliente registrado");
        };
    }

    private void crearRolSiNoExiste(RolRepository rolRepository, RolNombre nombre, String descripcion) {
        rolRepository.findByNombre(nombre).orElseGet(() ->
                rolRepository.save(new Rol(nombre, descripcion)));
    }
}
