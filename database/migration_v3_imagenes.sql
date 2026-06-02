-- ============================================================
-- Script de migración v3: Migrar imágenes a tabla producto_imagen
-- Ejecutar: mysql libreria_edex < database/migration_v3_imagenes.sql
-- ============================================================

-- Nota: Este script asume que la tabla producto_imagen ya existe
-- Si no existe, ejecutar primero: ALTER TABLE producto ... (ver comentario abajo)

-- Migrar imágenes existentes desde url_imagen a producto_imagen
-- Cada URL separada por ';' se convierte en un registro separado

DELIMITER //

-- Procedimiento para insertar imágenes desde url_imagen
CREATE PROCEDURE IF NOT EXISTS migrate_imagenes()
BEGIN
    DECLARE v_producto_id BIGINT;
    DECLARE v_url_imagen VARCHAR(500);
    DECLARE v_url VARCHAR(500);
    DECLARE v_orden INT;
    DECLARE v_pos INT;
    DECLARE v_next_pos INT;
    DECLARE v_done INT DEFAULT FALSE;
    
    DECLARE producto_cursor CURSOR FOR 
        SELECT id, url_imagen FROM producto WHERE url_imagen IS NOT NULL AND url_imagen != '';
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_done = TRUE;
    
    OPEN producto_cursor;
    
    read_loop: LOOP
        FETCH producto_cursor INTO v_producto_id, v_url_imagen;
        IF v_done THEN
            LEAVE read_loop;
        END IF;
        
        -- Procesar las URLs separadas por punto y coma
        SET v_pos = 1;
        SET v_orden = 1;
        
        WHILE v_pos <= LENGTH(v_url_imagen) DO
            -- Encontrar siguiente punto y coma
            SET v_next_pos = INSTR(SUBSTRING(v_url_imagen, v_pos), ';');
            
            IF v_next_pos = 0 THEN
                -- Última URL
                SET v_url = TRIM(SUBSTRING(v_url_imagen, v_pos));
                SET v_pos = LENGTH(v_url_imagen) + 1;
            ELSE
                -- URL intermedia
                SET v_url = TRIM(SUBSTRING(v_url_imagen, v_pos, v_next_pos - 1));
                SET v_pos = v_pos + v_next_pos;
            END IF;
            
            -- Insertar si no está vacía
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

-- Ejecutar la migración
CALL migrate_imagenes();

-- Limpiar el procedimiento
DROP PROCEDURE IF EXISTS migrate_imagenes;

-- Verificar resultados (ejecutar después de la migración)
-- SELECT p.id, p.nombre, COUNT(pi.id) as total_imagenes FROM producto p 
-- LEFT JOIN producto_imagen pi ON p.id = pi.producto_id 
-- GROUP BY p.id, p.nombre;
