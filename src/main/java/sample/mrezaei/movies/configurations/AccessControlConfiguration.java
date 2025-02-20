package sample.mrezaei.movies.configurations;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import sample.mrezaei.movies.services.ApiKeyService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This class is access control configuration
 * Swagger APIs excluded from authentication and are open.
 * Other APIs should include valid apiKey
 */
@Configuration
@AllArgsConstructor
public class AccessControlConfiguration extends OncePerRequestFilter {

    private ApiKeyService apiKeyService;

    private static final List<String> OPEN_URI_LIST = Arrays.asList(
            "/swagger-ui/",
            "/v3/api-docs",
            "/favicon.ico"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return OPEN_URI_LIST.stream().anyMatch(requestURI::startsWith);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        var apiKey = request.getParameterMap().entrySet().stream()
                .filter(entry -> entry.getKey().equals("api_key"))
                .map(Map.Entry::getValue)
                .findFirst();
        if (apiKey.isEmpty() || !apiKeyService.isValidApiKey(apiKey.get()[0])) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
