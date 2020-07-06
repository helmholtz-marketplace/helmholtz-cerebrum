package de.helmholtz.marketplace.cerebrum.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotNull;
import java.util.List;

@NodeEntity
public class ServiceProvider {

    @Schema(description = "Unique identifier of the ServiceProvider",
            example = "0", required = true)
    @Id
    @GeneratedValue
    private Long id;
    @Schema(description = "Relation to the Organization", required = true)
    @NotNull
    @Relationship(type = "PART_OF")
    private Organization organization;
    @Schema(description = "A list of Services which are provided by the ServiceProvider")
    @Relationship(type = "PROVIDES")
    private List<MarketService> serviceList;
    @Schema(description = "A list with users, to have a contact in case of trouble")
    @Relationship(type = "HAS")
    private List<MarketUser> contactPersons;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public List<MarketService> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<MarketService> serviceList) {
        this.serviceList = serviceList;
    }

    public List<MarketUser> getContactPersons() {
        return contactPersons;
    }

    public void setContactPersons(List<MarketUser> contactPersons) {
        this.contactPersons = contactPersons;
    }
}
