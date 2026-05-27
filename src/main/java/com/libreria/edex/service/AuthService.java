package com.libreria.edex.service;

import com.libreria.edex.dto.RegistroRequest;
import com.libreria.edex.model.Cliente;
import com.libreria.edex.model.Rol;
import com.libreria.edex.model.Usuario;
import com.libreria.edex.model.enums.RolNombre;
import com.libreria.edex.repository.ClienteRepository;
import com.libreria.edex.repository.RolRepository;
import com.libreria.edex.repository.UsuarioRepository;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.auth.skip-email-verification:false}")
    private boolean skipEmailVerification;

    public AuthService(
            UsuarioRepository usuarioRepository,
            ClienteRepository clienteRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public void registrar(@Valid RegistroRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        String username = request.getUsername().trim();

        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Ya existe una cuenta con ese correo electrónico.");
        }
        if (usuarioRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Ese nombre de usuario ya está en uso.");
        }

        Rol rolCliente = rolRepository.findByNombre(RolNombre.CLIENTE)
                .orElseThrow(() -> new IllegalStateException("Rol CLIENTE no configurado."));

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(rolCliente);
        usuario.setActivo(true);
        usuario.setEmailVerificado(skipEmailVerification || !emailService.isConfigured());
        usuario.setTokenVerificacion(UUID.randomUUID().toString());
        usuario.setTokenExpiracion(LocalDateTime.now().plusHours(24));
        usuario = usuarioRepository.save(usuario);

        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);
        cliente.setNombre(request.getNombre().trim());
        cliente.setApellido(request.getApellido().trim());
        cliente.setEmail(email);
        clienteRepository.save(cliente);

        if (!usuario.isEmailVerificado()) {
            try {
                emailService.enviarVerificacion(usuario);
            } catch (Exception ex) {
                throw new IllegalStateException(
                        "Cuenta creada pero no se pudo enviar el correo de verificación. "
                                + "Verificá la configuración SMTP.", ex);
            }
        }
    }

    @Transactional
    public void verificarEmail(String token) {
        Usuario usuario = usuarioRepository.findByTokenVerificacion(token)
                .orElseThrow(() -> new IllegalArgumentException("Enlace de verificación inválido."));

        if (usuario.isEmailVerificado()) {
            return;
        }

        if (usuario.getTokenExpiracion() == null
                || usuario.getTokenExpiracion().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("El enlace de verificación expiró. Registrate nuevamente.");
        }

        usuario.setEmailVerificado(true);
        usuario.setTokenVerificacion(null);
        usuario.setTokenExpiracion(null);
        usuarioRepository.save(usuario);
    }

    public Usuario validarCredenciales(String email, String password) {
        String emailNormalizado = email.trim().toLowerCase();

        Usuario usuario = usuarioRepository.findByEmail(emailNormalizado)
                .orElseThrow(() -> new BadCredentialsException("Correo o contraseña incorrectos."));

        if (!usuario.isActivo()) {
            throw new BadCredentialsException("Tu cuenta está desactivada. Contactá al administrador.");
        }

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new BadCredentialsException("Correo o contraseña incorrectos.");
        }

        if (!usuario.isEmailVerificado()) {
            throw new BadCredentialsException(
                    "Debés verificar tu correo antes de iniciar sesión. Revisá tu bandeja de entrada.");
        }

        return usuario;
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    }

    public boolean requiereVerificacionEmail() {
        return !skipEmailVerification && emailService.isConfigured();
    }

    @Transactional
    public void solicitarRecuperacionContrasena(String email) {
        String emailNormalizado = email.trim().toLowerCase();
        usuarioRepository.findByEmail(emailNormalizado).ifPresent(usuario -> {
            if (!usuario.isActivo()) {
                return;
            }
            usuario.setTokenResetPassword(UUID.randomUUID().toString());
            usuario.setTokenResetExpiracion(LocalDateTime.now().plusHours(1));
            usuarioRepository.save(usuario);
            emailService.enviarRecuperacionContrasena(usuario);
        });
    }

    @Transactional
    public void restablecerContrasena(String token, String nuevaContrasena) {
        if (nuevaContrasena == null || nuevaContrasena.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }

        Usuario usuario = usuarioRepository.findByTokenResetPassword(token)
                .orElseThrow(() -> new IllegalArgumentException("Enlace de recuperación inválido o expirado."));

        if (usuario.getTokenResetExpiracion() == null
                || usuario.getTokenResetExpiracion().isBefore(LocalDateTime.now())) {
            usuario.setTokenResetPassword(null);
            usuario.setTokenResetExpiracion(null);
            usuarioRepository.save(usuario);
            throw new IllegalArgumentException("El enlace de recuperación expiró. Solicitá uno nuevo.");
        }

        usuario.setPassword(passwordEncoder.encode(nuevaContrasena));
        usuario.setTokenResetPassword(null);
        usuario.setTokenResetExpiracion(null);
        usuarioRepository.save(usuario);
    }

    public boolean esTokenRecuperacionValido(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        return usuarioRepository.findByTokenResetPassword(token)
                .filter(u -> u.getTokenResetExpiracion() != null)
                .filter(u -> u.getTokenResetExpiracion().isAfter(LocalDateTime.now()))
                .isPresent();
    }
}
