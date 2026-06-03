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
    private Div contenedorIndicadores;

    public ProductDetailDialog(Producto producto) {
        this.producto = producto;
        // Obtener imágenes y filtrar las vacías
        List<String> imagenesBD = producto.getUrlsImagenes();
        this.imagenes = imagenesBD.stream()
                .filter(url -> url != null && !url.trim().isEmpty())
                .collect(java.util.stream.Collectors.toList());

        setWidth("700px");
        setHeight("auto");
        setModal(true);
        setDraggable(true);
        setResizable(true);
        addClassName("product-detail-dialog");

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
                .set("min-height", "350px");

        // Contenedor de imagen principal
        HorizontalLayout contenedorImagen = new HorizontalLayout();
        contenedorImagen.setWidthFull();
        contenedorImagen.setHeightFull();
        contenedorImagen.setAlignItems(FlexComponent.Alignment.CENTER);
        contenedorImagen.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        contenedorImagen.getStyle()
                .set("background", "white")
                .set("border-radius", "8px")
                .set("min-height", "350px");

        if (imagenes != null && !imagenes.isEmpty()) {
                imagenPrincipal = new Image();
                imagenPrincipal.setWidth("100%");
                imagenPrincipal.setHeight("100%");
                imagenPrincipal.getStyle()
                        .set("object-fit", "contain")
                        .set("max-width", "600px")
                        .set("max-height", "350px")
                        .set("display", "block");

                actualizarImagen();
                contenedorImagen.add(imagenPrincipal);
                seccion.add(contenedorImagen);

                // Controles de navegación solo si hay más de una imagen
                if (imagenes.size() > 1) {
                        seccion.add(crearControlesNavegacion());
                        seccion.add(crearIndicadores());
                }
        } else {
                // Mostrar placeholder cuando no hay imágenes
                Div placeholderDiv = new Div();
                placeholderDiv.getStyle()
                        .set("width", "100%")
                        .set("height", "350px")
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
        controles.getStyle().set("margin-top", "15px");

        Button btnAnterior = new Button(VaadinIcon.CHEVRON_LEFT.create(), e -> imagenAnterior());
        btnAnterior.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnAnterior.getStyle().set("background", "white").set("color", "#667eea");

        indicadorPagina = new Span((indiceImagenActual + 1) + " de " + imagenes.size());
        indicadorPagina.getStyle()
                .set("font-weight", "600")
                .set("color", "white")
                .set("font-size", "14px");

        Button btnSiguiente = new Button(VaadinIcon.CHEVRON_RIGHT.create(), e -> imagenSiguiente());
        btnSiguiente.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnSiguiente.getStyle().set("background", "white").set("color", "#667eea");

        controles.add(btnAnterior, indicadorPagina, btnSiguiente);
        return controles;
    }

    /**
     * Crea los indicadores (puntos) para navegar entre las imágenes
     */
    private Div crearIndicadores() {
        contenedorIndicadores = new Div();
        contenedorIndicadores.getStyle()
                .set("display", "flex")
                .set("justify-content", "center")
                .set("gap", "8px")
                .set("margin-top", "12px");

        for (int i = 0; i < imagenes.size(); i++) {
            Div indicador = new Div();
            indicador.getStyle()
                    .set("width", "10px")
                    .set("height", "10px")
                    .set("border-radius", "50%")
                    .set("background-color", i == 0 ? "white" : "rgba(255, 255, 255, 0.5)")
                    .set("cursor", "pointer")
                    .set("transition", "all 0.3s ease");

            final int index = i;
            indicador.getElement().addEventListener("click", e -> {
                indiceImagenActual = index;
                actualizarImagen();
            });

            contenedorIndicadores.add(indicador);
        }

        return contenedorIndicadores;
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
                // Agregar "/" si no comienza con "/" o "http"
                if (!url.startsWith("/") && !url.startsWith("http")) {
                    url = "/" + url;
                }
                imagenPrincipal.setSrc(url);
                imagenPrincipal.setAlt(producto.getNombre() + " - Imagen " + (indiceImagenActual + 1));

                if (indicadorPagina != null) {
                    indicadorPagina.setText((indiceImagenActual + 1) + " de " + imagenes.size());
                }
                
                // Actualizar indicadores (puntos)
                actualizarIndicadores();
            }
        }
    }
    
    /**
     * Actualiza el estilo visual de los indicadores según la imagen actual
     */
    private void actualizarIndicadores() {
        if (contenedorIndicadores != null) {
            for (int i = 0; i < contenedorIndicadores.getComponentCount(); i++) {
                Div indicador = (Div) contenedorIndicadores.getComponentAt(i);
                if (i == indiceImagenActual) {
                    indicador.getStyle().set("background-color", "white");
                } else {
                    indicador.getStyle().set("background-color", "rgba(255, 255, 255, 0.5)");
                }
            }
        }
    }
}
