package de.helmholtz.marketplace.cerebrum.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.helmholtz.marketplace.cerebrum.repository.MarketUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

@RestController
@RequestMapping("${spring.data.rest.base-path}")
public class MarketUserController
{
    private final  WebClient authorisationServer;
    private final MarketUserRepository marketUserRepository;

    @Autowired
    public MarketUserController(WebClient authorisationServer, MarketUserRepository marketUserRepository) {
        this.authorisationServer = authorisationServer;
        this.marketUserRepository = marketUserRepository;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(path = "/whoami")
    public JsonNode whoami() {
        return this.authorisationServer
                .get()
                .uri("https://login.helmholtz-data-federation.de/oauth2/userinfo")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    public boolean isSomebody(JwtAuthenticationToken token) {
        if (Objects.isNull(token)) return false;
        String name = marketUserRepository.findBySub((String) token.getTokenAttributes().get("sub")).getFirstName();
        if (Objects.isNull(name)) return false;
        return name.trim().isEmpty();
    }
}