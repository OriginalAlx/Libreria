package com.libreria.edex.ui;

import com.libreria.edex.service.AuthService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("verificar")
@PageTitle("Verificar correo | Librería EDEX")
@AnonymousAllowed
public class VerificarEmailView extends VerticalLayout implements BeforeEnterObserver {

    private final AuthService authService;

    public VerificarEmailView(AuthService authService) {
        this.authService = authService;
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        removeAll();

        String token = event.getLocation()
                .getQueryParameters()
                .getParameters()
                .getOrDefault("token", java.util.List.of())
                .stream()
                .findFirst()
                .orElse(null);

        Paragraph mensaje = new Paragraph();
        H2 titulo = new H2("Verificación de correo");

        if (token == null || token.isBlank()) {
            mensaje.setText("Enlace de verificación inválido.");
        } else {
            try {
                authService.verificarEmail(token);
                UI.getCurrent().navigate("login?verified");
                return;
            } catch (IllegalArgumentException ex) {
                mensaje.setText(ex.getMessage());
                Notification.show(ex.getMessage(), 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }

        VerticalLayout panel = new VerticalLayout(titulo, mensaje);
        panel.setAlignItems(FlexComponent.Alignment.CENTER);
        panel.getStyle()
                .set("border", "1px solid #e0e0e0")
                .set("border-radius", "16px")
                .set("padding", "30px")
                .set("background-color", "#ffffff")
                .set("max-width", "420px");

        add(panel);
    }
}
