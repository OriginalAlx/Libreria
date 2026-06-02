package com.libreria.edex.config;

import com.libreria.edex.model.Producto;
import com.libreria.edex.model.ProductoImagen;
import com.libreria.edex.model.Rol;
import com.libreria.edex.model.enums.RolNombre;
import com.libreria.edex.repository.ProductoRepository;
import com.libreria.edex.repository.RolRepository;
import com.libreria.edex.service.ProductoImagenService;
import java.util.List;
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

    @Bean
    CommandLineRunner initImagenes(ProductoRepository productoRepository, 
                                   ProductoImagenService productoImagenService) {
        return args -> {
            try {
                logger.info("🔄 Verificando migración de imágenes...");
                migrateImagenes(productoRepository, productoImagenService);
                logger.info("✅ Migración de imágenes completada");
            } catch (Exception e) {
                logger.warn("⚠️  Error en migración de imágenes: {}", e.getMessage());
            }
        };
    }

    private void crearRolSiNoExiste(RolRepository rolRepository, RolNombre nombre, String descripcion) {
        rolRepository.findByNombre(nombre).orElseGet(() ->
                rolRepository.save(new Rol(nombre, descripcion)));
    }

    /**
     * Migra imágenes de url_imagen a la tabla producto_imagen
     */
    private void migrateImagenes(ProductoRepository productoRepository, 
                                 ProductoImagenService productoImagenService) {
        // Obtener productos con url_imagen que NO tengan imágenes en la relación
        List<Producto> productosConImagenes = productoRepository.findAll().stream()
                .filter(p -> p.getUrlImagen() != null && !p.getUrlImagen().trim().isEmpty())
                .filter(p -> p.getImagenes() == null || p.getImagenes().isEmpty())
                .toList();

        if (productosConImagenes.isEmpty()) {
            logger.debug("✓ No hay imágenes para migrar");
            return;
        }

        int totalMigrados = 0;

        for (Producto producto : productosConImagenes) {
            String urlImagen = producto.getUrlImagen();
            
            // Dividir URLs por punto y coma
            String[] urls = urlImagen.split(";");
            
            for (int i = 0; i < urls.length; i++) {
                String url = urls[i].trim();
                if (!url.isEmpty()) {
                    ProductoImagen imagen = new ProductoImagen(
                            producto,
                            url,
                            i + 1,  // orden (1-based)
                            i == 0  // primera es principal
                    );
                    productoImagenService.save(imagen);
                    totalMigrados++;
                }
            }
        }

        if (totalMigrados > 0) {
            logger.info("✅ Migradas {} imágenes a tabla producto_imagen", totalMigrados);
        }
    }
}
