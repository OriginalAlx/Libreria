-- ============================================================
-- Migración v2: Agregar tabla de productos con campos completos
-- Ejecutar: mysql -u root -p libreria_edex < database/migration_v2_productos.sql
-- ============================================================

-- Verificar si la tabla existe y eliminarla si es necesario (para desarrollo)
-- DROP TABLE IF EXISTS productos;

-- Crear tabla productos con todos los campos
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
