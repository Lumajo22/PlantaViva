/*
 * ════════════════════════════════════════════════════════════════════
 *   PlantaViva — SecurityConfig.java
 *   Función:  Configuración de Spring Security + OAuth2 Google
 * ════════════════════════════════════════════════════════════════════
 */
package com.plantaviva.config;

import com.plantaviva.entity.Usuario;
import com.plantaviva.enums.RolUsuario;
import com.plantaviva.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Map;

/**
 * Configuración de seguridad con OAuth2 Google.
 *
 * <p>Flujo de autenticación:
 * <ol>
 *   <li>Usuario hace clic en "Login con Google" en el frontend</li>
 *   <li>Se redirige a Google OAuth</li>
 *   <li>Google devuelve al usuario autenticado</li>
 *   <li>Este servicio busca o crea el usuario en la BD</li>
 *   <li>Redirige al frontend con sesión activa</li>
 * </ol>
 *
 * @author  Equipo PlantaViva
 * @version 1.0.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UsuarioRepository usuarioRepository;

    public SecurityConfig(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/api/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/login/**",
                    "/oauth2/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("http://localhost:5173/auth/callback", true)
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(oauth2UserService())
                )
            )
            .logout(logout -> logout
                .logoutSuccessUrl("http://localhost:5173")
                .permitAll()
            );

        return http.build();
    }

    /**
     * Servicio personalizado que maneja el login de Google.
     * Busca o crea el usuario en la base de datos.
     */
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        
        return userRequest -> {
            OAuth2User oauth2User = delegate.loadUser(userRequest);
            
            // Extraer información del usuario de Google
            Map<String, Object> attributes = oauth2User.getAttributes();
            String googleId = (String) attributes.get("sub");
            String email = (String) attributes.get("email");
            String nombre = (String) attributes.get("name");
            
            // Buscar o crear usuario en la BD
            Usuario usuario = usuarioRepository.findByGoogleId(googleId)
                .orElseGet(() -> {
                    Usuario nuevoUsuario = Usuario.builder()
                        .googleId(googleId)
                        .email(email)
                        .nombre(nombre)
                        .rol(RolUsuario.TECNICO)
                        .activo(true)
                        .build();
                    return usuarioRepository.save(nuevoUsuario);
                });
            
            return oauth2User;
        };
    }

    /**
     * Configuración CORS para permitir requests desde el frontend.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}