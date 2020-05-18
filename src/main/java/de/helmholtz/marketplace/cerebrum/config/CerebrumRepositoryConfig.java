package de.helmholtz.marketplace.cerebrum.config;

import de.helmholtz.marketplace.cerebrum.repository.listeners.MarketUserEventHandler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CerebrumRepositoryConfig
{
    public CerebrumRepositoryConfig() {
        super();
    }

    @Bean
    MarketUserEventHandler makeUserEventHandler() {
        return new MarketUserEventHandler();
    }
}
