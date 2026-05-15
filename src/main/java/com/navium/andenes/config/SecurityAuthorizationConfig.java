package com.navium.andenes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

/**
 * Configuración de autorización basada en roles para el microservicio de andenes.
 * 
 * Este archivo define las reglas de acceso a los endpoints del microservicio basándose en los roles
 * de los usuarios contenidos en el token JWT. La librería navium-security-lib se encarga de validar
 * el token y extraer los roles, mientras que este customizador define qué roles pueden acceder a cada
 * endpoint específico.
 * 
 * Flujo de seguridad:
 * 1. El token JWT es validado por JwtAuthorizationFilter (navium-security-lib)
 * 2. Los roles del token se mapean a authorities (ROL_OPERADOR, ROL_CENTRO_MANDO)
 * 3. Este customizador verifica si el usuario tiene el rol requerido para cada endpoint
 * 
 * Roles definidos:
 * - ROL_OPERADOR: Acceso a operaciones de lectura y asignación de andenes
 * - ROL_CENTRO_MANDO: Acceso completo a todas las operaciones
 */
@Configuration
public class SecurityAuthorizationConfig {
    
    @Bean
    public Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> andenesAuthorizationCustomizer() {
        return (AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) -> {
            // Swagger UI - public access
            auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll();
            
            // Operaciones de lectura (GET) - ROL_OPERADOR y ROL_CENTRO_MANDO
            auth.requestMatchers(HttpMethod.GET, "/api/v0/andenes/{id}")
                .hasAnyAuthority("ROL_OPERADOR", "ROL_CENTRO_MANDO");
            auth.requestMatchers(HttpMethod.GET, "/api/v0/andenes/{id}/asignacion")
                .hasAnyAuthority("ROL_OPERADOR", "ROL_CENTRO_MANDO");
            auth.requestMatchers(HttpMethod.GET, "/api/v0/andenes/asignacion")
                .hasAnyAuthority("ROL_OPERADOR", "ROL_CENTRO_MANDO");
            auth.requestMatchers(HttpMethod.GET, "/api/v0/andenes")
                .hasAnyAuthority("ROL_OPERADOR", "ROL_CENTRO_MANDO");
            auth.requestMatchers(HttpMethod.GET, "/api/v0/andenes/zona/{zona}")
                .hasAnyAuthority("ROL_OPERADOR", "ROL_CENTRO_MANDO");
            auth.requestMatchers(HttpMethod.GET, "/api/v0/andenes/codigo/{codigo}")
                .hasAnyAuthority("ROL_OPERADOR", "ROL_CENTRO_MANDO");
            auth.requestMatchers(HttpMethod.GET, "/api/v0/andenes/disponibles")
                .hasAnyAuthority("ROL_OPERADOR", "ROL_CENTRO_MANDO");
            auth.requestMatchers(HttpMethod.GET, "/api/v0/andenes/ocupados")
                .hasAnyAuthority("ROL_OPERADOR", "ROL_CENTRO_MANDO");
            auth.requestMatchers(HttpMethod.GET, "/api/v0/andenes/mantenimiento")
                .hasAnyAuthority("ROL_OPERADOR", "ROL_CENTRO_MANDO");
            
            // Operaciones de escritura crítica (POST crear, DELETE) - Solo ROL_CENTRO_MANDO
            auth.requestMatchers(HttpMethod.POST, "/api/v0/andenes")
                .hasAuthority("ROL_CENTRO_MANDO");
            auth.requestMatchers(HttpMethod.DELETE, "/api/v0/andenes/{id}")
                .hasAuthority("ROL_CENTRO_MANDO");
            
            // Operaciones de patio (asignar) - ROL_OPERADOR y ROL_CENTRO_MANDO
            auth.requestMatchers(HttpMethod.POST, "/api/v0/andenes/{id}/asignar")
                .hasAnyAuthority("ROL_OPERADOR", "ROL_CENTRO_MANDO");
            
            // Operaciones especiales (solo CENTRO_MANDO)
            auth.requestMatchers(HttpMethod.POST, "/api/v0/andenes/{id}/liberar")
                .hasAuthority("ROL_CENTRO_MANDO");
            auth.requestMatchers(HttpMethod.POST, "/api/v0/andenes/{id}/mantenimiento")
                .hasAuthority("ROL_CENTRO_MANDO");
            auth.requestMatchers(HttpMethod.POST, "/api/v0/andenes/{id}/habilitar")
                .hasAuthority("ROL_CENTRO_MANDO");
            
            // Catch-all - deny all other requests
            // auth.anyRequest().denyAll();
        };
    }
}
