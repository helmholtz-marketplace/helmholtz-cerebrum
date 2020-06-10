package de.helmholtz.marketplace.cerebrum.repository;

import de.helmholtz.marketplace.cerebrum.entities.Image;
import de.helmholtz.marketplace.cerebrum.entities.Organization;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface OrganizationRepository extends CrudRepository<Organization, Long> {

    @Query("MATCH (o:Organization)-[:IMG]->(i:Image) RETURN o,i SKIP $0 LIMIT $1")
    Collection<Organization> getOrganizationsLimitOffset(Integer offset, Integer limit);

    @Query("MATCH (o:Organization)-[:IMG]->(i:Image) WHERE ID(o)=$0 DETACH DELETE o,i")
    void deleteOrganizationById(Long id);

}
