package de.helmholtz.marketplace.cerebrum.controller;

import de.helmholtz.marketplace.cerebrum.entities.Organization;
import de.helmholtz.marketplace.cerebrum.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("${spring.data.rest.base-path}")
public class OrganizationController {

    private final OrganizationRepository organizationRepository;

    @Autowired
    public OrganizationController(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/organization",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Iterable<Organization> getOrganizations() {
        return organizationRepository.findAll();
    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/organization/ol",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Iterable<Organization> getOrganizationsLimitOffset(@RequestParam Integer offset, @RequestParam Integer limit) {
        return organizationRepository.getOrganizationsLimitOffset(offset, limit);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/organization/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Optional<Organization> getOrganization(@PathVariable Long id) {
        return organizationRepository.findById(id);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            path = "/organization",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public Organization insertOrganization(@RequestBody Organization organization) {
        return organizationRepository.save(organization);
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/organization",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )

    public Organization updateOrganization(@RequestBody Organization organization) {
        return organizationRepository.save(organization);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            path = "/organization/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE

    )
    public void deleteOrganization(@PathVariable("id") Long id) {
        organizationRepository.deleteOrganizationById(id);
    }

}
