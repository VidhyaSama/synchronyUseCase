package com.synchrony.userapp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * This class allows customization to both WebSecurity and HttpSecurity.<br>
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfiguration{

  @Autowired
  JwtRequestFilter authJWTTokenfilter;

  @Autowired
  JwtAuthenticationEntryPoint authJWTEntryPoint;

  @Autowired
  UserDetailsService jwtUserDetailService;

  /**
   * Description: Filter is to intercept all the incoming requests
   * @param http
   * @return filterChain.
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    try {
      http.csrf()
              .disable()
              .authorizeRequests()
              .antMatchers("/register", "/login", "/h2-console/**", "/swagger-ui/**","/swagger-ui.html","/user-openapi/**")
              .permitAll()
              .antMatchers("/uploadImage","/imageData","/image/**")
              .authenticated()
              .and()
              .exceptionHandling()
              .authenticationEntryPoint(authJWTEntryPoint)
              .and()
              .addFilterBefore(authJWTTokenfilter, UsernamePasswordAuthenticationFilter.class);
      http.headers().frameOptions().disable();
      return http.build();
    } catch (Exception exp) {
      if (log.isErrorEnabled()) {
        log.error("Error while configuring http ", exp);
      }
    }
    return null;
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Decsprition : Authenticate User.
   */
  @Bean
  public AuthenticationManager authenticationManagerBean(HttpSecurity http) {
    try {
      AuthenticationManagerBuilder authenticationManagerBuilder = http
              .getSharedObject(AuthenticationManagerBuilder.class);
      authenticationManagerBuilder.userDetailsService(jwtUserDetailService)
              .passwordEncoder(bCryptPasswordEncoder());
      return authenticationManagerBuilder.build();
    } catch (Exception exp) {
      if (log.isErrorEnabled()) {
        log.error("Error while configuring http ", exp);
      }
    }
    return null;
  }


}
