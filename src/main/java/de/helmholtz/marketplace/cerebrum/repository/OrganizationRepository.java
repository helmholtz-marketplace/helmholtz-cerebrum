package de.helmholtz.marketplace.cerebrum.repository;

import de.helmholtz.marketplace.cerebrum.entities.Organization;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrganizationRepository extends PagingAndSortingRepository<Organization, Long> {

}
