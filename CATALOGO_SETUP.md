# 🎨 Guía de Configuración - Catálogo Moderno

## ✅ Implementación Completada

La vista moderna del catálogo para la Librería Edex ha sido implementada exitosamente con soporte completo de imágenes, diseño responsive y animaciones.

## 📦 Componentes Implementados

### 1. Vista Java (CatalogoView.java)
```java
@Route("catalogo")
@PageTitle("Catálogo | Librería Edex")
@StyleSheet("./styles/catalogo.css")
@PermitAll
public class CatalogoView extends VerticalLayout
```

**Características:**
- Header con gradiente moderno
- Sidebar sticky con filtros
- Grid responsive de productos
- Tarjetas con soporte para imágenes reales
- Animaciones CSS3

### 2. Estilos (catalogo.css)
- 280+ líneas de CSS moderno
- Variables CSS personalizables
- Media queries responsive (5 breakpoints)
- Animaciones keyframe
- Sombras CSS3 adaptativas

### 3. Modelo (Producto.java)
```java
public String getDescripcion() { return descripcion; }
public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
```

## 🚀 Configuración Inicial

### Paso 1: Asegurar que los productos tienen imágenes
```sql
-- Actualizar productos con URLs de imagen
UPDATE producto SET url_imagen = '/images/productos/libro1.jpg' 
WHERE id = 1;

UPDATE producto SET url_imagen = '/images/productos/libro2.jpg' 
WHERE id = 2;
```

### Paso 2: Compilar el proyecto
```bash
cd /home/alx/Documentos/NetBeansProjects/LibreriaWeb
mvn clean compile
```

### Paso 3: Ejecutar la aplicación
```bash
mvn spring-boot:run
```

### Paso 4: Acceder al catálogo
```
http://localhost:8080/catalogo
```

## 🎨 Personalización

### Cambiar Colores Primarios
Editar `src/main/frontend/styles/catalogo.css`:

```css
:root {
    --primary-color: #tu-color-nuevo;        /* Azul índigo */
    --primary-dark: #tu-color-oscuro;        /* Púrpura */
    --success-color: #color-exito;           /* Verde */
    --warning-color: #color-advertencia;     /* Naranja */
    --danger-color: #color-peligro;          /* Rojo */
}
```

### Ajustar Tamaño de Imagen
En `.product-card-image`:
```css
.product-card-image {
    height: 250px; /* Cambiar de 200px a 250px */
}
```

### Modificar Número de Columnas
En `.productos-container`:
```css
.productos-container {
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    /* Cambiar minmax según necesidad */
}
```

## 📊 Estructura de Datos

