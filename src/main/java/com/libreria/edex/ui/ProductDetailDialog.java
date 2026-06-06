package com.libreria.edex.ui;

import com.libreria.edex.model.Producto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.List;

public class ProductDetailDialog extends Dialog {

    private final Producto producto;
    private final List<String> imagenes;
    private int indiceImagenActual = 0;
    private Image imagenPrincipal;
    private Span indicadorPagina;

    public ProductDetailDialog(Producto producto) {
        this.producto = producto;
        // Obtener imágenes y filtrar las vacías
        List<String> imagenesBD = producto.getUrlsImagenes();
        this.imagenes = imagenesBD.stream()
                .filter(url -> url != null && !url.trim().isEmpty())
                .collect(java.util.stream.Collectors.toList());

        setWidth("600px");
        setHeight("auto");
        setModal(true);
        setDraggable(true);
        setResizable(true);

        add(crearContenido());
    }

    private VerticalLayout crearContenido() {
        VerticalLayout contenedor = new VerticalLayout();
        contenedor.setSpacing(true);
        contenedor.setPadding(true);

        // Sección de imagen
        contenedor.add(crearSeccionImagen());

        // Información del producto
        contenedor.add(crearSeccionInfo());

        // Botones de acción
        contenedor.add(crearBotonesAccion());

        return contenedor;
    }

    private VerticalLayout crearSeccionImagen() {
        VerticalLayout seccion = new VerticalLayout();
        seccion.setPadding(false);
        seccion.setSpacing(true);
        seccion.getStyle()
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("border-radius", "8px")
                .set("padding", "20px")
                .set("min-height", "300px");

        // Contenedor de imagen
        HorizontalLayout contenedorImagen = new HorizontalLayout();
        contenedorImagen.setWidthFull();
        contenedorImagen.setHeightFull();
        contenedorImagen.setAlignItems(FlexComponent.Alignment.CENTER);
        contenedorImagen.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        contenedorImagen.getStyle()
                .set("background", "white")
                .set("border-radius", "8px")
                .set("min-height", "300px");

        if (imagenes != null && !imagenes.isEmpty()) {
                imagenPrincipal = new Image();
                imagenPrincipal.setWidth("100%");
                imagenPrincipal.setHeight("100%");
                imagenPrincipal.getStyle()
                        .set("object-fit", "contain")
                        .set("max-width", "500px")
                        .set("max-height", "300px")
                        .set("display", "block");

                actualizarImagen();
                contenedorImagen.add(imagenPrincipal);
                seccion.add(contenedorImagen);

                // Controles de navegación solo si hay más de una imagen
                if (imagenes.size() > 1) {
                        seccion.add(crearControlesNavegacion());
                }
        } else {
                // Mostrar placeholder cuando no hay imágenes
                Div placeholderDiv = new Div();
                placeholderDiv.getStyle()
                        .set("width", "100%")
                        .set("height", "300px")
                        .set("display", "flex")
                        .set("flex-direction", "column")
                        .set("align-items", "center")
                        .set("justify-content", "center");

                Icon icono = VaadinIcon.PACKAGE.create();
                icono.getStyle()
                        .set("color", "#ddd")
                        .set("width", "80px")
                        .set("height", "80px")
                        .set("margin-bottom", "15px");

                Span textoPlaceholder = new Span("No hay imágenes disponibles");
                textoPlaceholder.getStyle()
                        .set("color", "#999")
                        .set("font-size", "14px");

                placeholderDiv.add(icono, textoPlaceholder);
                contenedorImagen.add(placeholderDiv);
                seccion.add(contenedorImagen);
        }

        return seccion;
    }

    private HorizontalLayout crearControlesNavegacion() {
        HorizontalLayout controles = new HorizontalLayout();
        controles.setWidthFull();
        controles.setAlignItems(FlexComponent.Alignment.CENTER);
        controles.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        Button btnAnterior = new Button(VaadinIcon.CHEVRON_LEFT.create(), e -> imagenAnterior());
        btnAnterior.addThemeVariants(ButtonVariant.LUMO_ICON);
        btnAnterior.getStyle().set("margin-right", "auto");

        indicadorPagina = new Span((indiceImagenActual + 1) + " de " + imagenes.size());
        indicadorPagina.getStyle()
                .set("font-weight", "600")
                .set("color", "#667eea");

        Button btnSiguiente = new Button(VaadinIcon.CHEVRON_RIGHT.create(), e -> imagenSiguiente());
        btnSiguiente.addThemeVariants(ButtonVariant.LUMO_ICON);
        btnSiguiente.getStyle().set("margin-left", "auto");

        controles.add(btnAnterior, indicadorPagina, btnSiguiente);
        return controles;
    }

