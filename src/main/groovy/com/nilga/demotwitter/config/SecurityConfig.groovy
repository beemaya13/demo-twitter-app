package com.nilga.demotwitter.config

import com.nilga.demotwitter.security.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.http.HttpStatus
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/**
 * Configuration class for Spring Security.
 * Handles JWT-based authentication and sets up security rules for the application.
 */
@Configuration
@EnableWebSecurity
class SecurityConfig {

    private final UserDetailsService userDetailsService
    private final JwtAuthenticationFilter jwtAuthenticationFilter

    /**
     * Constructor for SecurityConfig.
     *
     * @param userDetailsService the user details service for loading user-specific data.
     * @param jwtAuthenticationFilter the filter for processing JWT tokens.
     */
    SecurityConfig(UserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService
        this.jwtAuthenticationFilter = jwtAuthenticationFilter
    }

    /**
     * Configures the security filter chain.
     *
     * @param http the HttpSecurity instance to customize.
     * @return the configured SecurityFilterChain.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/users/register",
                                "/api/users/login",
                                "/swagger-ui/**",  // Access to Swagger UI
                                "/swagger-ui.html",  // Access to Swagger UI HTML page
                                "/v3/api-docs/**"  // Access to API documentation
                        ).permitAll()  // Allow access without authentication
                        .anyRequest().authenticated()  // Require authentication for other requests
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)  // Add JWT filter
                .exceptionHandling(exceptions -> {
                    exceptions.authenticationEntryPoint((request, response, authException) -> {
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\": \"Unauthorized\"}");
                    })
                })

        return http.build()
    }

    /**
     * Provides the password encoder bean.
     *
     * @return a BCryptPasswordEncoder for hashing passwords.
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder()
    }

    /**
     * Provides the authentication manager bean.
     *
     * @param authenticationConfiguration the configuration for authentication.
     * @return the authentication manager.
     * @throws Exception if an error occurs while retrieving the manager.
     */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Provides the handler for successful login attempts.
     *
     * @return an AuthenticationSuccessHandler that returns a success message in JSON format.
     */
    @Bean
    AuthenticationSuccessHandler loginSuccessHandler() {
        return (request, response, authentication) -> {
            response.setContentType("application/json;charset=UTF-8")
            response.setStatus(200)
            response.getWriter().write("{\"message\": \"Login successful\"}")
        }
    }

    /**
     * Provides the handler for failed login attempts.
     *
     * @return an AuthenticationFailureHandler that returns an error message in JSON format.
     */
    @Bean
    AuthenticationFailureHandler loginFailureHandler() {
        return (request, response, exception) -> {
            response.setContentType("application/json;charset=UTF-8")
            response.setStatus(401)
            response.getWriter().write("{\"error\": \"Login failed\"}")
        }
    }

    /**
     * Provides the handler for successful logout attempts.
     *
     * @return a LogoutSuccessHandler that returns a success message in JSON format.
     */
    @Bean
    LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
            response.setContentType("application/json;charset=UTF-8")
            response.setStatus(200)
            response.getWriter().write("{\"message\": \"Logout successful\"}")
        }
    }
}
