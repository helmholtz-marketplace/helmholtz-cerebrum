package de.helmholtz.marketplace.cerebrum.repository;

import de.helmholtz.marketplace.cerebrum.entities.MarketUser;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource ;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface MarketUserRepository extends PagingAndSortingRepository<MarketUser, Long>
{
    MarketUser findBySub(@Param("sub") String sub);

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    MarketUser save (MarketUser user);

    Optional<MarketUser> findByUuid(String uuid);

    void deleteByUuid(String id);
}