    private VerticalLayout crearSeccionInfo() {
        VerticalLayout seccion = new VerticalLayout();
        seccion.setSpacing(false);
        seccion.setPadding(false);

        H2 nombre = new H2(producto.getNombre());
        nombre.getStyle()
                .set("margin", "0 0 10px 0")
                .set("color", "#222")
                .set("font-size", "24px");

        Span categoria = new Span(producto.getCategoria());
        categoria.getStyle()
                .set("display", "inline-block")
                .set("background", "#f0f2ff")
                .set("color", "#667eea")
                .set("padding", "4px 10px")
                .set("border-radius", "16px")
                .set("font-size", "12px")
                .set("font-weight", "600")
                .set("margin-bottom", "15px");

        Paragraph descripcion = new Paragraph();
        String desc = producto.getDescripcion();
        descripcion.setText((desc != null && !desc.isEmpty()) ? desc : "SKU: " + producto.getSku());
        descripcion.getStyle()
                .set("color", "#666")
                .set("line-height", "1.6")
                .set("margin", "0 0 15px 0");

        HorizontalLayout precioLayout = new HorizontalLayout();
        precioLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        precioLayout.setSpacing(true);

        Long precioGuarani = producto.getPrecio() != null ? Math.round(producto.getPrecio()) : null;
        H4 precio = new H4(precioGuarani != null ? "Gs. " + String.format("%,d", precioGuarani) : "");
        precio.getStyle()
                .set("margin", "0")
                .set("color", "#667eea")
                .set("font-size", "20px");

        precioLayout.add(precio);

        Paragraph stockInfo = new Paragraph();
        int stock = producto.getStockActual();
        if (stock > 10) {
            stockInfo.setText("✓ " + stock + " en stock");
            stockInfo.getStyle().set("color", "#28a745");
        } else if (stock > 0) {
            stockInfo.setText("⚠ Solo " + stock + " disponible");
            stockInfo.getStyle().set("color", "#ff9800");
        } else {
            stockInfo.setText("✗ Agotado");
            stockInfo.getStyle().set("color", "#dc3545");
        }
        stockInfo.getStyle().set("margin", "0 0 15px 0").set("font-weight", "600");

        seccion.add(categoria, nombre, descripcion, precioLayout, stockInfo);
        return seccion;
    }

    private HorizontalLayout crearBotonesAccion() {
        HorizontalLayout botones = new HorizontalLayout();
        botones.setWidthFull();
        botones.setSpacing(true);
        botones.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        Button btnCerrar = new Button("Cerrar", e -> close());
        btnCerrar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button btnAgregarCarrito = new Button("Agregar al carrito");
        btnAgregarCarrito.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnAgregarCarrito.setEnabled(producto.isDisponible() && producto.getStockActual() > 0);

        botones.add(btnCerrar, btnAgregarCarrito);
        return botones;
    }

    private void imagenAnterior() {
        if (imagenes != null && imagenes.size() > 0) {
            indiceImagenActual = (indiceImagenActual - 1 + imagenes.size()) % imagenes.size();
            actualizarImagen();
        }
    }

    private void imagenSiguiente() {
        if (imagenes != null && imagenes.size() > 0) {
            indiceImagenActual = (indiceImagenActual + 1) % imagenes.size();
            actualizarImagen();
        }
    }

    private void actualizarImagen() {
        if (imagenPrincipal != null && imagenes != null && !imagenes.isEmpty()) {
            String url = imagenes.get(indiceImagenActual);
            // Validar que la URL sea válida
            if (url != null && !url.trim().isEmpty()) {
                url = url.trim();
                // Convertir ruta relativa a absoluta usando URL completa del servidor
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    // Remover slash inicial si existe
                    if (url.startsWith("/")) {
                        url = url.substring(1);
                    }
                    // Usar URL absoluta del servidor local
                    url = "http://localhost:8081/" + url;
                }
                imagenPrincipal.setSrc(url);
                imagenPrincipal.setAlt(producto.getNombre() + " - Imagen " + (indiceImagenActual + 1));

                if (indicadorPagina != null) {
                    indicadorPagina.setText((indiceImagenActual + 1) + " de " + imagenes.size());
                }
            }
        }
    }
}
