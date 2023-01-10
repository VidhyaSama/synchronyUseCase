package com.synchrony.userapp.config;

import com.synchrony.userapp.service.implemetation.UserServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * For any incoming request this Filter class gets executed.<br>
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userService;

    @Autowired
    private JwtUtils jwtTokenUtil;

    /**
     * This method is invoked once per request.
     *
     * @param request  that contains Authorization token.
     * @param response the response to pass along the chains.
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain chain) throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");
        try {
            String username = null;
            String jwtToken = null;
            final int tokenStarts = 7;
            // JWT Token is in the form "Bearer token". Remove Bearer word and get
            // only the Token
            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                jwtToken = jwtTokenUtil.parseJwt(requestTokenHeader);
                username = jwtTokenUtil.getUserNameFromJwtToken(jwtToken);
            }

            // Once we get the token validate it.
            if (username != null && SecurityContextHolder.getContext()
                    .getAuthentication() == null) {
                UserDetails userDetails = this.userService.loadUserByUsername(username);
                // if token is valid configure Spring Security to manually set
                // authentication
                if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // After setting the Authentication in the context, we specify
                    // that the current user is authenticated. So it passes the
                    // Spring Security Configurations successfully.
                    SecurityContextHolder.getContext()
                            .setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }
        chain.doFilter(request, response);
    }

}
