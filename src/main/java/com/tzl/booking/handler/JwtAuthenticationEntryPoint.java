package com.tzl.booking.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tzl.booking.utils.CustomApiResponse;
import com.tzl.booking.utils.ResponseConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        CustomApiResponse<String> responseBody = CustomApiResponse.<String>builder()
                .returnCode(ResponseConstants.UNAUTHORIZED_ERROR_CODE)
                .returnMessage(ResponseConstants.UNAUTHORIZED_ERROR_MESSAGE)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), responseBody);
    }
}
