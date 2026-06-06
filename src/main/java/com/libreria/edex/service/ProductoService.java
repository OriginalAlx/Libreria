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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 *
 * @author alx
 */
@Service
public class ProductoService {
    private final ProductoRepository productoRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> findAll() {
        // Usar JOIN FETCH para cargar las imágenes junto con los productos
        String jpql = "SELECT DISTINCT p FROM Producto p LEFT JOIN FETCH p.imagenes ORDER BY p.id";
        return entityManager.createQuery(jpql, Producto.class).getResultList();
    }

    public Optional<Producto> findById(Long id) {
        // Usar JOIN FETCH para cargar las imágenes
        String jpql = "SELECT DISTINCT p FROM Producto p LEFT JOIN FETCH p.imagenes WHERE p.id = :id";
        List<Producto> resultados = entityManager.createQuery(jpql, Producto.class)
                .setParameter("id", id)
                .getResultList();
        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.get(0));
    }

    public List<Producto> findByFilters(String categoria, Double min, Double max, Boolean disponible) {
        List<Producto> resultados;
        if (categoria != null && min != null && max != null && disponible != null) {
            resultados = productoRepository.findByCategoriaAndPrecioBetweenAndDisponible(categoria, min, max, disponible);
        } else if (categoria != null) {
            resultados = productoRepository.findByCategoria(categoria);
        } else if (min != null && max != null) {
            resultados = productoRepository.findByPrecioBetween(min, max);
        } else if (disponible != null) {
            resultados = productoRepository.findByDisponible(disponible);
        } else {
            return findAll();
        }
        // Cargar imágenes para cada producto
        cargarImagenes(resultados);
        return resultados;
    }

    public List<Producto> findDisponibles() {
        List<Producto> resultados = productoRepository.findByDisponibleTrue();
        cargarImagenes(resultados);
        return resultados;
    }

    public List<Producto> searchByNombre(String nombre) {
        List<Producto> resultados = productoRepository.findByNombreContainingIgnoreCase(nombre);
        cargarImagenes(resultados);
        return resultados;
    }

    public List<Producto> findByProveedor(String proveedor) {
        List<Producto> resultados = productoRepository.findByProveedor(proveedor);
        cargarImagenes(resultados);
        return resultados;
    }
    
    /**
     * Carga las imágenes para una lista de productos usando JOIN FETCH
     */
    private void cargarImagenes(List<Producto> productos) {
        if (productos == null || productos.isEmpty()) {
            return;
        }
        List<Long> ids = productos.stream().map(Producto::getId).toList();
        String jpql = "SELECT DISTINCT p FROM Producto p LEFT JOIN FETCH p.imagenes WHERE p.id IN :ids";
        List<Producto> conImagenes = entityManager.createQuery(jpql, Producto.class)
                .setParameter("ids", ids)
                .getResultList();
        // Actualizar la lista original con los datos cargados
        for (Producto p : conImagenes) {
            Producto original = productos.stream()
                    .filter(orig -> orig.getId().equals(p.getId()))
                    .findFirst()
                    .orElse(null);
            if (original != null) {
                original.setImagenes(p.getImagenes());
            }
        }
    }

    public Producto findBySku(String sku) {
        Producto producto = productoRepository.findBySku(sku);
        if (producto != null) {
            // Cargar imágenes para este producto
            String jpql = "SELECT DISTINCT p FROM Producto p LEFT JOIN FETCH p.imagenes WHERE p.id = :id";
            List<Producto> resultados = entityManager.createQuery(jpql, Producto.class)
                    .setParameter("id", producto.getId())
                    .getResultList();
            if (!resultados.isEmpty()) {
                producto.setImagenes(resultados.get(0).getImagenes());
            }
        }
        return producto;
    }

    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    public void delete(Long id) {
        productoRepository.deleteById(id);
    }
}
