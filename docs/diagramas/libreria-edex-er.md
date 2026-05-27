# Diagrama Entidad-Relación — Librería EDEX

Sistema de gestión para venta de útiles escolares (Spring Boot + Vaadin + MySQL).

![Diagrama ER Librería EDEX](libreria-edex-er.png)

## Diagrama ER

```mermaid
erDiagram
    ROL ||--o{ USUARIO : tiene
    USUARIO |o--o| CLIENTE : "puede ser"
    CATEGORIA ||--o{ PRODUCTO : clasifica
    PROVEEDOR ||--o{ COMPRA : suministra
    USUARIO ||--o{ COMPRA : registra
    COMPRA ||--|{ DETALLE_COMPRA : contiene
    PRODUCTO ||--o{ DETALLE_COMPRA : incluye
    CLIENTE ||--o{ VENTA : realiza
    USUARIO ||--o{ VENTA : registra
    CAJA ||--o{ VENTA : procesa
    VENTA ||--|{ DETALLE_VENTA : contiene
    PRODUCTO ||--o{ DETALLE_VENTA : incluye
    VENTA ||--|| TICKET : genera
    USUARIO ||--o{ CAJA : "abre/cierra"
    CAJA ||--o{ MOVIMIENTO_CAJA : registra
    PRODUCTO ||--o{ MOVIMIENTO_STOCK : afecta
    PRODUCTO ||--o{ ALERTA_STOCK : dispara

    ROL {
        bigint id PK
        varchar nombre UK
        varchar descripcion
    }

    USUARIO {
        bigint id PK
        bigint rol_id FK
        varchar username UK
        varchar password
        varchar email UK
        boolean activo
        datetime fecha_registro
    }

    CLIENTE {
        bigint id PK
        bigint usuario_id FK
        varchar nombre
        varchar apellido
        varchar dni UK
        varchar telefono
        varchar email
        varchar direccion
    }

    CATEGORIA {
        bigint id PK
        varchar nombre UK
        varchar descripcion
    }

    PRODUCTO {
        bigint id PK
        bigint categoria_id FK
        varchar codigo UK
        varchar nombre
        text descripcion
        decimal precio_compra
        decimal precio_venta
        int stock_actual
        int stock_minimo
        int stock_maximo
        boolean activo
    }

    PROVEEDOR {
        bigint id PK
        varchar razon_social
        varchar cuit
        varchar telefono
        varchar email
        varchar direccion
        varchar contacto_nombre
        boolean activo
    }

    COMPRA {
        bigint id PK
        bigint proveedor_id FK
        bigint usuario_id FK
        date fecha
        varchar numero_factura
        decimal total
        varchar estado
        date fecha_entrega
    }

    DETALLE_COMPRA {
        bigint id PK
        bigint compra_id FK
        bigint producto_id FK
        int cantidad
        decimal precio_unitario
        decimal subtotal
    }

    CAJA {
        bigint id PK
        bigint usuario_apertura_id FK
        bigint usuario_cierre_id FK
        datetime fecha_apertura
        datetime fecha_cierre
        decimal monto_inicial
        decimal monto_final
        varchar estado
    }

    VENTA {
        bigint id PK
        bigint cliente_id FK
        bigint usuario_id FK
        bigint caja_id FK
        datetime fecha
        decimal subtotal
        decimal descuento
        decimal total
        varchar metodo_pago
        varchar estado
    }

    DETALLE_VENTA {
        bigint id PK
        bigint venta_id FK
        bigint producto_id FK
        int cantidad
        decimal precio_unitario
        decimal subtotal
    }

    TICKET {
        bigint id PK
        bigint venta_id FK
        varchar numero_ticket UK
        datetime fecha_emision
        text contenido
    }

    MOVIMIENTO_CAJA {
        bigint id PK
        bigint caja_id FK
        varchar tipo
        decimal monto
        varchar concepto
        datetime fecha
        bigint referencia_id
    }

    MOVIMIENTO_STOCK {
        bigint id PK
        bigint producto_id FK
        varchar tipo
        int cantidad
        int stock_anterior
        int stock_posterior
        datetime fecha
        bigint referencia_id
        varchar observacion
    }

    ALERTA_STOCK {
        bigint id PK
        bigint producto_id FK
        int stock_actual
        int stock_minimo
        datetime fecha_alerta
        boolean leida
        boolean resuelta
    }
```

## Diagrama de módulos

```mermaid
flowchart TB
    subgraph seguridad [Seguridad]
        ROL
        USUARIO
        CLIENTE
    end

    subgraph catalogo [Catálogo]
        CATEGORIA
        PRODUCTO
        ALERTA_STOCK
    end

    subgraph compras [Compras]
        PROVEEDOR
        COMPRA
        DETALLE_COMPRA
    end

    subgraph ventas [Ventas]
        VENTA
        DETALLE_VENTA
        TICKET
    end

    subgraph caja [Caja]
        CAJA
        MOVIMIENTO_CAJA
    end

    subgraph inventario [Inventario]
        MOVIMIENTO_STOCK
    end

    compras --> inventario
    ventas --> inventario
    inventario --> catalogo
    ventas --> caja
    seguridad --> ventas
    seguridad --> compras
    seguridad --> caja
```

## Cómo visualizar

| Formato | Archivo | Herramienta |
|---------|---------|-------------|
| PlantUML | `libreria-edex-er.puml` | [PlantUML Online](https://www.plantuml.com/plantuml/uml/), VS Code + extensión PlantUML |
| Mermaid | Este archivo `.md` | Preview de Markdown en Cursor/VS Code, GitHub |
| Imagen PNG | `libreria-edex-er.png` | Generada con PlantUML |
