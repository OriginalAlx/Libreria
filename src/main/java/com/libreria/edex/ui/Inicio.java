package com.libreria.edex.ui;

import com.libreria.edex.service.AuthService;
import com.libreria.edex.service.SecurityService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

@Route("login")
@PageTitle("Iniciar sesión | Librería EDEX")
@PermitAll
public class Inicio extends VerticalLayout implements BeforeEnterObserver {

    private final SecurityService securityService;
    private final AuthService authService;
    private final EmailField email = new EmailField("Correo electrónico *");
    private final PasswordField contrasena = new PasswordField("Contraseña *");

    public Inicio(SecurityService securityService, AuthService authService) {
        this.securityService = securityService;
        this.authService = authService;

        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        getStyle()
                .set("background-image", "url('http://localhost:8081/images/fondo-libreriaedex.png')")
                .set("background-size", "cover")
                .set("background-position", "center")
                .set("background-repeat", "no-repeat");

        StreamResource logoResource = new StreamResource("libreria-edex-logo.png",
                () -> getClass().getResourceAsStream("/META-INF/resources/images/libreria-edex-logo.png"));
        Image logo = new Image(logoResource, "Logo de la aplicación");
        logo.setWidth("300px");
        logo.getStyle().set("height", "auto").set("margin-bottom", "5px");

        email.setWidth("300px");
        email.setRequired(true);
        email.setErrorMessage("Ingrese un correo válido");
        email.getElement().setAttribute("autocomplete", "email");

        contrasena.setWidth("300px");
        contrasena.setRequired(true);
        contrasena.getElement().setAttribute("autocomplete", "current-password");

        VerticalLayout campos = new VerticalLayout(email, contrasena);
        campos.setSpacing(true);
        campos.setPadding(false);
        campos.setAlignItems(FlexComponent.Alignment.CENTER);

        Button botonIngresar = new Button("Ingresar", e -> iniciarSesion());
        botonIngresar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        botonIngresar.setWidth("300px");

        Button botonRegistro = new Button("Registrarse", e -> UI.getCurrent().navigate("registro"));
        botonRegistro.setWidth("300px");

        Anchor enlaceOlvido = new Anchor("recuperar-contrasena", "¿Olvidaste tu contraseña?");

        VerticalLayout acciones = new VerticalLayout(botonIngresar, enlaceOlvido, botonRegistro);
        acciones.setSpacing(true);
        acciones.setPadding(false);
        acciones.setAlignItems(FlexComponent.Alignment.CENTER);
        acciones.getStyle().set("margin-top", "20px");

        VerticalLayout formulario = new VerticalLayout(logo, campos, acciones);
        formulario.setAlignItems(FlexComponent.Alignment.CENTER);
        formulario.setSpacing(true);
        formulario.getStyle()
                .set("border", "1px solid rgba(224,224,224,0.6)")
                .set("border-radius", "16px")
                .set("padding", "30px")
                .set("box-shadow", "0 4px 12px rgba(0,0,0,0.15), 0 12px 24px rgba(0,0,0,0.25)")
                .set("background-color", "#ffffff")
                .set("max-width", "400px")
                .set("width", "100%");

        add(formulario);
    }

    private void iniciarSesion() {
        email.setInvalid(false);
        contrasena.setInvalid(false);

        if (email.isEmpty() || email.isInvalid()) {
            email.setInvalid(true);
            mostrarError("Ingrese un correo electrónico válido.");
            return;
        }
        if (contrasena.isEmpty()) {
            contrasena.setInvalid(true);
            mostrarError("Ingrese su contraseña.");
            return;
        }

        try {
            authService.validarCredenciales(email.getValue(), contrasena.getValue());
            securityService.login(email.getValue(), contrasena.getValue());
            UI.getCurrent().navigate(CatalogoView.class);
        } catch (BadCredentialsException ex) {
            mostrarError(ex.getMessage());
        } catch (AuthenticationException ex) {
            mostrarError("No se pudo iniciar sesión. Verificá tus datos.");
        }
    }

    private void mostrarError(String mensaje) {
        Notification notification = Notification.show(mensaje, 5000, Notification.Position.MIDDLE);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation().getQueryParameters().getParameters().containsKey("verified")) {
            Notification.show("Correo verificado. Ya podés iniciar sesión.", 5000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        }
        if (event.getLocation().getQueryParameters().getParameters().containsKey("registered")) {
            Notification.show("Registro exitoso. Revisá tu correo para activar la cuenta.", 6000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        }
        if (event.getLocation().getQueryParameters().getParameters().containsKey("ready")) {
            Notification.show("Registro exitoso. Ya podés iniciar sesión.", 5000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        }
        if (event.getLocation().getQueryParameters().getParameters().containsKey("resetSent")) {
            Notification.show(
                    "Si el correo está registrado, recibirás un enlace para restablecer tu contraseña.",
                    6000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        }
        if (event.getLocation().getQueryParameters().getParameters().containsKey("passwordReset")) {
            Notification.show("Contraseña actualizada", 5000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        }
    }
}
