/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.libreria.edex.service;

import com.libreria.edex.model.Producto;
import com.libreria.edex.repository.ProductoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 *
 * @author alx
 */
@Service
public class ProductoService {
    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }

    public List<Producto> findByFilters(String categoria, Double min, Double max, Boolean disponible) {
        if (categoria != null && min != null && max != null && disponible != null) {
            return productoRepository.findByCategoriaAndPrecioBetweenAndDisponible(categoria, min, max, disponible);
        } else if (categoria != null) {
            return productoRepository.findByCategoria(categoria);
        } else if (min != null && max != null) {
            return productoRepository.findByPrecioBetween(min, max);
        } else if (disponible != null) {
            return productoRepository.findByDisponible(disponible);
        }
        return productoRepository.findAll();
    }

    public List<Producto> findDisponibles() {
        return productoRepository.findByDisponibleTrue();
    }

    public List<Producto> searchByNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Producto> findByProveedor(String proveedor) {
        return productoRepository.findByProveedor(proveedor);
    }

    public Producto findBySku(String sku) {
        return productoRepository.findBySku(sku);
    }

    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    public void delete(Long id) {
        productoRepository.deleteById(id);
    }

    /**
     * Obtiene todas las categorías únicas de productos
     * @return Lista de categorías ordenadas alfabéticamente
     */
    public List<String> findDistinctCategorias() {
        return productoRepository.findDistinctCategorias();
    }
}
