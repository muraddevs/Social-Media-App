package CIC.social_media_api.jwt;


import CIC.social_media_api.service.CustomUserDetails;
import CIC.social_media_api.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        final String authorizationHeader = request.getHeader("Authorization");

        String userName = null;
        String jwt = null;
        Long customerId = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                userName = jwtTokenProvider.getUsernameFromToken(jwt);
                customerId = jwtTokenProvider.getUserIdFromToken(jwt);
                System.out.println("Username extracted from token: " + userName);
                System.out.println("CustomerId extracted from token: " + customerId);
            } catch (Exception e) {
                logger.error("Could not extract username or customerId from token", e);
            }
        }

        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(userName);

            if (jwtTokenProvider.validateToken(jwt, userDetails)) {
                List<String> roles = userDetails.getAuthorities().stream()
                        .map(Object::toString)
                        .collect(Collectors.toList());
                System.out.println("Roles extracted from token: " + roles);

                CustomUserDetails customUserDetails = new CustomUserDetails(
                        userDetails.getUsername(),
                        userDetails.getPassword(),
                        customerId,
                        userDetails.getAuthorities()
                );

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                System.out.println("User authenticated: " + userName);

                // Custom logic to check authorization for specific endpoints
                if (request.getRequestURI().startsWith("/orders/customer/")) {
                    // Extract customer ID from URI
                    String[] uriParts = request.getRequestURI().split("/");
                    Long pathCustomerId = Long.parseLong(uriParts[uriParts.length - 1]);

                    // Check if the authenticated user matches the requested customer ID or has admin role
                    if (customerId.equals(pathCustomerId) || roles.contains("ROLE_ADMIN")) {
                        // User has required role or access to this endpoint
                        System.out.println("User authorized to access /orders/customer/" + pathCustomerId);
                    } else {
                        // User does not have required role or access
                        System.out.println("User not authorized to access /orders/customer/" + pathCustomerId);
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                        return;
                    }
                }
            } else {
                System.out.println("Token validation failed");
            }
        } else {
            System.out.println("Username is null or user is already authenticated");
        }

        filterChain.doFilter(request, response);
    }
}
