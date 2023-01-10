package com.synchrony.userapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * It rejects every unauthenticated request and send error code 401.<br>
 *
 *
 */

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  /**
   * Commences an authentication scheme.
   * @param httpServletRequest that resulted in an AuthenticationException
   * @param httpServletResponse so that the user agent can begin authentication
   * @param authenticationException that caused the invocation
   */
  @Override
  public void commence(final HttpServletRequest httpServletRequest,
      final HttpServletResponse httpServletResponse,
      final AuthenticationException authenticationException)
      throws IOException {

    httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
    httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    final Map<String, Object> body = new HashMap<>();
    body.put("error", "Please authenticate.");

    final ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(httpServletResponse.getOutputStream(), body);
  }
}
