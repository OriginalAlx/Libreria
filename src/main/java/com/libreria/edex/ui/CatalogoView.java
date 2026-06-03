package com.libreria.edex.ui;

import com.libreria.edex.model.Producto;
import com.libreria.edex.service.ProductoService;
import com.libreria.edex.service.SecurityService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
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
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.dependency.StyleSheet;
import jakarta.annotation.security.PermitAll;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Route("catalogo")
@PageTitle("Catálogo | Librería Edex")
@StyleSheet("./styles/catalogo.css")
@PermitAll
public class CatalogoView extends VerticalLayout {

        private final SecurityService securityService;
        private final ProductoService productoService;
        private final Div productosContainer = new Div();
        private List<Producto> productosList;

        // Componentes de filtro
        private TextField busquedaField;
        private ComboBox<String> categoriaFilter;
        private NumberField precioMinField;
        private NumberField precioMaxField;
        private Checkbox disponibleFilter;
        private Paragraph infoProductos;

        public CatalogoView(SecurityService securityService, ProductoService productoService) {
                this.securityService = securityService;
                this.productoService = productoService;

                setSizeFull();
                setPadding(false);
                setSpacing(false);
                getStyle().set("background-color", "#f7f7f7");

                add(crearHeader());

                HorizontalLayout mainContent = new HorizontalLayout();
                mainContent.setWidthFull();
                mainContent.setSpacing(false);
                mainContent.setPadding(false);

                mainContent.add(crearSidebarFiltros());

                VerticalLayout areaProductos = crearAreaProductos();
                mainContent.add(areaProductos);
                mainContent.setFlexGrow(1, areaProductos);

                add(mainContent);

                cargarProductos();
        }

        private HorizontalLayout crearHeader() {
                HorizontalLayout header = new HorizontalLayout();
                header.setWidthFull();
                header.setPadding(true);
                header.setAlignItems(FlexComponent.Alignment.CENTER);
                header.setSpacing(true);
                header.getStyle()
                                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                                .set("color", "white")
                                .set("box-shadow", "0 4px 12px rgba(102, 126, 234, 0.3)")
                                .set("padding", "20px 30px");

                H1 titulo = new H1("LIBRERÍA EDEX");
                titulo.getStyle()
                                .set("margin", "0")
                                .set("flex-grow", "1")
                                .set("font-size", "28px")
                                .set("font-weight", "700")
                                .set("letter-spacing", "0.5px");

                Button btnCarrito = new Button("🛒 Carrito");
                btnCarrito.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
                btnCarrito.getStyle()
                                .set("color", "white")
                                .set("font-weight", "600")
                                .set("font-size", "15px");

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String username = (auth != null && auth.isAuthenticated())
                                ? auth.getName()
                                : "Perfil";
                Button btnPerfil = new Button("👤 " + username);
                btnPerfil.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
                btnPerfil.getStyle()
                                .set("color", "white")
                                .set("font-weight", "600")
                                .set("font-size", "15px");

                Button btnSalir = new Button("Salir", e -> securityService.logout());
                btnSalir.addThemeVariants(ButtonVariant.LUMO_ERROR);
                btnSalir.getStyle()
                                .set("font-weight", "600")
                                .set("font-size", "15px");

                header.add(titulo, btnCarrito, btnPerfil, btnSalir);
                return header;
        }

