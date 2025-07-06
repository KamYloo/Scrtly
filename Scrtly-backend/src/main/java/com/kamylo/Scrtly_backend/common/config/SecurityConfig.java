package com.kamylo.Scrtly_backend.common.config;

import com.kamylo.Scrtly_backend.auth.service.impl.CustomOAuth2UserServiceImpl;
import com.kamylo.Scrtly_backend.auth.service.impl.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {

    private final JwtFilter jwtFilter;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final CustomOAuth2UserServiceImpl customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Value("${application.file.image-dir}")
    private String uploadDir;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests( auth -> {
                            auth.requestMatchers(
                                    "/public/**",
                                    "/uploads/**",
                                    "/v2/api-docs",
                                    "/v3/api-docs",
                                    "/v3/api-docs/**",
                                    "/swagger-resources",
                                    "/swagger-resources/**",
                                    "/configuration/ui",
                                    "/configuration/security",
                                    "/swagger-ui/**",
                                    "/webjars/**",
                                    "/swagger-ui.html",

                                    "/auth/**",
                                    "/oauth2/authorize/**",
                                    "/oauth2/redirect/**",
                                    "/api/oauth2/**",
                                    "/admin/artist/verify/**"
                            ).permitAll();

                            auth.requestMatchers(
                                    "/admin/**"
                            ).hasAuthority("ADMIN");

                            auth.requestMatchers(HttpMethod.GET,"/artist/**").permitAll();
                            auth.requestMatchers("/artist/update").hasAnyAuthority("ARTIST", "ADMIN");

                            auth.requestMatchers(HttpMethod.GET,"/album/**").permitAll();
                            auth.requestMatchers("/album/create", "/album/delete/**").hasAnyAuthority("ARTIST", "ADMIN");

                            auth.requestMatchers(HttpMethod.GET,"/song/**").permitAll();
                            auth.requestMatchers("/song/upload", "/song/delete/**").hasAnyAuthority("ARTIST", "ADMIN");

                            auth.requestMatchers(HttpMethod.GET,"/posts/**").permitAll();
                            auth.requestMatchers(HttpMethod.GET,"/comments/**").permitAll();
                            auth.requestMatchers(HttpMethod.GET,"/user/profile/**").permitAll();
                            auth.requestMatchers(HttpMethod.GET,"recommendations/**").permitAll();
                            auth.requestMatchers(HttpMethod.POST,"song/{id}/play").permitAll();

                            auth.anyRequest().authenticated();
                        }
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint( authz -> authz.baseUri("/oauth2/authorize") )
                        .redirectionEndpoint( redir -> redir.baseUri("/oauth2/redirect") )
                        .userInfoEndpoint(ui -> ui.oidcUserService(customOAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration cfg = new CorsConfiguration();
            cfg.setAllowedOrigins(Arrays.asList(
                    "http://localhost:3000",
                    "http://localhost:5173",
                    "http://localhost:5174"
            ));
            cfg.setAllowedMethods(Collections.singletonList("*"));
            cfg.setAllowCredentials(true);
            cfg.setAllowedHeaders(Collections.singletonList("*"));
            cfg.setExposedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "X-XSRF-TOKEN"));
            cfg.setMaxAge(3600L);
            return cfg;
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder);
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:"+uploadDir);
    }
}

