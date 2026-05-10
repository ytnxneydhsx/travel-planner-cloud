package org.example.gatewayservice.security.access;

import java.util.ArrayList;
import java.util.List;
import org.example.gatewayservice.security.authentication.AuthenticationConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gateway.header-policy")
public class RequestHeaderPolicyProperties {

    private List<String> stripRequestHeaders = new ArrayList<>(List.of(
            AuthenticationConstants.CURRENT_USER_ID_HEADER));

    public List<String> getStripRequestHeaders() {
        return stripRequestHeaders;
    }

    public void setStripRequestHeaders(List<String> stripRequestHeaders) {
        this.stripRequestHeaders = stripRequestHeaders;
    }
}
