package com.libreria.edex.service;

import com.libreria.edex.model.Producto;
import com.libreria.edex.model.ProductoImagen;
import com.libreria.edex.repository.ProductoImagenRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoImagenService {

    private final ProductoImagenRepository productoImagenRepository;

    public ProductoImagenService(ProductoImagenRepository productoImagenRepository) {
        this.productoImagenRepository = productoImagenRepository;
    }

    /**
     * Obtiene todas las imágenes de un producto ordenadas
     */
    public List<ProductoImagen> findByProductoId(Long productoId) {
        return productoImagenRepository.findByProductoIdOrderByPrincipalDescOrdenAsc(productoId);
    }

    /**
     * Obtiene la imagen principal de un producto
     */
    public ProductoImagen findPrincipalByProductoId(Long productoId) {
        return productoImagenRepository.findByProductoIdAndPrincipalTrue(productoId);
    }

    /**
     * Guarda una imagen de producto
     */
    public ProductoImagen save(ProductoImagen imagen) {
        return productoImagenRepository.save(imagen);
    }

    /**
     * Guarda múltiples imágenes
     */
    public List<ProductoImagen> saveAll(List<ProductoImagen> imagenes) {
        return productoImagenRepository.saveAll(imagenes);
    }

    /**
     * Añade una imagen a un producto
     */
    @Transactional
    public ProductoImagen addImagenToProducto(Producto producto, String url, Integer orden, boolean principal) {
        // Si es principal, remover la anterior imagen principal
        if (principal) {
            ProductoImagen anterior = findPrincipalByProductoId(producto.getId());
            if (anterior != null) {
                anterior.setPrincipal(false);
                save(anterior);
            }
        }

        ProductoImagen imagen = new ProductoImagen(producto, url, orden, principal);
        return save(imagen);
    }

    /**
     * Elimina una imagen
     */
    public void delete(Long imagenId) {
        productoImagenRepository.deleteById(imagenId);
    }

    /**
     * Elimina todas las imágenes de un producto
     */
    public void deleteByProductoId(Long productoId) {
        productoImagenRepository.deleteByProductoId(productoId);
    }

    /**
     * Reordena las imágenes de un producto
     */
    @Transactional
    public void reordenarImagenes(List<ProductoImagen> imagenes) {
        for (int i = 0; i < imagenes.size(); i++) {
            imagenes.get(i).setOrden(i + 1);
        }
        saveAll(imagenes);
    }
}
