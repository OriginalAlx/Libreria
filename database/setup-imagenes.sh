#!/bin/bash

# ============================================================
# Script de Setup - Imágenes de Productos
# Este script configura la BD para soportar múltiples imágenes
# 
# Uso: bash database/setup-imagenes.sh [usuario] [contraseña] [host] [BD]
# Ejemplo: bash database/setup-imagenes.sh root root localhost libreria_edex
# ============================================================

MYSQL_USER=${1:-root}
MYSQL_PASSWORD=${2:-root}
MYSQL_HOST=${3:-localhost}
MYSQL_DB=${4:-libreria_edex}

echo "=========================================="
echo "Setup de Imágenes de Productos"
echo "=========================================="
echo "Usuario: $MYSQL_USER"
echo "Host: $MYSQL_HOST"
echo "BD: $MYSQL_DB"
echo ""

# Archivo temporal para los comandos SQL
TEMP_SQL=$(mktemp)

cat > "$TEMP_SQL" << 'EOF'
-- Verificar que la BD existe
USE libreria_edex;

-- Crear tabla producto_imagen si no existe
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

-- Verificar si ya se ha ejecutado la migración
-- (si hay imágenes en producto_imagen)
SELECT 'Tabla producto_imagen verificada' as status;
EOF

echo "1️⃣  Creando tabla producto_imagen..."
mysql -h "$MYSQL_HOST" -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" < "$TEMP_SQL"

if [ $? -ne 0 ]; then
    echo "❌ Error al crear tabla. Verifica credenciales y que la BD exista."
    rm -f "$TEMP_SQL"
    exit 1
fi

echo "✅ Tabla producto_imagen creada/verificada"

# Ejecutar migración
TEMP_MIGRATION=$(mktemp)
cat > "$TEMP_MIGRATION" << 'EOF'
USE libreria_edex;

DELIMITER //

-- Procedimiento para migrar imágenes
DROP PROCEDURE IF EXISTS migrate_imagenes //

CREATE PROCEDURE migrate_imagenes()
BEGIN
    DECLARE v_producto_id BIGINT;
    DECLARE v_url_imagen VARCHAR(500);
    DECLARE v_url VARCHAR(500);
    DECLARE v_orden INT;
    DECLARE v_pos INT;
    DECLARE v_next_pos INT;
    DECLARE v_done INT DEFAULT FALSE;
    
    DECLARE producto_cursor CURSOR FOR 
        SELECT id, url_imagen FROM producto 
        WHERE url_imagen IS NOT NULL AND url_imagen != ''
        AND id NOT IN (SELECT DISTINCT producto_id FROM producto_imagen);
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_done = TRUE;
    
    OPEN producto_cursor;
    
    read_loop: LOOP
        FETCH producto_cursor INTO v_producto_id, v_url_imagen;
        IF v_done THEN
            LEAVE read_loop;
        END IF;
        
        SET v_pos = 1;
        SET v_orden = 1;
        
        WHILE v_pos <= LENGTH(v_url_imagen) DO
            SET v_next_pos = INSTR(SUBSTRING(v_url_imagen, v_pos), ';');
            
            IF v_next_pos = 0 THEN
                SET v_url = TRIM(SUBSTRING(v_url_imagen, v_pos));
                SET v_pos = LENGTH(v_url_imagen) + 1;
            ELSE
                SET v_url = TRIM(SUBSTRING(v_url_imagen, v_pos, v_next_pos - 1));
                SET v_pos = v_pos + v_next_pos;
            END IF;
            
            IF v_url != '' THEN
                INSERT IGNORE INTO producto_imagen (producto_id, url, orden, principal, fecha_creacion)
                VALUES (v_producto_id, v_url, v_orden, v_orden = 1, NOW());
                SET v_orden = v_orden + 1;
            END IF;
        END WHILE;
    END LOOP;
    
    CLOSE producto_cursor;
END //

DELIMITER ;

-- Ejecutar migración
CALL migrate_imagenes();

-- Mostrar resultados
SELECT COUNT(*) as total_imagenes FROM producto_imagen;
SELECT p.id, p.nombre, COUNT(pi.id) as total_imagenes 
FROM producto p 
LEFT JOIN producto_imagen pi ON p.id = pi.producto_id 
WHERE p.url_imagen IS NOT NULL AND p.url_imagen != ''
GROUP BY p.id, p.nombre;
EOF

echo ""
echo "2️⃣  Ejecutando migración de imágenes existentes..."
mysql -h "$MYSQL_HOST" -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" < "$TEMP_MIGRATION"

if [ $? -ne 0 ]; then
    echo "❌ Error en la migración. Verifica que la tabla producto_imagen exista."
    rm -f "$TEMP_SQL" "$TEMP_MIGRATION"
    exit 1
fi

echo "✅ Migración completada"
echo ""
echo "=========================================="
echo "✅ Setup completado exitosamente"
echo "=========================================="
echo ""
echo "Próximos pasos:"
echo "1. Reiniciar la aplicación Spring Boot"
echo "2. Las imágenes ahora se gestionan en tabla producto_imagen"
echo "3. Usa ProductoImagenService para agregar/modificar imágenes"
echo ""

# Limpiar
rm -f "$TEMP_SQL" "$TEMP_MIGRATION"
