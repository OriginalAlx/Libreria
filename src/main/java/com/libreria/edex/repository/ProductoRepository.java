/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.libreria.edex.repository;

import com.libreria.edex.model.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author alx
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long>{
    // Buscar por categoría
    List<Producto> findByCategoria(String categoria);

    // Buscar por rango de precios
    List<Producto> findByPrecioBetween(Double min, Double max);

    // Buscar por disponibilidad
    List<Producto> findByDisponible(boolean disponible);

    // Combinar filtros
    List<Producto> findByCategoriaAndPrecioBetweenAndDisponible(
            String categoria, Double min, Double max, boolean disponible
    );
}
