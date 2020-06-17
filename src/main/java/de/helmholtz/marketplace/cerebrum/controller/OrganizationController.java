package de.helmholtz.marketplace.cerebrum.controller;

import de.helmholtz.marketplace.cerebrum.entities.Organization;
import de.helmholtz.marketplace.cerebrum.repository.OrganizationRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.util.annotation.Nullable;

import java.util.Iterator;
import java.util.Optional;

@RestController
@RequestMapping("${spring.data.rest.base-path}/organizations")
public class OrganizationController {

    private final OrganizationRepository organizationRepository;

    public OrganizationController(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @GetMapping(
            path = "",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Iterable<Organization> getOrganizations(@RequestParam @Nullable Integer page, @RequestParam @Nullable Integer size) {
        if (page != null && size != null) {
            return organizationRepository.findAll(PageRequest.of(page, size));
        } else {
            return organizationRepository.findAll();
        }
    }

    @GetMapping(
            path = "{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Optional<Organization> getOrganization(@PathVariable Long id) {
        return organizationRepository.findById(id);
    }

    @PostMapping(
            path = "",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public Organization insertOrganization(@RequestBody Organization organization) {
        return organizationRepository.save(organization);
    }

    @PutMapping(
            path = "",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public Organization updateOrganization(@RequestBody Organization organization) {
        System.out.println(organization.getId());
        return organizationRepository.save(organization);
    }

    @DeleteMapping(
            path = "{id}",
            produces = MediaType.APPLICATION_JSON_VALUE

    )
    public void deleteOrganization(@PathVariable("id") Long id) {
        organizationRepository.deleteById(id);
    }

}
