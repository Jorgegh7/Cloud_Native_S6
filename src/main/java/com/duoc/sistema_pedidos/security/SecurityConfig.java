package com.duoc.sistema_pedidos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/s3/test").permitAll()
                        .requestMatchers("/guias/*/descargar").hasAnyAuthority("ROLE_Lector", "ROLE_Admin")
                        .anyRequest().hasAnyAuthority("ROLE_Admin", "ROLE_APP")
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        String b2cJwkSetUri = "https://mscloudnativeduoc.b2clogin.com/mscloudnativeduoc.onmicrosoft.com/discovery/v2.0/keys?p=b2c_1_susi";
        String microsoftJwkSetUri = "https://login.microsoftonline.com/af524a33-531a-4faf-ab02-396a981b9499/discovery/v2.0/keys";

        NimbusJwtDecoder b2cDecoder = NimbusJwtDecoder.withJwkSetUri(b2cJwkSetUri).build();
        NimbusJwtDecoder microsoftDecoder = NimbusJwtDecoder.withJwkSetUri(microsoftJwkSetUri).build();

        return token -> {
            try {
                return b2cDecoder.decode(token);
            } catch (Exception e) {
                return microsoftDecoder.decode(token);
            }
        };
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<SimpleGrantedAuthority> authorities = new java.util.ArrayList<>();
            String rol = jwt.getClaimAsString("extension_consultaRol");
            if (rol != null) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + rol));
            } else {
                authorities.add(new SimpleGrantedAuthority("ROLE_APP"));
            }
            return new java.util.ArrayList<>(authorities);
        });
        return authenticationConverter;
    }
}