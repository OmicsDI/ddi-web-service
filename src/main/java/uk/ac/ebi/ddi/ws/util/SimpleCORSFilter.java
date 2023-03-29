package uk.ac.ebi.ddi.ws.util;

/**
 * @author Jose A. Dianes <jdianes@ebi.ac.uk>
 *
 */

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SimpleCORSFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getHeader("Access-Control-Request-Method") != null
                || ("GET".equals(request.getMethod()))
                || ("POST".equals(request.getMethod()))
                || ("OPTION".equals(request.getMethod()))
                || ("PUT".equals(request.getMethod()))) {
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE,OPTIONS");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type,X-Requested-With,accept,Origin,"
                    + "Access-Control-Request-Method,Access-Control-Request-Headers,x-auth-token");
            response.addHeader("cors.support.credentials", "true");
            response.addHeader("cors.exposed.headers", "Access-Control-Allow-Origin,"
                    + "Access-Control-Allow-Credentials");
        }
        filterChain.doFilter(request, response);
    }
}