        private VerticalLayout crearSidebarFiltros() {
                VerticalLayout sidebar = new VerticalLayout();
                sidebar.setWidth("270px");
                sidebar.setPadding(true);
                sidebar.setSpacing(true);
                sidebar.getStyle()
                                .set("background-color", "white")
                                .set("border-right", "1px solid #e8e8e8")
                                .set("overflow-y", "auto")
                                .set("position", "sticky")
                                .set("top", "0")
                                .set("height", "100vh");

                // Título de filtros
                H2 tituloFiltros = new H2("FILTROS");
                tituloFiltros.getStyle()
                                .set("margin-top", "0")
                                .set("margin-bottom", "20px")
                                .set("font-size", "18px")
                                .set("color", "#222")
                                .set("font-weight", "700")
                                .set("padding-bottom", "15px")
                                .set("border-bottom", "2px solid #667eea");

                // Busqueda
                busquedaField = new TextField("Buscar producto");
                busquedaField.setPlaceholder("Nombre, SKU...");
                busquedaField.setWidthFull();
                busquedaField.setPrefixComponent(VaadinIcon.SEARCH.create());
                busquedaField.getStyle()
                                .set("margin-bottom", "15px");
                busquedaField.addValueChangeListener(e -> aplicarFiltros());

                // Filtro por categoría
                categoriaFilter = new ComboBox<>("Categoría");
                categoriaFilter.setPlaceholder("Seleccionar categoría");
                categoriaFilter.setWidthFull();
                categoriaFilter.setItems(
                                "Papelería",
                                "Escritura",
                                "Arte y dibujo",
                                "Organización",
                                "Accesorios",
                                "Tecnología",
                                "Mochilas y estuches",
                                "Oficina y escritorio");
                categoriaFilter.getStyle()
                                .set("margin-bottom", "20px");
                categoriaFilter.addValueChangeListener(e -> aplicarFiltros());

                // Rango de precio
                H4 titlePrecio = new H4("Rango de precio");
                titlePrecio.getStyle()
                                .set("margin", "20px 0 15px 0")
                                .set("font-size", "14px")
                                .set("color", "#333")
                                .set("font-weight", "600");

                precioMinField = new NumberField("Mínimo ($)");
                precioMinField.setMin(0);
                precioMinField.setWidthFull();
                precioMinField.getStyle().set("margin-bottom", "10px");
                precioMinField.addValueChangeListener(e -> aplicarFiltros());

                precioMaxField = new NumberField("Máximo ($)");
                precioMaxField.setMin(0);
                precioMaxField.setWidthFull();
                precioMaxField.getStyle().set("margin-bottom", "20px");
                precioMaxField.addValueChangeListener(e -> aplicarFiltros());

                // Filtro de disponibilidad
                disponibleFilter = new Checkbox("Solo disponibles");
                disponibleFilter.setValue(false);
                disponibleFilter.getStyle()
                                .set("margin-bottom", "20px");
                disponibleFilter.addValueChangeListener(e -> aplicarFiltros());

                // Botón limpiar filtros
                Button btnLimpiar = new Button("Limpiar filtros", e -> limpiarFiltros());
                btnLimpiar.setWidthFull();
                btnLimpiar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                btnLimpiar.getStyle()
                                .set("border-radius", "8px")
                                .set("font-weight", "600");

                sidebar.add(
                                tituloFiltros,
                                busquedaField,
                                categoriaFilter,
                                titlePrecio,
                                precioMinField,
                                precioMaxField,
                                disponibleFilter,
                                btnLimpiar);

                return sidebar;
        }

        private VerticalLayout crearAreaProductos() {
                VerticalLayout area = new VerticalLayout();
                area.setSpacing(true);
                area.setPadding(true);

                // Barra superior con información
                HorizontalLayout barraInfo = new HorizontalLayout();
                barraInfo.setWidthFull();
                barraInfo.setAlignItems(FlexComponent.Alignment.CENTER);
                barraInfo.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

                H2 tituloProductos = new H2("Catálogo de Productos");
                tituloProductos.getStyle()
                                .set("margin", "0")
                                .set("flex-grow", "1")
                                .set("color", "#222")
                                .set("font-size", "28px");

                infoProductos = new Paragraph();
                infoProductos.getStyle()
                                .set("color", "#888")
                                .set("margin", "0")
                                .set("font-size", "14px");

                barraInfo.add(tituloProductos, infoProductos);

                // Contenedor de productos con grid responsivo
                productosContainer.setWidthFull();
                productosContainer.getStyle()
                                .set("display", "grid")
                                .set("grid-template-columns", "repeat(auto-fill, minmax(280px, 1fr))")
                                .set("gap", "24px")
                                .set("padding", "20px");

                area.add(barraInfo, productosContainer);
                return area;
        }

        private void cargarProductos() {
                productosList = productoService.findAll();
                actualizarVista(productosList);
        }

