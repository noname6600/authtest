package com.learn.test.configuration;

import com.learn.test.service.impl.CustomUserDetailsService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {


    private final CustomUserDetailsService customUserDetailsService;
    protected final CorsProperties securityProperties;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/configuration/ui",
                                "/configuration/security",
                                "/api/v1/auth/**",
                                "/api/v1/register"
                        ).permitAll()
                        .anyRequest().authenticated()

                )
                .httpBasic(Customizer.withDefaults());
                this.cors(http);

        return http.build();
    }



    protected void cors(HttpSecurity http) throws Exception {
        CorsConfiguration configuration = buildCorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        http.cors(cors -> cors.configurationSource(source));
    }

    protected CorsConfiguration buildCorsConfiguration() {
        CorsProperties.Cors cors = this.securityProperties.getCors();
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        System.out.println(cors.toString());
        if (Objects.nonNull(cors)) {
            if (Objects.nonNull(cors.getAllowedOrigins())) {
                List<String> origins = new ArrayList<>();
                for (String allowedOrigin : cors.getAllowedOrigins()) {
                    origins.addAll(Arrays.asList(allowedOrigin.split("\\s*,\\s*")));
                }
                configuration.setAllowedOrigins(origins);
            }

            if (Objects.nonNull(cors.getAllowedMethods())) {
                configuration.setAllowedMethods(cors.getAllowedMethods());
            }

            if (Objects.nonNull(cors.getAllowedHeaders())) {
                configuration.setAllowedHeaders(cors.getAllowedHeaders());
            }
        }
        return configuration;
    }
}

