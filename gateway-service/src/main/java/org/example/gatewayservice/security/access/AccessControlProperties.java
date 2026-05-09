package org.example.gatewayservice.security.access;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gateway.access-control")
public class AccessControlProperties {

    private List<String> whitelistPaths = new ArrayList<>(List.of(
            "/users/login",
            "/users/register"));

    public List<String> getWhitelistPaths() {
        return whitelistPaths;
    }

    public void setWhitelistPaths(List<String> whitelistPaths) {
        this.whitelistPaths = whitelistPaths;
    }
}
