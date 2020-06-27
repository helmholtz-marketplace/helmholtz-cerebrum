package de.helmholtz.marketplace.cerebrum.controller;

import de.helmholtz.marketplace.cerebrum.entities.Organization;
import de.helmholtz.marketplace.cerebrum.exception.OrganizationNotFoundException;
import de.helmholtz.marketplace.cerebrum.repository.OrganizationRepository;

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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
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

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE,
        path = "${spring.data.rest.base-path}/organizations")
@Tag(name = "organizations", description = "The Organization API")
public class OrganizationController {
    private final OrganizationRepository organizationRepository;

    public OrganizationController(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Operation(summary = "get array list of all organisations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "successful operation",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = Organization.class)))),
            @ApiResponse(responseCode = "400", description = "invalid request")
    })
    @GetMapping(path = "")
    public Iterable<Organization> getOrganizations(
            @Parameter(description = "specify the page number")
            @RequestParam(value = "page", defaultValue = "0") @Nullable Integer page,
            @Parameter(description = "limit the number of records returned in one page")
            @RequestParam(value = "size", defaultValue = "20") @Nullable Integer size) {
        if (page != null && size != null) {
            return organizationRepository.findAll(PageRequest.of(page, size));
        } else {
            return organizationRepository.findAll();
        }
    }

    @Operation(summary = "find organisation by ID",
            description = "Returns a detailed organization information " +
                    "corresponding to the ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "successful operation",
                    content = @Content(schema = @Schema(implementation = Organization.class))),
            @ApiResponse(responseCode = "400", description = "invalid organization ID supplied")
    })
    @GetMapping(path = "/{id}")
    public Optional<Organization> getOrganization(
            @Parameter(description = "ID of the organisation that needs to be fetched")
            @PathVariable(required = true) Long id) {
        return organizationRepository.findById(id);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "add a new organization",
            security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "organization created",
                    content = @Content(schema = @Schema(implementation = Organization.class))),
            @ApiResponse(responseCode = "400", description = "invalid ID supplied")
    })
    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Organization insertOrganization(
            @Parameter(description = "Organisation object that needs to be added to the marketplace",
                    required=true, schema=@Schema(implementation = Organization.class))
            @Valid @RequestBody Organization organization) {
        return organizationRepository.save(organization);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "update an existing organization",
            description = "Update part (or all) of an organisation information",
            security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "invalid ID supplied")
    })
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Organization updateOrganization(
            @Parameter(
                    description="Organization to update or replace. This cannot be null or empty.",
                    schema=@Schema(implementation = Organization.class),
                    required=true) @Valid @RequestBody Organization newOrganization,
            @Parameter(description = "ID of the organisation that needs to be updated")
            @PathVariable(required = true) Long id)
    {
        return organizationRepository.findById(id)
                .map(organisation -> {
                    organisation.setAbbreviation(newOrganization.getAbbreviation());
                    organisation.setName(newOrganization.getName());
                    organisation.setImg(newOrganization.getImg());
                    organisation.setUrl(newOrganization.getUrl());
                    return organizationRepository.save(organisation);
                })
                .orElseGet(() -> {
                    newOrganization.setId(id);
                    return organizationRepository.save(newOrganization);
                });
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "partially update an existing organisation",
            security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "invalid id or json patch body"),
            @ApiResponse(responseCode = "404", description = "organisation not found")
    })
    @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
    public Organization partialUpdateOrganisation(
            @Parameter(description="JSON Patch document structured as a JSON " +
                    "array of objects where each object contains one of the six " +
                    "JSON Patch operations: add, remove, replace, move, copy, and test",
                    schema=@Schema(implementation = JsonPatch.class),
                    required=true) @Valid @RequestBody JsonPatch patch,
            @Parameter(description = "ID of the organisation that needs to be partially updated")
            @PathVariable(required = true) Long id)
    {
        return organizationRepository.findById(id)
                .map(organisation -> {
                    try {
                        Organization organizationPatched = applyPatchToOrganization(patch, organisation);
                        return organizationRepository.save(organizationPatched);
                    } catch (JsonPatchException e) {
                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST, "invalid id or json patch body", e);
                    } catch (JsonProcessingException e) {
                        throw new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
                    }
                })
                .orElseThrow(OrganizationNotFoundException::new);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "deletes an organisation",
            description = "Removes the record of the specified " +
                    "organisation id from the database. The organisation " +
                    "unique identification number cannot be null or empty",
            security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "404", description = "invalid organisation id supplied")
    })
    @DeleteMapping(path = "/{id}")
    public void deleteOrganization(
            @Parameter(description="organisation id to delete", required=true)
            @PathVariable(name = "id", required = true) Long id) {
        organizationRepository.deleteById(id);
    }

    private Organization applyPatchToOrganization(
            JsonPatch patch,
            Organization targetOrganization) throws JsonPatchException, JsonProcessingException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patched = patch.apply(objectMapper.convertValue(targetOrganization, JsonNode.class));
        return objectMapper.treeToValue(patched, Organization.class);
    }
}
