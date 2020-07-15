package de.helmholtz.marketplace.cerebrum.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class MarketService {
    @Id
    @GeneratedValue
    @Schema(description = "Unique identifier of the Service", example = "0", required = true)
    private Long id;
    @NotNull
    @Schema(description = "Name of a Service", example = "Sync+Share", required = true)
    private String name;
    @Schema(description = "Description of a Service", example = "A awesome Sync+Share Service provides by Helmholtz Zentrum xy")
    private String description;
    @Schema(description = "Url to a Service", example = "serviceXy.helmholtz.de")
    private String url;
    @Schema(description = "Creation date of a Service", example = "2020-02-19")
    private Date created;
    @Schema(description = "Date of last modification", example = "2020-03-24")
    private Date lastModified;
    @Schema(description = "Specifies the current lifecycle")
    private LifecycleStatus lifecycleStatus;
    @Schema(description = "Specifies the authentication which a user can use to log in to a service")
    private Authentication authentication;
    @Schema(description = "Indicates who is the provider of the service")
    @Relationship(type = "PROVIDED_BY")
    private ServiceProvider serviceProvider;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public LifecycleStatus getLifecycleStatus() {
        return lifecycleStatus;
    }

    public void setLifecycleStatus(LifecycleStatus lifecycleStatus) {
        this.lifecycleStatus = lifecycleStatus;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }
}
