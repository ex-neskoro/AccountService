package account.security;

import account.service.SecurityEventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

    private SecurityEventService securityEventService;

    @Autowired
    public CustomAccessDeniedHandler(SecurityEventService securityEventService) {
        this.securityEventService = securityEventService;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setStatus(HttpStatus.FORBIDDEN.value());
        Map<String, Object> data = new LinkedHashMap<>();
        data.put(
                "timestamp",
                new Date());
        data.put(
                "status",
                HttpStatus.FORBIDDEN.value());
        data.put(
                "error",
                "Forbidden");
        data.put(
                "message",
                "Access Denied!");
        data.put(
                "path",
                request.getServletPath());

        securityEventService.addAccessDeniedEvent(request);
        response.getOutputStream()
                .println(objectMapper.writeValueAsString(data));

    }
}
