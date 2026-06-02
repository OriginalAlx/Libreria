-- ============================================================
-- Librería EDEX - Script de base de datos MySQL
-- Ejecutar: mysql -u root -p < database/libreria_edex.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS libreria_edex
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE libreria_edex;

CREATE TABLE IF NOT EXISTS rol (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(20)  NOT NULL UNIQUE,
    descripcion VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS usuario (
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    rol_id                 BIGINT       NOT NULL,
    email                  VARCHAR(100) NOT NULL UNIQUE,
    username               VARCHAR(50)  NOT NULL UNIQUE,
    password               VARCHAR(255) NOT NULL,
    activo                 TINYINT(1)   NOT NULL DEFAULT 1,
    email_verificado       TINYINT(1)   NOT NULL DEFAULT 0,
    token_verificacion     VARCHAR(64),
    token_expiracion       DATETIME,
    token_reset_password   VARCHAR(64),
    token_reset_expiracion DATETIME,
    fecha_registro         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuario_rol FOREIGN KEY (rol_id) REFERENCES rol(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS cliente (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT UNIQUE,
    nombre     VARCHAR(80)  NOT NULL,
    apellido   VARCHAR(80)  NOT NULL,
    dni        VARCHAR(15)  UNIQUE,
    telefono   VARCHAR(30),
    email      VARCHAR(100),
    direccion  VARCHAR(200),
    CONSTRAINT fk_cliente_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS producto(
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku                 VARCHAR(50)  NOT NULL UNIQUE,
    nombre              VARCHAR(150) NOT NULL,
    descripcion         VARCHAR(500),
    url_imagen          VARCHAR(500),
    categoria           VARCHAR(100) NOT NULL,
    precio              DECIMAL(10, 2) NOT NULL,
    cost_compra         DECIMAL(10, 2) NOT NULL,
    stock_actual        INT NOT NULL DEFAULT 0,
    stock_minimo        INT NOT NULL DEFAULT 5,
    stock_maximo        INT NOT NULL DEFAULT 100,
    proveedor           VARCHAR(100),
    disponible          TINYINT(1) NOT NULL DEFAULT 1,
    fecha_creacion      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_categoria (categoria),
    INDEX idx_disponible (disponible),
    INDEX idx_sku (sku)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabla para múltiples imágenes por producto
CREATE TABLE IF NOT EXISTS producto_imagen (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id     BIGINT NOT NULL,
    url             VARCHAR(500) NOT NULL,
    orden           INT NOT NULL DEFAULT 1,
    principal       TINYINT(1) NOT NULL DEFAULT 0,
    descripcion     VARCHAR(200),
    fecha_creacion  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_producto_imagen FOREIGN KEY (producto_id) REFERENCES producto(id) ON DELETE CASCADE,
    INDEX idx_producto (producto_id),
    INDEX idx_orden (orden),
    UNIQUE KEY uq_producto_url (producto_id, url)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO rol (nombre, descripcion) VALUES
    ('ADMIN',    'Administrador del sistema'),
    ('EMPLEADO', 'Empleado / vendedor'),
    ('CLIENTE',  'Cliente registrado');
