package uk.ac.ebi.ddi.ws.modules.security;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.ac.ebi.ddi.ws.error.exception.UnauthorizedException;
import uk.ac.ebi.ddi.ws.modules.dataset.model.Role;

import java.net.URI;

@Service
public class UserPermissionService {

    @Value("${ddi.common.profile-service.endpoint}")
    private String profileServiceEndpoint;

    private RestTemplate restTemplate = new RestTemplate();

    public void hasRole(Role requiredRole, HttpHeaders headers) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(profileServiceEndpoint)
                .path("/api/user/current");
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        URI uri = builder.build().toUri();
        ResponseEntity<JsonNode> response = restTemplate.exchange(uri, HttpMethod.GET, entity, JsonNode.class);
        JsonNode userInfo = response.getBody();
        if (userInfo.has("roles")) {
            String[] roles = userInfo.get("roles").asText().split(",");
            for (String role : roles) {
                if (role.equals(requiredRole.name())) {
                    return;
                }
            }
        }
        throw new UnauthorizedException();
    }
}
