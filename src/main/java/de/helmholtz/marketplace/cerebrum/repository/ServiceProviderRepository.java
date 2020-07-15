package de.helmholtz.marketplace.cerebrum.repository;

import de.helmholtz.marketplace.cerebrum.entities.MarketService;
import de.helmholtz.marketplace.cerebrum.entities.ServiceProvider;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ServiceProviderRepository extends PagingAndSortingRepository<ServiceProvider, Long> {

    @Query("MATCH (s:ServiceProvider)-[p:PART_OF]->(o) WHERE ID(s) = $0 DELETE p")
    void deleteRelationshipToOrganization(Long serviceProviderId);

    @Query("MATCH (m:MarketService)-[p:PROVIDED_BY]->(s:ServiceProvider) WHERE ID(s) = $0 RETURN m")
    Iterable<MarketService> getMarketServicesByServiceProviderId(Long serviceProviderId);

}
