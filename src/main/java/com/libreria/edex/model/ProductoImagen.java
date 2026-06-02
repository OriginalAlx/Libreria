package com.libreria.edex.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Entidad para almacenar múltiples imágenes de un producto
 * Permite un producto con varias fotos, una de las cuales puede ser principal
 */
@Entity
@Table(name = "producto_imagen")
public class ProductoImagen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(nullable = false)
    private Integer orden = 1;

    @Column(nullable = false)
    private boolean principal = false;

    @Column(length = 200)
    private String descripcion;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    public ProductoImagen() {
    }

    public ProductoImagen(Producto producto, String url, Integer orden, boolean principal) {
        this.producto = producto;
        this.url = url;
        this.orden = orden;
        this.principal = principal;
    }

    public ProductoImagen(Producto producto, String url, Integer orden) {
        this.producto = producto;
        this.url = url;
        this.orden = orden;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public String toString() {
        return "ProductoImagen{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", orden=" + orden +
                ", principal=" + principal +
                '}';
    }
}
