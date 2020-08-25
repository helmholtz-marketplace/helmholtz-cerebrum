package de.helmholtz.marketplace.cerebrum.repository;

import de.helmholtz.marketplace.cerebrum.entities.MarketUser;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MarketUserRepository extends PagingAndSortingRepository<MarketUser, Long>
{
    MarketUser findBySub(@Param("sub") String sub);

    Optional<MarketUser> findByUuid(String uuid);

    @SuppressWarnings("UnusedReturnValue")
    Long deleteByUuid(String uuid);
}