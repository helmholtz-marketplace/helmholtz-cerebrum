package de.helmholtz.marketplace.cerebrum.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import de.helmholtz.marketplace.cerebrum.entities.MarketService;

public interface MarketServiceRepository extends PagingAndSortingRepository<MarketService, Long> {

    @Query("MATCH (m:MarketService)-[p:PROVIDED_BY]->(s) WHERE ID(m) = $0 DELETE p")
    void deleteRelationshipToOrganization(Long organizationId);

}
