package com.libreria.edex.service;

import com.libreria.edex.model.Usuario;
import com.libreria.edex.model.enums.RolNombre;
import com.libreria.edex.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        if (!usuario.isActivo()) {
            throw new UsernameNotFoundException("Cuenta desactivada.");
        }

        if (!usuario.isEmailVerificado()) {
            throw new UsernameNotFoundException("Correo no verificado.");
        }

        RolNombre rol = usuario.getRol().getNombre();
        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .authorities(new SimpleGrantedAuthority("ROLE_" + rol.name()))
                .build();
    }
}
