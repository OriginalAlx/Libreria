package com.libreria.edex.repository;

import com.libreria.edex.model.ProductoImagen;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoImagenRepository extends JpaRepository<ProductoImagen, Long> {
    
    /**
     * Encuentra todas las imágenes de un producto ordenadas
     */
    List<ProductoImagen> findByProductoIdOrderByPrincipalDescOrdenAsc(Long productoId);
    
    /**
     * Encuentra la imagen principal de un producto
     */
    ProductoImagen findByProductoIdAndPrincipalTrue(Long productoId);
    
    /**
     * Encuentra todas las imágenes de un producto
     */
    List<ProductoImagen> findByProductoId(Long productoId);
    
    /**
     * Elimina todas las imágenes de un producto
     */
    void deleteByProductoId(Long productoId);
}
