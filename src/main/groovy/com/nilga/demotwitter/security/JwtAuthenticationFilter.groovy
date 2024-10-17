package com.nilga.demotwitter.security

import com.nilga.demotwitter.service.CustomUserDetailsService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

/**
 * A custom filter for JWT authentication that extends OncePerRequestFilter to ensure
 * it executes once per request.
 */
@Component
class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider
    private final CustomUserDetailsService userDetailsService

    /**
     * Constructor for JwtAuthenticationFilter.
     *
     * @param jwtTokenProvider the JWT token provider for handling tokens.
     * @param userDetailsService the user details service for loading user data.
     */
    JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider
        this.userDetailsService = userDetailsService
    }

    /**
     * Filters incoming requests for JWT authentication.
     *
     * @param request the HTTP request.
     * @param response the HTTP response.
     * @param chain the filter chain to proceed with the next filter.
     * @throws ServletException if a servlet-specific error occurs.
     * @throws IOException if an input or output error occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String token = getJwtFromRequest(request)

        // Validate the token and set authentication context if valid.
        if (token != null && jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsernameFromJWT(token)

            // Load user details using the username from the token.
            UserDetails userDetails = userDetailsService.loadUserByUsername(username)
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            )
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request))

            // Set the authentication in the security context.
            SecurityContextHolder.getContext().setAuthentication(authentication)
        }

        chain.doFilter(request, response)
    }

    /**
     * Extracts the JWT token from the request header.
     *
     * @param request the HTTP request containing the "Authorization" header.
     * @return the JWT token or null if not present.
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization")
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        return null
    }
}
