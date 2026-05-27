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

INSERT IGNORE INTO rol (nombre, descripcion) VALUES
    ('ADMIN',    'Administrador del sistema'),
    ('EMPLEADO', 'Empleado / vendedor'),
    ('CLIENTE',  'Cliente registrado');
