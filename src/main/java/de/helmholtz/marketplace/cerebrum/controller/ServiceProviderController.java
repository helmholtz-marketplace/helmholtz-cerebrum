package de.helmholtz.marketplace.cerebrum.controller;

import de.helmholtz.marketplace.cerebrum.entities.Organization;
import de.helmholtz.marketplace.cerebrum.entities.ServiceProvider;
import de.helmholtz.marketplace.cerebrum.exception.ServiceProviderNotFoundException;
import de.helmholtz.marketplace.cerebrum.repository.OrganizationRepository;
import de.helmholtz.marketplace.cerebrum.repository.ServiceProviderRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping(path = "${spring.data.rest.base-path}/serviceProviders", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "serviceProviders", description = "The API of the Service Provider")
public class ServiceProviderController {

    private final ServiceProviderRepository serviceProviderRepository;
    private final OrganizationRepository organizationRepository;

    public ServiceProviderController(ServiceProviderRepository serviceProviderRepository, OrganizationRepository organizationRepository) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.organizationRepository = organizationRepository;
    }

    /* get ServiceProviders */
    @Operation(summary = "get array list of all serviceProviders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ServiceProvider.class)))),
            @ApiResponse(responseCode = "400", description = "invalid request")
    })
    @GetMapping(path = "")
    public Iterable<ServiceProvider> getServiceProviders(
            @Parameter(description = "specify the page number")
            @RequestParam(value = "page", defaultValue = "0") @Nullable Integer page,
            @Parameter(description = "limit the number of records returned in one page")
            @RequestParam(value = "size", defaultValue = "20") @Nullable Integer size) {
        if (page != null && size != null) {
            return serviceProviderRepository.findAll(PageRequest.of(page, size));
        } else {
            return serviceProviderRepository.findAll();
        }
    }

    /* get ServiceProvider */
    @Operation(summary = "find a serviceProvider by ID", description = "Returns a detailed serviceProvider information corresponding to the ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = ServiceProvider.class))),
            @ApiResponse(responseCode = "400", description = "invalid serviceProvider ID supplied")
    })
    @GetMapping(path = "/{id}")
    public Optional<ServiceProvider> getServiceProvider(
            @Parameter(description = "ID of the serviceProvider that needs to be fetched")
            @PathVariable(required = true) Long id) {
        return serviceProviderRepository.findById(id);
    }

    /* create ServiceProvider */
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "add a new serviceProvider", security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "create operation was successful",
                    content = @Content(schema = @Schema(implementation = ServiceProvider.class))),
            @ApiResponse(responseCode = "400", description = "invalid ID supplied")
    })
    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ServiceProvider insertServiceProvider(
            @Parameter(description = "ServiceProvider object that needs to be added to the marketplace",
                    required = true, schema = @Schema(implementation = ServiceProvider.class))
            @Valid @RequestBody ServiceProvider serviceProvider) {
        Optional<Organization> organization = organizationRepository.findById(serviceProvider.getOrganization().getId());
        organization.ifPresent(serviceProvider::setOrganization);
        return serviceProviderRepository.save(serviceProvider);
    }

    /* update ServiceProvider */
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "update an existing serviceProvider", description = "Update all attributes and relations of a serviceProvider",
            security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "update operation was successful",
                    content = @Content(schema = @Schema(implementation = ServiceProvider.class))),
            @ApiResponse(responseCode = "400", description = "invalid ID supplied")
    })
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ServiceProvider updateServiceProvider(
            @Parameter(description = "ServiceProvider to update or replace. This cannot be null or empty.",
                    required = true, schema = @Schema(implementation = ServiceProvider.class))
            @Valid @RequestBody ServiceProvider serviceProvider,
            @Parameter(description = "ID of the serviceProvider that needs to be updated")
            @PathVariable(required = true) Long id) {
        Optional<Organization> organization = organizationRepository.findById(serviceProvider.getOrganization().getId());
        if (organization.isPresent()) {
            serviceProviderRepository.deleteRelationshipToOrganization(id);
            serviceProvider.setOrganization(organization.get());
        }
        serviceProvider.setId(id);
        return this.serviceProviderRepository.save(serviceProvider);
    }

    /* JSON PATCH ServiceProvider */
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "partially update an existing serviceProvider",
            security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = ServiceProvider.class))),
            @ApiResponse(responseCode = "400", description = "invalid id or json patch body"),
            @ApiResponse(responseCode = "404", description = "serviceProvider not found")
    })
    @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
    public ServiceProvider partialUpdateServiceProvider(
            @Parameter(description = "JSON Patch document structured as a JSON " +
                    "array of objects where each object contains one of the six " +
                    "JSON Patch operations: add, remove, replace, move, copy, and test",
                    schema = @Schema(implementation = JsonPatch.class),
                    required = true) @Valid @RequestBody JsonPatch patch,
            @Parameter(description = "ID of the serviceProvider that needs to be partially updated")
            @PathVariable(required = true) Long id) {
        return serviceProviderRepository.findById(id)
                .map(serviceProvider -> {
                    try {
                        ServiceProvider serviceProviderPatched = applyPatchToServiceProvider(patch, serviceProvider);
                        Optional<Organization> organization = organizationRepository.findById(serviceProviderPatched.getOrganization().getId());
                        organization.ifPresent(serviceProviderPatched::setOrganization);
                        return serviceProviderRepository.save(serviceProviderPatched);
                    } catch (JsonPatchException e) {
                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST, "invalid id or json patch body", e);
                    } catch (JsonProcessingException e) {
                        throw new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
                    }
                })
                .orElseThrow(ServiceProviderNotFoundException::new);
    }

    /* delete ServiceProvider */
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "deletes a serviceProvider",
            description = "Removes the record of the specified serviceProvider id from the database. The serviceProvider unique identification number cannot be null or empty",
            security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "404", description = "invalid serviceProvider id supplied")
    })
    @DeleteMapping(path = "/{id}")
    public void deleteServiceProvider(@PathVariable("id") Long id) {
        serviceProviderRepository.deleteById(id);
    }

    /* for ServiceProvider - PATCH */
    private ServiceProvider applyPatchToServiceProvider(
            JsonPatch patch,
            ServiceProvider targetServiceProvider) throws JsonPatchException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patched = patch.apply(objectMapper.convertValue(targetServiceProvider, JsonNode.class));
        return objectMapper.treeToValue(patched, ServiceProvider.class);
    }
}