package de.helmholtz.marketplace.cerebrum.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

import de.helmholtz.marketplace.cerebrum.entities.MarketService;

public interface MarketServiceRepository extends PagingAndSortingRepository<MarketService, Long>
{
    Optional<MarketService> findByUuid(String uuid);

    @SuppressWarnings("UnusedReturnValue")
    Long deleteByUuid(String id);
}