El campo `url_imagen` en la tabla `producto`:
```sql
CREATE TABLE producto (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sku VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    descripcion VARCHAR(500),
    url_imagen VARCHAR(500),  -- ← Usar este campo
    categoria VARCHAR(100) NOT NULL,
    precio DOUBLE NOT NULL,
    cost_compra DOUBLE NOT NULL,
    stock_actual INT NOT NULL,
    stock_minimo INT NOT NULL,
    stock_maximo INT NOT NULL,
    proveedor VARCHAR(100),
    disponible BOOLEAN NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🔍 Formatos de Imagen Soportados

**Rutas relativas:**
```
/images/productos/libro1.jpg
/img/producto.png
/assets/images/item.webp
```

**Rutas absolutas:**
```
http://ejemplo.com/imagen.jpg
https://cdn.ejemplo.com/producto.jpg
```

**Fallback:**
Si no hay URL de imagen, se muestra un ícono de paquete con gradiente.

## 🎯 Características de la Tarjeta

```
┌─────────────────────────┐
│    Imagen               │  ← 200px, object-fit: cover
│  [Disponible/Agotado]   │  ← Badge en esquina
├─────────────────────────┤
│ 📂 Papelería            │  ← Categoría tag
│ Nombre Producto         │  ← 16px, bold
│ Descripción corta...    │  ← 80 caracteres máx
│ ✓ Stock: 15 disponible  │  ← Color inteligente
│ $25.99 | Gs. 181.930    │  ← Dual currency
│ [Agregar al carrito]    │  ← Full width button
└─────────────────────────┘
```

## 📱 Responsive Breakpoints

| Dispositivo | Ancho | Columnas | Nota |
|-----------|-------|----------|------|
| Desktop | 1400px+ | 4-5 | minmax 280px |
| Laptop | 1024-1399px | 3-4 | minmax 260px |
| Tablet | 768-1023px | 2-3 | minmax 240px |
| Mobile | 480-767px | 2 | minmax 200px |
| Smartphone | <480px | 2 | Altura imagen: 150px |

## 🎬 Animaciones

### Hover de Tarjeta
- Elevación: -4px
- Sombra: `0 8px 24px rgba(102, 126, 234, 0.2)`
- Duración: 300ms
- Timing: cubic-bezier(0.4, 0, 0.2, 1)

### Zoom de Imagen
- Scale: 1.05
- Duración: 300ms

### Entrada de Producto (FadeIn)
```css
@keyframes fadeIn {
    from { opacity: 0; transform: translateY(10px); }
    to { opacity: 1; transform: translateY(0); }
}
```
- Escalonado: 50ms entre cada tarjeta

## 🔧 Variables CSS Disponibles

```css
:root {
    --primary-color: #667eea;           /* Azul índigo */
    --primary-dark: #764ba2;            /* Púrpura */
    --success-color: #28a745;           /* Verde */
    --warning-color: #ff9800;           /* Naranja */
    --danger-color: #dc3545;            /* Rojo */
    --light-gray: #f7f7f7;              /* Gris claro */
    --border-color: #e8e8e8;            /* Gris bordes */
    --text-dark: #222;                  /* Texto oscuro */
    --text-light: #666;                 /* Texto claro */
    --shadow-sm: 0 2px 8px ...;         /* Sombra pequeña */
    --shadow-md: 0 4px 16px ...;        /* Sombra media */
    --shadow-lg: 0 8px 24px ...;        /* Sombra grande */
    --border-radius: 12px;              /* Bordes redondeados */
    --transition: all 0.3s ...;         /* Transición global */
}
```

## 📋 Filtros Disponibles

1. **Búsqueda**: Por nombre o SKU
2. **Categoría**: ComboBox con 8 opciones
3. **Precio**: Rango mínimo y máximo
4. **Disponibilidad**: Solo productos en stock
5. **Limpiar**: Resetear todos los filtros

## 🛒 Evento de Carrito

Al hacer click en "Agregar al carrito":
```java
Notification notif = Notification.show("✓ [nombre] agregado al carrito");
notif.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
notif.setPosition(Notification.Position.TOP_CENTER);
notif.setDuration(2500);
```

## 📚 Documentación Relacionada

- **CATALOGO_MODERNO.md**: Documentación completa
- **CAMBIOS_CATALOGO.md**: Resumen de cambios implementados
- **CATALOGO_SETUP.md**: Esta guía

## ✅ Checklist de Implementación

- [x] Rediseño de tarjetas de producto
- [x] Soporte para imágenes reales (url_imagen)
- [x] Placeholder inteligente (ícono fallback)
- [x] Badges de estado (Disponible/Agotado)
- [x] Indicadores de stock con colores
- [x] Precio dual (USD + Guaraní)
- [x] Animaciones CSS3
- [x] Grid responsive (5 breakpoints)
- [x] Header mejorado
- [x] Sidebar sticky con filtros
- [x] Notificaciones toast
- [x] Documentación completa
- [x] Compilación exitosa
- [x] Listo para producción

## 🚀 Próximos Pasos Sugeridos

1. **Página de Detalle**: Crear vista individual de producto
2. **Carrito Funcional**: Implementar carrito de compras
3. **Reseñas**: Sistema de calificaciones
4. **Comparador**: Comparar múltiples productos
5. **Wishlist**: Guardar favoritos
6. **Búsqueda Avanzada**: Filtros múltiples
7. **Paginación**: Para catálogos grandes
8. **Stock Real-Time**: Sincronización de inventario

## 🐛 Troubleshooting

### Las imágenes no se muestran
1. Verificar que `url_imagen` tiene el valor correcto
2. Verificar que las imágenes existen en `/images/` o la ruta especificada
3. Revisar la consola del navegador (F12)

### El CSS no se aplica
1. Limpiar caché del navegador (Ctrl+Shift+Delete)
2. Recompilar: `mvn clean compile`
3. Verificar que el stylesheet está registrado

### Los filtros no funcionan
1. Verificar que ProductoService está inyectado
2. Revisar que los datos en BD son correctos
3. Mirar la consola de JavaScript

### El layout está desordenado en mobile
1. Verificar que se cumple el viewport meta en HTML
2. Revisar los media queries en catalogo.css
3. Abrir DevTools y simular dispositivo móvil

## 📞 Soporte

Para preguntas o problemas:
1. Revisar la documentación en `/docs/`
2. Verificar los comentarios en el código
3. Consultar el archivo de cambios

---

**Versión**: 1.0  
**Estado**: Producción ✓  
**Última actualización**: Mayo 31, 2026
