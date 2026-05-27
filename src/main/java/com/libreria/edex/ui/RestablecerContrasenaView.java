package com.libreria.edex.ui;

import com.libreria.edex.service.AuthService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("restablecer-contrasena")
@PageTitle("Restablecer contraseña | Librería EDEX")
@AnonymousAllowed
public class RestablecerContrasenaView extends VerticalLayout implements BeforeEnterObserver {

    private final AuthService authService;
    private String token;

    private final PasswordField password = new PasswordField("Nueva contraseña *");
    private final PasswordField confirmPassword = new PasswordField("Confirmar contraseña *");
    private final Paragraph mensajeError = new Paragraph();

    public RestablecerContrasenaView(AuthService authService) {
        this.authService = authService;

        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        H2 titulo = new H2("Nueva contraseña");
        Paragraph info = new Paragraph("Elegí una contraseña segura de al menos 6 caracteres.");

        password.setWidth("320px");
        confirmPassword.setWidth("320px");
        mensajeError.setVisible(false);
        mensajeError.getStyle().set("color", "var(--lumo-error-color)");

        Button guardar = new Button("Guardar contraseña", e -> guardarContrasena());
        guardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        guardar.setWidth("320px");

        Anchor volver = new Anchor("login", "Volver al inicio de sesión");

        VerticalLayout panel = new VerticalLayout(titulo, info, password, confirmPassword, mensajeError, guardar, volver);
        panel.setAlignItems(FlexComponent.Alignment.CENTER);
        panel.setSpacing(true);
        panel.getStyle()
                .set("border", "1px solid #e0e0e0")
                .set("border-radius", "16px")
                .set("padding", "30px")
                .set("box-shadow", "0 6px 18px rgba(0,0,0,0.12)")
                .set("background-color", "#ffffff")
                .set("max-width", "420px")
                .set("width", "100%");

        add(panel);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        token = event.getLocation()
                .getQueryParameters()
                .getParameters()
                .getOrDefault("token", java.util.List.of())
                .stream()
                .findFirst()
                .orElse(null);

        if (!authService.esTokenRecuperacionValido(token)) {
            removeAll();
            Paragraph error = new Paragraph("El enlace de recuperación es inválido o expiró.");
            Anchor solicitar = new Anchor("recuperar-contrasena", "Solicitar un nuevo enlace");
            VerticalLayout panel = new VerticalLayout(new H2("Enlace no válido"), error, solicitar);
            panel.setAlignItems(FlexComponent.Alignment.CENTER);
            add(panel);
        }
    }

    private void guardarContrasena() {
        mensajeError.setVisible(false);
        password.setInvalid(false);
        confirmPassword.setInvalid(false);

        if (password.isEmpty() || password.getValue().length() < 6) {
            password.setInvalid(true);
            mensajeError.setText("La contraseña debe tener al menos 6 caracteres.");
            mensajeError.setVisible(true);
            return;
        }
        if (!password.getValue().equals(confirmPassword.getValue())) {
            confirmPassword.setInvalid(true);
            mensajeError.setText("Las contraseñas no coinciden.");
            mensajeError.setVisible(true);
            return;
        }

        try {
            authService.restablecerContrasena(token, password.getValue());
            UI.getCurrent().navigate("login?passwordReset");
        } catch (IllegalArgumentException ex) {
            mensajeError.setText(ex.getMessage());
            mensajeError.setVisible(true);
            Notification.show(ex.getMessage(), 5000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