        private void aplicarFiltros() {
                List<Producto> filtered = productosList;

                // Filtro de búsqueda
                if (busquedaField != null && !busquedaField.getValue().isEmpty()) {
                        String query = busquedaField.getValue().toLowerCase();
                        filtered = filtered.stream()
                                        .filter(p -> p.getNombre().toLowerCase().contains(query)
                                                        || p.getSku().toLowerCase().contains(query))
                                        .collect(Collectors.toList());
                }

                // Filtro de categoría
                if (categoriaFilter != null && categoriaFilter.getValue() != null) {
                        String cat = categoriaFilter.getValue();
                        filtered = filtered.stream()
                                        .filter(p -> p.getCategoria().equals(cat))
                                        .collect(Collectors.toList());
                }

                // Filtro de precio
                if (precioMinField != null && precioMinField.getValue() != null) {
                        Double min = precioMinField.getValue();
                        filtered = filtered.stream()
                                        .filter(p -> p.getPrecio() >= min)
                                        .collect(Collectors.toList());
                }

                if (precioMaxField != null && precioMaxField.getValue() != null) {
                        Double max = precioMaxField.getValue();
                        filtered = filtered.stream()
                                        .filter(p -> p.getPrecio() <= max)
                                        .collect(Collectors.toList());
                }

                // Filtro de disponibilidad
                if (disponibleFilter != null && disponibleFilter.getValue()) {
                        filtered = filtered.stream()
                                        .filter(Producto::isDisponible)
                                        .collect(Collectors.toList());
                }

                actualizarVista(filtered);
        }

        private void limpiarFiltros() {
                busquedaField.clear();
                categoriaFilter.clear();
                precioMinField.clear();
                precioMaxField.clear();
                disponibleFilter.setValue(false);
                cargarProductos();
        }

        private void actualizarVista(List<Producto> productos) {
                productosContainer.removeAll();

                if (productos.isEmpty()) {
                        Div empty = new Div();
                        empty.setWidthFull();
                        empty.getStyle().set("text-align", "center").set("padding", "40px").set("color", "#999");
                        empty.add(new Paragraph("No se encontraron productos"));
                        productosContainer.add(empty);
                } else {
                        for (Producto producto : productos) {
                                productosContainer.add(crearTarjetaProducto(producto));
                        }
                }

                // Actualizar información de productos mostrados
                if (infoProductos != null) {
                        infoProductos.setText("Mostrando " + productos.size() + " productos");
                }
        }

        private Div crearTarjetaProducto(Producto producto) {
                Div tarjeta = new Div();
                tarjeta.addClassName("product-card");
                tarjeta.getStyle()
                                .set("background-color", "white")
                                .set("border", "1px solid #e8e8e8")
                                .set("border-radius", "12px")
                                .set("padding", "0")
                                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.08)")
                                .set("overflow", "hidden")
                                .set("cursor", "pointer")
                                .set("transition", "all 0.3s cubic-bezier(0.4, 0, 0.2, 1)")
                                .set("display", "flex")
                                .set("flex-direction", "column")
                                .set("height", "100%");

                // Contenedor de imágenes
                Div imagenContainer = new Div();
                imagenContainer.getStyle()
                                .set("position", "relative")
                                .set("width", "100%")
                                .set("height", "200px")
                                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                                .set("overflow", "hidden")
                                .set("cursor", "pointer");

                List<String> imagenes = producto.getUrlsImagenes();
                boolean tieneImagenes = imagenes != null && !imagenes.isEmpty() && imagenes.stream().anyMatch(u -> u != null && !u.trim().isEmpty());
                
                if (tieneImagenes) {
                        String imagenUrl = imagenes.get(0).trim();
                        // Validar que la URL sea válida
                        if (!imagenUrl.isEmpty()) {
                                // Agregar "/" si no comienza con "/" o "http"
                                if (!imagenUrl.startsWith("/") && !imagenUrl.startsWith("http")) {
                                        imagenUrl = "/" + imagenUrl;
                                }
                                
                                Image img = new Image(imagenUrl, producto.getNombre());
                                img.getStyle()
                                                .set("width", "100%")
                                                .set("height", "100%")
                                                .set("object-fit", "cover")
                                                .set("transition", "transform 0.3s ease")
                                                .set("display", "block");
                                imagenContainer.add(img);
                                
                                // Badge de múltiples imágenes si existen
                                List<String> imagenesValidas = imagenes.stream()
                                        .filter(u -> u != null && !u.trim().isEmpty())
                                        .collect(Collectors.toList());
                                        
                                if (imagenesValidas.size() > 1) {
                                        Span badgeMultiples = new Span("📸 " + imagenesValidas.size() + " fotos");
                                        badgeMultiples.getStyle()
                                                        .set("position", "absolute")
                                                        .set("bottom", "10px")
                                                        .set("left", "10px")
                                                        .set("background", "rgba(102, 126, 234, 0.95)")
                                                        .set("color", "white")
                                                        .set("padding", "6px 12px")
                                                        .set("border-radius", "20px")
                                                        .set("font-size", "12px")
                                                        .set("font-weight", "600")
                                                        .set("z-index", "10")
                                                        .set("backdrop-filter", "blur(5px)");
                                        imagenContainer.add(badgeMultiples);
                                }
                        } else {
                                // URL vacía, mostrar placeholder
                                crearPlaceholderImagen(imagenContainer);
                        }
                } else {
                        // Sin imágenes, mostrar placeholder
                        crearPlaceholderImagen(imagenContainer);
                }

