package com.libreria.edex.service;

import com.libreria.edex.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String remitente;

    @Value("${app.mail.from-name:Librería EDEX}")
    private String fromName;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean isConfigured() {
        return remitente != null && !remitente.isBlank();
    }

    private String remitenteFormateado() {
        return fromName + " <" + remitente.trim() + ">";
    }

    private void enviar(String destinatario, String asunto, String cuerpo) {
        if (!isConfigured()) {
            throw new IllegalStateException("Gmail no configurado. Completá spring.mail.username y spring.mail.password.");
        }
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitenteFormateado());
        mensaje.setReplyTo(remitente.trim());
        mensaje.setTo(destinatario);
        mensaje.setSubject(asunto);
        mensaje.setText(cuerpo);
        mailSender.send(mensaje);
    }

    public void enviarVerificacion(Usuario usuario) {
        String enlace = baseUrl + "/verificar?token=" + usuario.getTokenVerificacion();
        enviar(usuario.getEmail(), "Verificá tu cuenta - Librería Edex", """
                Hola %s,

                Gracias por registrarte en Librería EDEX.

                Para activar tu cuenta, hacé clic en el siguiente enlace:
                %s

                El enlace expira en 24 horas.

                Si no creaste esta cuenta, ignorá este mensaje.

                Librería Edex
                """.formatted(usuario.getUsername(), enlace));
    }

    public void enviarRecuperacionContrasena(Usuario usuario) {
        String enlace = baseUrl + "/restablecer-contrasena?token=" + usuario.getTokenResetPassword();
        enviar(usuario.getEmail(), "Recuperación de contraseña - Libreria Edex", """
                Hola %s,

                Recibimos una solicitud para restablecer tu contraseña en Librería Edex.

                Para elegir una nueva contraseña, haz clic en el siguiente enlace:
                %s

                El enlace expira en 1 hora.

                Si no solicitaste este cambio, ignora este mensaje. Tu contraseña no se modificará.

                Libreria Edex
                """.formatted(usuario.getUsername(), enlace));
    }
}
