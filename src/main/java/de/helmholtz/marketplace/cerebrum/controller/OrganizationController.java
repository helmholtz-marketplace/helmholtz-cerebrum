package de.helmholtz.marketplace.cerebrum.controller;

import de.helmholtz.marketplace.cerebrum.entities.Organization;
import de.helmholtz.marketplace.cerebrum.errorhandling.CerebrumApiError;
import de.helmholtz.marketplace.cerebrum.errorhandling.exception.CerebrumEntityNotFoundException;
import de.helmholtz.marketplace.cerebrum.errorhandling.exception.CerebrumInvalidUuidException;
import de.helmholtz.marketplace.cerebrum.repository.OrganizationRepository;
import de.helmholtz.marketplace.cerebrum.utils.CerebrumControllerUtilities;
import de.helmholtz.marketplace.cerebrum.utils.CerebrumEntityUuidGenerator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@Validated
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE,
        path = "${spring.data.rest.base-path}/organizations")
@Tag(name = "organizations", description = "The Organization API")
public class OrganizationController {
    private final OrganizationRepository organizationRepository;

    public OrganizationController(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    /* get Organizations */
    @Operation(summary = "get array list of all organizations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "successful operation",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = Organization.class)))),
            @ApiResponse(responseCode = "400", description = "invalid request",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = CerebrumApiError.class))))
    })
    @GetMapping(path = "")
    public Iterable<Organization> getOrganizations(
            @Parameter(description = "specify the page number")
            @RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
            @Parameter(description = "limit the number of records returned in one page")
            @RequestParam(value = "size", defaultValue = "20") @Min(1) Integer size,
            @Parameter(description = "sort the fetched data in either ascending (asc) " +
                    "or descending (desc) according to one or more of the organisation " +
                    "properties. Eg. to sort the list in ascending order base on the " +
                    "name property; the value will be set to name.asc")
            @RequestParam(value = "sort", defaultValue = "name.asc") List<String> sorts)
    {
        return organizationRepository.findAll(
                PageRequest.of(page, size, Sort.by(CerebrumControllerUtilities.getOrders(sorts))));
    }

    /* get Organization */
    @Operation(summary = "find organization by ID",
            description = "Returns a detailed organization information " +
                    "corresponding to the ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "successful operation",
                    content = @Content(schema = @Schema(implementation = Organization.class))),
            @ApiResponse(responseCode = "400", description = "invalid organization ID supplied",
                    content = @Content(schema = @Schema(implementation = CerebrumApiError.class))),
            @ApiResponse(responseCode = "404", description = "organization not found",
                    content = @Content(schema = @Schema(implementation = CerebrumApiError.class)))
    })
    @GetMapping(path = "/{uuid}")
    public Organization getOrganization(
            @Parameter(description = "ID of the organization that needs to be fetched")
            @PathVariable(name = "uuid") String uuid)
    {
        if (Boolean.TRUE.equals(CerebrumEntityUuidGenerator.isValid(uuid))) {
            return organizationRepository.findByUuid(uuid)
                    .orElseThrow(() -> new CerebrumEntityNotFoundException("organization", uuid));
        }
        else throw new CerebrumInvalidUuidException(uuid);
    }

    /* create Organization */
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "add a new organization",
            security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "organization created",
                    content = @Content(schema = @Schema(implementation = Organization.class))),
            @ApiResponse(responseCode = "400", description = "invalid ID supplied",
                    content = @Content(schema = @Schema(implementation = CerebrumApiError.class))),
            @ApiResponse(responseCode = "401", description = "unauthorised", content = @Content())
    })
    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Organization> createOrganization(
            @Parameter(description = "organization object that needs to be added to the marketplace",
                    required = true, schema = @Schema(implementation = Organization.class))
            @Valid @RequestBody Organization organization, UriComponentsBuilder uriComponentsBuilder)
    {
        Organization createdOrg = organizationRepository.save(organization);
        UriComponents uriComponents =
                uriComponentsBuilder.path("/api/v0/organizations/{id}").buildAndExpand(createdOrg.getUuid());
        URI location = uriComponents.toUri();

        return ResponseEntity
                .created(location)
                .body(createdOrg);
    }

    /* update Organization */
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "update an existing organization",
            description = "Update part (or all) of an organization information",
            security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = Organization.class))),
            @ApiResponse(responseCode = "201", description = "organization created",
                    content = @Content(schema = @Schema(implementation = Organization.class))),
            @ApiResponse(responseCode = "400", description = "invalid ID supplied",
                    content = @Content(schema = @Schema(implementation = CerebrumApiError.class))),
            @ApiResponse(responseCode = "401", description = "unauthorised", content = @Content())
    })
    @PutMapping(path = "/{uuid}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Organization> updateOrganization(
            @Parameter(description="Organization to update or replace. This cannot be null or empty.",
                    schema=@Schema(implementation = Organization.class),
                    required=true) @Valid @RequestBody Organization newOrganization,
            @Parameter(description = "Unique identifier of the organization that needs to be updated")
            @PathVariable(name = "uuid") String uuid, UriComponentsBuilder uriComponentsBuilder)
    {
        if (Boolean.TRUE.equals(CerebrumEntityUuidGenerator.isValid(uuid))) {
            AtomicBoolean isCreated = new AtomicBoolean(false);
            Organization org = organizationRepository.findByUuid(uuid)
                    .map(organization -> {
                        organization.setAbbreviation(newOrganization.getAbbreviation());
                        organization.setName(newOrganization.getName());
                        organization.setImg(newOrganization.getImg());
                        organization.setUrl(newOrganization.getUrl());
                        return organizationRepository.save(organization);
                    })
                    .orElseGet(() -> {
                        newOrganization.setUuid(uuid);
                        isCreated.set(true);
                        return organizationRepository.save(newOrganization);
                    });

            if (isCreated.get()) {
                UriComponents uriComponents =
                        uriComponentsBuilder.path("/api/v0/organizations/{id}").buildAndExpand(org.getUuid());
                URI location = uriComponents.toUri();
                return ResponseEntity.created(location).body(org);
            }
            return ResponseEntity.ok().body(org);
        }
        else throw new CerebrumInvalidUuidException(uuid);
    }

    /* JSON PATCH Organization */
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "partially update an existing organization",
            security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = Organization.class))),
            @ApiResponse(responseCode = "400", description = "invalid id or json patch body",
                    content = @Content(schema = @Schema(implementation = CerebrumApiError.class))),
            @ApiResponse(responseCode = "401", description = "unauthorised", content = @Content()),
            @ApiResponse(responseCode = "404", description = "organization not found",
                    content = @Content(schema = @Schema(implementation = CerebrumApiError.class))),
            @ApiResponse(responseCode = "500", description = "internal server error",
                    content = @Content(schema = @Schema(implementation = CerebrumApiError.class)))
    })
    @PatchMapping(path = "/{uuid}", consumes = "application/json-patch+json")
    public ResponseEntity<Organization> partialUpdateOrganization(
            @Parameter(description = "JSON Patch document structured as a JSON " +
                    "array of objects where each object contains one of the six " +
                    "JSON Patch operations: add, remove, replace, move, copy, and test",
                    schema = @Schema(implementation = JsonPatch.class),
                    required = true) @Valid @RequestBody JsonPatch patch,
            @Parameter(description = "ID of the organization that needs to be partially updated")
            @PathVariable(name = "uuid") String uuid)
    {
        if (Boolean.TRUE.equals(CerebrumEntityUuidGenerator.isValid(uuid))) {
            Organization partialUpdateOrganisation = organizationRepository.findByUuid(uuid)
                    .map(organization -> {
                        try {
                            Organization organizationPatched =
                                    CerebrumControllerUtilities.applyPatch(patch, organization, Organization.class);
                            return organizationRepository.save(organizationPatched);
                        } catch (JsonPatchException e) {
                            throw new ResponseStatusException(
                                    HttpStatus.BAD_REQUEST, "json patch body", e);
                        } catch (JsonProcessingException e) {
                            throw new ResponseStatusException(
                                    HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
                        }
                    })
                    .orElseThrow(() -> new CerebrumEntityNotFoundException("organization", uuid));
            return ResponseEntity.ok().body(partialUpdateOrganisation);
        }
        else throw new CerebrumInvalidUuidException(uuid);
    }

    /* delete Organization */
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "deletes an organization",
            description = "Removes the record of the specified " +
                    "organization id from the database. The organization " +
                    "unique identification number cannot be null or empty",
            security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "successful operation", content = @Content()),
            @ApiResponse(responseCode = "401", description = "unauthorised", content = @Content())
    })
    @DeleteMapping(path = "/{uuid}")
    public ResponseEntity<Organization> deleteOrganization(
            @Parameter(description="organization id to delete", required=true)
            @PathVariable(name = "uuid") String uuid)
    {
        organizationRepository.deleteByUuid(uuid);
        return ResponseEntity.noContent().build();
    }
}
