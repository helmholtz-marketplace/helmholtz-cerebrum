package de.helmholtz.marketplace.cerebrum.repository;

import de.helmholtz.marketplace.cerebrum.entities.Organization;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;

public interface OrganizationRepository extends PagingAndSortingRepository<Organization, Long> {

}
