package com.libreria.edex.ui;

import com.libreria.edex.dto.RegistroRequest;
import com.libreria.edex.service.AuthService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("registro")
@PageTitle("Registro | Librería EDEX")
@AnonymousAllowed
public class Registro extends VerticalLayout {

    private final AuthService authService;
    private final Binder<RegistroRequest> binder = new Binder<>(RegistroRequest.class);

    public Registro(AuthService authService) {
        this.authService = authService;

        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        RegistroRequest request = new RegistroRequest();
        binder.setBean(request);

        TextField nombre = new TextField("Nombre *");
        TextField apellido = new TextField("Apellido *");
        EmailField email = new EmailField("Correo electrónico *");
        TextField username = new TextField("Nombre de usuario *");
        PasswordField password = new PasswordField("Contraseña *");
        PasswordField confirmPassword = new PasswordField("Confirmar contraseña *");

        nombre.setWidth("320px");
        apellido.setWidth("320px");
        email.setWidth("320px");
        username.setWidth("320px");
        password.setWidth("320px");
        confirmPassword.setWidth("320px");

        email.setRequired(true);
        email.setErrorMessage("Ingresa un correo electrónico válido (ej: nombre@gmail.com)");

        binder.forField(nombre).asRequired("El nombre es obligatorio").bind("nombre");
        binder.forField(apellido).asRequired("El apellido es obligatorio").bind("apellido");
        binder.forField(email).asRequired("El correo es obligatorio").withValidator(
                value -> value != null && value.matches("^[\\w.+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"),
                "Ingresá un correo electrónico válido").bind("email");
        binder.forField(username).asRequired("El usuario es obligatorio")
                .withValidator(u -> u.length() >= 3, "Mínimo 3 caracteres").bind("username");
        binder.forField(password).asRequired("La contraseña es obligatoria")
                .withValidator(p -> p.length() >= 6, "Mínimo 6 caracteres").bind("password");

        Button btnRegistrar = new Button("Crear cuenta", e -> registrar(password, confirmPassword));
        btnRegistrar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnRegistrar.setWidth("320px");

        Anchor volverLogin = new Anchor("login", "¿Ya tenés cuenta? Iniciá sesión");

        /*Paragraph info = new Paragraph(
                "Te enviaremos un correo de verificación para activar la cuenta.");*/

        //info.getStyle().set("font-size", "0.85rem").set("color", "#666").set("max-width", "320px");

        VerticalLayout formulario = new VerticalLayout(
                new H2("Crear cuenta"),
                //info,
                nombre,
                apellido,
                email,
                username,
                password,
                confirmPassword,
                btnRegistrar,
                volverLogin
        );
        formulario.setAlignItems(FlexComponent.Alignment.CENTER);
        formulario.setSpacing(true);
        formulario.getStyle()
                .set("border", "1px solid #e0e0e0")
                .set("border-radius", "16px")
                .set("padding", "30px")
                .set("box-shadow", "0 6px 18px rgba(0,0,0,0.12)")
                .set("background-color", "#ffffff")
                .set("max-width", "420px")
                .set("width", "100%");

        add(formulario);
    }

    private void registrar(PasswordField password, PasswordField confirmPassword) {
        if (!password.getValue().equals(confirmPassword.getValue())) {
            confirmPassword.setInvalid(true);
            Notification.show("Las contraseñas no coinciden.", 4000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        confirmPassword.setInvalid(false);

        try {
            binder.writeBean(binder.getBean());
            authService.registrar(binder.getBean());
            String destino = authService.requiereVerificacionEmail() ? "login?registered" : "login?ready";
            UI.getCurrent().navigate(destino);
        } catch (ValidationException ex) {
            Notification.show("Completa todos los campos correctamente.", 4000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (IllegalArgumentException ex) {
            Notification.show(ex.getMessage(), 5000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception ex) {
            Notification.show("No se pudo completar el registro. Verificá la configuración de correo.",
                    5000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
