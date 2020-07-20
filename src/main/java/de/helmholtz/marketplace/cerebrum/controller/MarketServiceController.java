package de.helmholtz.marketplace.cerebrum.controller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Optional;

import de.helmholtz.marketplace.cerebrum.entities.MarketService;
import de.helmholtz.marketplace.cerebrum.errorhandling.exception.CerebrumEntityNotFoundException;
import de.helmholtz.marketplace.cerebrum.repository.MarketServiceRepository;

@RestController
@RequestMapping(path = "${spring.data.rest.base-path}/services", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "services", description = "The API of the Service")
public class MarketServiceController {

    private final MarketServiceRepository marketServiceRepository;

    public MarketServiceController(MarketServiceRepository marketServiceRepository) {
        this.marketServiceRepository = marketServiceRepository;
    }

    /* get Services */
    @Operation(summary = "get array list of all services")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MarketService.class)))),
            @ApiResponse(responseCode = "400", description = "invalid request")
    })
    @GetMapping(path = "")
    public Iterable<MarketService> getMarketServices(
            @Parameter(description = "specify the page number")
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @Parameter(description = "limit the number of records returned in one page")
            @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return marketServiceRepository.findAll(PageRequest.of(page, size));
    }

    /* get single Service */
    @Operation(summary = "find a service by ID",
            description = "Returns detailed service information corresponding to the ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = MarketService.class))),
            @ApiResponse(responseCode = "400", description = "invalid service ID supplied")
    })
    @GetMapping(path = "/{uuid}")
    public MarketService getMarketService(
            @Parameter(description = "ID of the service that needs to be fetched")
            @PathVariable() String uuid) {
        return marketServiceRepository
                .findByUuid(uuid)
                .orElseThrow(() -> new CerebrumEntityNotFoundException("marketService", uuid));
    }

    /* create Service */
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "add a new service", security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "create operation was successful",
                    content = @Content(schema = @Schema(implementation = MarketService.class))),
            @ApiResponse(responseCode = "400", description = "invalid ID supplied")
    })
    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public MarketService createMarketService(
            @Parameter(description = "Service object that needs to be added to the marketplace",
                    required = true, schema = @Schema(implementation = MarketService.class))
            @Valid @RequestBody MarketService marketService) {
        return marketServiceRepository.save(marketService);
    }

    /* update Service */
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "update an existing service",
            description = "Update all attributes and relations of a service",
            security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "update operation was successful",
                    content = @Content(schema = @Schema(implementation = MarketService.class))),
            @ApiResponse(responseCode = "400", description = "invalid ID supplied")
    })
    @PutMapping(path = "/{uuid}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public MarketService updateMarketService(
            @Parameter(description = "Service to update or replace. This cannot be null or empty.",
                    required = true, schema = @Schema(implementation = MarketService.class))
            @Valid @RequestBody MarketService marketService,
            @Parameter(description = "ID of the service that needs to be updated")
            @PathVariable() String uuid)
    {
        marketService.setUuid(uuid);
        return this.marketServiceRepository.save(marketService);
    }

    /* JSON PATCH Service */
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "partially update an existing service",
            security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = MarketService.class))),
            @ApiResponse(responseCode = "400", description = "invalid id or json patch body"),
            @ApiResponse(responseCode = "404", description = "service not found")
    })
    @PatchMapping(path = "/{uuid}", consumes = "application/json-patch+json")
    public MarketService partialUpdateMarketService(
            @Parameter(description = "JSON Patch document structured as a JSON " +
                    "array of objects where each object contains one of the six " +
                    "JSON Patch operations: add, remove, replace, move, copy, and test",
                    schema = @Schema(implementation = JsonPatch.class),
                    required = true) @Valid @RequestBody JsonPatch patch,
            @Parameter(description = "ID of the service that needs to be partially updated")
            @PathVariable() String uuid)
    {
        return marketServiceRepository.findByUuid(uuid)
                .map(marketService -> {
                    try {
                        MarketService marketServicePatched = applyPatchToMarketService(patch, marketService);
                        return marketServiceRepository.save(marketServicePatched);
                    } catch (JsonPatchException e) {
                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST, "invalid id or json patch body", e);
                    } catch (JsonProcessingException e) {
                        throw new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
                    }
                })
                .orElseThrow(() -> new CerebrumEntityNotFoundException("marketService", uuid));
    }

    /* delete Service */
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "deletes a service",
            description = "Removes the record of the specified service id " +
                    "from the database. The service unique identification " +
                    "number cannot be null or empty",
            security = @SecurityRequirement(name = "hdf-aai"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "404", description = "invalid service id supplied")
    })
    @DeleteMapping(path = "/{uuid}")
    public ResponseEntity<MarketService> deleteMarketService(@PathVariable("uuid") String uuid)
    {
        marketServiceRepository.deleteByUuid(uuid);
        return ResponseEntity.noContent().build();
    }

    /* for Service - PATCH */
    private MarketService applyPatchToMarketService(
            JsonPatch patch,
            MarketService targetMarketService) throws JsonPatchException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patched = patch.apply(objectMapper.convertValue(targetMarketService, JsonNode.class));
        return objectMapper.treeToValue(patched, MarketService.class);
    }

}
