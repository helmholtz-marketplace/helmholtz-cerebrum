package de.helmholtz.marketplace.cerebrum.repository.listeners;

import de.helmholtz.marketplace.cerebrum.entities.MarketUser;

import org.springframework.data.rest.core.annotation.*;

import java.util.logging.Logger;

@RepositoryEventHandler
public class MarketUserEventHandler
{
    Logger logger = Logger.getLogger("Class MarketUserEventHandler");

    public MarketUserEventHandler() {
        super();
    }

    @HandleAfterCreate
    public void handleMarketUserBeforeCreate(MarketUser user) {
        logger.info("A new MarketUser is created successfully....");
        String name = user.getFirstName();
    }
}