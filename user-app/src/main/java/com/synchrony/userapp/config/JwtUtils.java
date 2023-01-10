package com.synchrony.userapp.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

@Component
@Slf4j
public class JwtUtils {

    @Value("${app.jwtSecret}")
    private transient String jwtSecret;

    @Value("${app.jwtExpirationMs}")
    private transient int jwtExpirationMs;

    public static final int SKIP = 7;


    public String generateJwtToken(UserDetails userDetails) {
    	UserDetails userPrincipal =  userDetails;
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Description: Check token expiration.
     * @param token
     * @return
     */
     private Boolean isTokenExpired(final String token) {
        final Date expiration = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getExpiration();
        return expiration.before(new Date());
    }

    /** Validate token . */
    public Boolean validateToken(final String token,
            final UserDetails userDetails) {
        final String username = getUserNameFromJwtToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /** Parse token . */
    public String parseJwt(final String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(SKIP, token.length());
        }

        return null;
    }

    /**
     * Description: Fetch loggedIn userName from security Context.
     * @return userName
     */
    public String getLoggedInUserName() {
        log.info("Fetch loggedIn UserName from Security Context");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return  auth.getName();
    }
}
