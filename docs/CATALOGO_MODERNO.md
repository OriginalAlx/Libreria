# Catálogo Moderno - Librería Edex

## Descripción

Se ha implementado una vista de catálogo moderna y responsive para la Librería Edex con las siguientes características principales:

### 🎨 Características Visuales

#### 1. **Diseño de Tarjetas de Producto Mejorado**
- Tarjetas con imagen de producto en alta calidad
- Efecto hover con animación suave (elevación + sombra)
- Imagen con zoom al pasar el mouse (1.05x)
- Bordes redondeados (12px) para un aspecto moderno
- Sombras CSS3 adaptativas

#### 2. **Sección de Imagen Mejorada**
- **Altura optimizada**: 200px
- **Manejo de imágenes**: Soporte para `url_imagen` desde la base de datos
- **Fallback inteligente**: Ícono de paquete como placeholder si no hay imagen
- **Badges de estado**:
  - Verde para "Disponible"
  - Rojo para "Agotado"
  - Posicionados en la esquina superior derecha con efecto de vidrio (backdrop-filter)

#### 3. **Información del Producto Organizada**
- **Categoría**: Mostrada como tag con fondo lavanda
- **Nombre**: Fuente grande y legible (16px, 600 weight)
- **Descripción**: Máximo 80 caracteres con "..."
- **Stock**: Indicador visual inteligente:
  - Verde: "Stock > 10"
  - Naranja: "Stock 1-10"
  - Rojo: "Agotado"
- **Precio**: Dual display (USD + Guaraní)

#### 4. **Paleta de Colores Moderna**
- Primario: #667eea (Azul índigo)
- Secundario: #764ba2 (Púrpura)
- Éxito: #28a745 (Verde)
- Advertencia: #ff9800 (Naranja)
- Peligro: #dc3545 (Rojo)

### 📐 Diseño Responsive

**Grid adaptativo** según tamaño de pantalla:

```
Desktop (1400px+):     4-5 columnas (280px min)
Laptop (1024-1399px):  3-4 columnas (260px min)
Tablet (768-1023px):   2-3 columnas (240px min)
Mobile (480-767px):    2 columnas
Smartphone (<480px):   2 columnas (200px min)
```

**Ajustes en dispositivos móviles:**
- Reducción de altura de imagen (150px)
- Padding reducido en tarjetas (12px)
- Font sizes ajustados

### 🔍 Filtros Mejorados

**Sidebar lateral con:**
- 🔎 Búsqueda por nombre/SKU con ícono de lupa
- 📂 Filtro por categoría (ComboBox)
- 💰 Rango de precio (Mín/Máx con label mejorado)
- ✓ Checkbox para "Solo disponibles"
- 🔄 Botón para limpiar todos los filtros

### ⚡ Animaciones y Transiciones

- Transición de sombra: 300ms (cubic-bezier para movimiento natural)
- Elevación de tarjeta: -4px translateY
- Zoom de imagen: scale(1.05)
- Animación de entrada (fadeIn) para productos (escalonada)

### 🛒 Botón "Agregar al Carrito"

- Ancho completo de la tarjeta
- Tema primario (azul índigo)
- Deshabilitado si producto está agotado o sin stock
- Notificación toast al agregar (éxito, 2.5s duración)

### 📱 Header Mejorado

- Logo de libería (🏪)
- Gradiente de fondo (167eea → 764ba2)
- Botones: Carrito, Perfil (usuario), Salir
- Sombra sutil pero visible
- Padding aumentado (20px 30px)

### 🎯 Mejoras en el Modelo `Producto`

Se agregaron métodos getter/setter para `descripcion`:
```java
public String getDescripcion() {
    return descripcion;
}

public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
}
```

## 📁 Archivos Modificados/Creados

### Creados:
- `src/main/frontend/styles/catalogo.css` - Estilos CSS modernos
- `docs/CATALOGO_MODERNO.md` - Esta documentación

### Modificados:
- `src/main/java/com/libreria/edex/ui/CatalogoView.java`:
  - Mejora completa de la vista
  - Tarjetas con imágenes reales
  - Animaciones y transiciones
  - Import de StyleSheet

- `src/main/java/com/libreria/edex/model/Producto.java`:
  - Agregados getters/setters para `descripcion`

## 🚀 Funcionalidades

### Vista del Catálogo

1. **Carga automática** de productos desde la BD
2. **Grid responsive** que se adapta al tamaño de pantalla
3. **Filtros en tiempo real**:
   - Búsqueda por nombre/SKU
   - Filtro por categoría
   - Rango de precio
   - Solo productos disponibles
4. **Información contextual**: Mostrar "X productos" encontrados

### Manejo de Imágenes

Las imágenes se cargan desde el campo `url_imagen` del modelo:
- Si existe: Se muestra con object-fit: cover
- Si no existe: Se muestra ícono de paquete en gradiente
- Ruta relativa o absoluta soportada

## 🎓 Variables CSS Raíz

Se definen variables CSS personalizadas para fácil customización:

```css
:root {
    --primary-color: #667eea;
    --primary-dark: #764ba2;
    --success-color: #28a745;
    --warning-color: #ff9800;
    --danger-color: #dc3545;
    --light-gray: #f7f7f7;
    --border-color: #e8e8e8;
    --text-dark: #222;
    --text-light: #666;
    --shadow-sm: 0 2px 8px rgba(0, 0, 0, 0.08);
    --shadow-md: 0 4px 16px rgba(0, 0, 0, 0.12);
    --shadow-lg: 0 8px 24px rgba(102, 126, 234, 0.2);
    --border-radius: 12px;
    --transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
```

## 💡 Próximas Mejoras Sugeridas

1. **Vista de detalle del producto**: Página individual con descripción completa
2. **Carrito de compras**: Implementación de carrito funcional
3. **Reseñas de productos**: Sistema de ratings y comentarios
4. **Búsqueda avanzada**: Búsqueda por etiquetas, autor, etc.
5. **Comparador de productos**: Comparar múltiples productos
6. **Wishlist**: Guardar productos favoritos
7. **Recomendaciones**: Productos relacionados
8. **Paginación**: Para catálogos con muchos productos

## 🔧 Cómo Personalizar

### Cambiar Colores
Editar `src/main/frontend/styles/catalogo.css`:
```css
:root {
    --primary-color: #tu-color;
    --primary-dark: #tu-color-oscuro;
    /* ... */
}
```

### Ajustar Tamaño de Tarjetas
En la clase `.product-card-image`:
```css
height: 200px; /* Cambiar altura */
```

### Modificar Grid
En `.productos-container`:
```css
grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
/* Ajustar minmax según necesidad */
```

## 📊 Estructura de la Tarjeta de Producto

```
┌─────────────────────────┐
│    Imagen (200px)       │  ← Con badge de estado
│                         │
├─────────────────────────┤
│ 📂 Categoría            │
│ Nombre Producto         │
│ Descripción corta...    │
│ ✓ Stock: 15 disponible  │
│ $25.99 | Gs. 181,930    │
│ [Agregar al carrito]    │
└─────────────────────────┘
```

## ✅ Testing

La vista ha sido compilada exitosamente con Maven:
```bash
mvn clean compile
```

Para probar en desarrollo:
```bash
mvn spring-boot:run
```

Luego navegar a: `http://localhost:8080/catalogo`

---

**Versión**: 1.0  
**Fecha**: Mayo 2026  
**Estado**: Producción ✓
