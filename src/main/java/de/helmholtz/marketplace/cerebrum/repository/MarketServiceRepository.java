package de.helmholtz.marketplace.cerebrum.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

import de.helmholtz.marketplace.cerebrum.entities.MarketService;

public interface MarketServiceRepository extends PagingAndSortingRepository<MarketService, Long> {

    @Query("MATCH (m:MarketService)-[p:PROVIDED_BY]->(s) WHERE ID(m) = $0 DELETE p")
    void deleteRelationshipToServiceProvider(Long serviceProviderId);

    /*
    @Query("MATCH (m:MarketService)-[p1:PROVIDED_BY]->(s)-[p2:PART_OF]->(o) WHERE ID(m) = $0 RETURN m,s,o")
    Optional<MarketService> findById(Long marketServiceId);
    */
}