                // Badge de disponibilidad
                Span badge = new Span(producto.isDisponible() ? "Disponible" : "Agotado");
                badge.getStyle()
                                .set("position", "absolute")
                                .set("top", "10px")
                                .set("right", "10px")
                                .set("background",
                                                producto.isDisponible() ? "rgba(40, 167, 69, 0.9)"
                                                                : "rgba(220, 53, 69, 0.9)")
                                .set("color", "white")
                                .set("padding", "6px 12px")
                                .set("border-radius", "20px")
                                .set("font-size", "12px")
                                .set("font-weight", "600")
                                .set("z-index", "10");
                imagenContainer.add(badge);

                // Contenido del producto
                Div contenido = new Div();
                contenido.getStyle()
                                .set("padding", "16px")
                                .set("flex-grow", "1")
                                .set("display", "flex")
                                .set("flex-direction", "column");

                Span categoriaBadge = new Span(producto.getCategoria());
                categoriaBadge.getStyle()
                                .set("display", "inline-block")
                                .set("background", "#f0f2ff")
                                .set("color", "#667eea")
                                .set("padding", "4px 10px")
                                .set("border-radius", "16px")
                                .set("font-size", "11px")
                                .set("font-weight", "600")
                                .set("margin-bottom", "10px");

                H4 nombre = new H4(producto.getNombre());
                nombre.getStyle()
                                .set("margin", "0 0 8px 0")
                                .set("color", "#222")
                                .set("font-size", "16px")
                                .set("font-weight", "600");

                Paragraph descripcion = new Paragraph();
                String desc = producto.getDescripcion();
                descripcion.setText((desc != null && !desc.isEmpty())
                                ? (desc.length() > 80 ? desc.substring(0, 80) + "..." : desc)
                                : "SKU: " + producto.getSku());

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

                HorizontalLayout precioLayout = new HorizontalLayout();

                Long precioGuarani = producto.getPrecio() != null ? Math.round(producto.getPrecio()) : null;
                Span precioGuaraniBadge = new Span(
                                precioGuarani != null ? "Gs. " + String.format("%,d", precioGuarani) : "");
                precioGuaraniBadge.getStyle().set("font-size", "12px").set("color", "#888").set("margin-left", "10px");

                precioLayout.add(precioGuaraniBadge);

                Button btnAgregarCarrito = new Button("Agregar al carrito");
                btnAgregarCarrito.setWidthFull();
                btnAgregarCarrito.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                btnAgregarCarrito.setEnabled(producto.isDisponible() && producto.getStockActual() > 0);

                contenido.add(categoriaBadge, nombre, descripcion, stockInfo, precioLayout, btnAgregarCarrito);

                tarjeta.add(imagenContainer, contenido);

                // Abrir dialog con detalle del producto al hacer click
                tarjeta.getElement().addEventListener("click", e -> {
                        new ProductDetailDialog(producto).open();
                });

                return tarjeta;
        }

        /**
         * Crea un placeholder de imagen cuando no hay imagen disponible
         */
        private void crearPlaceholderImagen(Div imagenContainer) {
                Div placeholderDiv = new Div();
                placeholderDiv.getStyle()
                                .set("width", "100%")
                                .set("height", "100%")
                                .set("display", "flex")
                                .set("flex-direction", "column")
                                .set("align-items", "center")
                                .set("justify-content", "center")
                                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)");

                Icon icono = VaadinIcon.PACKAGE.create();
                icono.getStyle()
                                .set("color", "white")
                                .set("width", "60px")
                                .set("height", "60px")
                                .set("opacity", "0.8")
                                .set("margin-bottom", "10px");

                Span textoPlaceholder = new Span("Sin foto");
                textoPlaceholder.getStyle()
                                .set("color", "white")
                                .set("font-size", "13px")
                                .set("opacity", "0.7");

                placeholderDiv.add(icono, textoPlaceholder);
                imagenContainer.add(placeholderDiv);
        }
}
