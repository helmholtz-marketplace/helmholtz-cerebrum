package de.helmholtz.marketplace.cerebrum.repository;

import de.helmholtz.marketplace.cerebrum.entities.MarketService;
import de.helmholtz.marketplace.cerebrum.entities.Organization;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrganizationRepository extends PagingAndSortingRepository<Organization, Long> {

    @Query("MATCH (m:MarketService)-[p:PROVIDED_BY]->(o:Organization) WHERE ID(o) = $0 RETURN m")
    Iterable<MarketService> getMarketServicesByOrganizationId(Long organizationId);

}
