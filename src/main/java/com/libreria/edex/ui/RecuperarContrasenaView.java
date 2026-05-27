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
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("recuperar-contrasena")
@PageTitle("Recuperar contraseña | Librería EDEX")
@AnonymousAllowed
public class RecuperarContrasenaView extends VerticalLayout {

    public RecuperarContrasenaView(AuthService authService) {
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        H2 titulo = new H2("Recuperar contraseña");
        Paragraph info = new Paragraph(
                "Ingresá el correo de tu cuenta. Te enviaremos un enlace por Gmail para restablecer la contraseña.");

        EmailField email = new EmailField("Correo electrónico *");
        email.setWidth("320px");
        email.setRequired(true);
        email.setErrorMessage("Ingresá un correo válido");

        Button enviar = new Button("Enviar enlace", e -> {
            if (email.isEmpty() || email.isInvalid()) {
                email.setInvalid(true);
                Notification.show("Ingresá un correo electrónico válido.", 4000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            try {
                authService.solicitarRecuperacionContrasena(email.getValue());
                UI.getCurrent().navigate("login?resetSent");
            } catch (Exception ex) {
                Notification.show(
                        "No se pudo enviar el correo. Verifica la configuración de Gmail en application.properties.",
                        6000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        enviar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        enviar.setWidth("320px");

        Anchor volver = new Anchor("login", "Volver al inicio de sesión");

        VerticalLayout panel = new VerticalLayout(titulo, info, email, enviar, volver);
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

        info.getStyle().set("font-size", "0.9rem").set("color", "#555").set("text-align", "center");

        add(panel);
    }
}
