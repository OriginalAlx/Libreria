package com.libreria.edex.ui;

import com.libreria.edex.model.Producto;
import com.libreria.edex.service.ProductoService;
import com.libreria.edex.service.SecurityService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route("catalogo")
@PageTitle("Catálogo | Librería E")
@PermitAll
public class CatalogoView extends VerticalLayout {

    public CatalogoView(SecurityService securityService, ProductoService productoService) {
        H1 titulo = new H1("LIBRERÍA EDEX");
        Paragraph bienvenida = new Paragraph("¡Bienvenido!");

        Button cerrarSesion = new Button("Cerrar sesión", e -> {
            securityService.logout();
            getUI().ifPresent(ui -> ui.navigate("login"));
        });

        HorizontalLayout header = new HorizontalLayout(titulo, cerrarSesion);
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);

        ComboBox<String> categoriaFilter = new ComboBox<>("Categoría");
        categoriaFilter.setItems("Cuadernos", "Mochilas", "Calculadoras", "Cartucheras");

        NumberField precioMin = new NumberField("Precio mínimo");
        NumberField precioMax = new NumberField("Precio máximo");

        Checkbox disponibleFilter = new Checkbox("Solo disponibles");

        Button aplicarFiltros = new Button("Aplicar filtros");

        HorizontalLayout filtros = new HorizontalLayout(
            categoriaFilter, precioMin, precioMax, disponibleFilter, aplicarFiltros
        );

        Grid<Producto> grid = new Grid<>(Producto.class, false);
        grid.addColumn(Producto::getNombre).setHeader("Nombre");
        grid.addColumn(Producto::getCategoria).setHeader("Categoría");
        grid.addColumn(Producto::getPrecio).setHeader("Precio");
        grid.addColumn(p -> p.isDisponible() ? "Sí" : "No").setHeader("Disponible");

        grid.setItems(productoService.findAll());

        aplicarFiltros.addClickListener(e -> {
            grid.setItems(productoService.findByFilters(
                categoriaFilter.getValue(),
                precioMin.getValue(),
                precioMax.getValue(),
                disponibleFilter.getValue()
            ));
        });


        add(header, bienvenida, filtros, grid);
        setSizeFull();
    }
}
