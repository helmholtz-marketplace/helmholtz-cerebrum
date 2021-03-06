package de.helmholtz.marketplace.cerebrum.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import de.helmholtz.marketplace.cerebrum.utils.CerebrumEntityUuidGenerator;

import static de.helmholtz.marketplace.cerebrum.utils.CerebrumEntityUuidGenerator.generate;

@NodeEntity
public class MarketUser
{
    @Schema(description = "Unique identifier of the marketplace user.",
            example = "hmu-01eac6d7-0d35-1812-a3ed-24aec4231940", required = true)
    @Id @GeneratedValue(strategy = CerebrumEntityUuidGenerator.class)
    private String uuid;

    @Schema(description = "first name of the user.",
            example = "Paul", required = true)
    @NotBlank
    @Size(max = 100)
    private String firstName;

    @Schema(description = "last name or surname of the user.",
            example = "Millar", required = true)
    @NotBlank
    @Size(max = 100)
    private String lastName;

    @Schema(description = "User chosen name to represent him or herself", example = "pm")
    @Size(max = 20)
    private String screenName;

    @Schema(description = "Email address of the user.",
            example = "paul.millar@hifis.net", required = true)
    @Email(message = "Email Address")
    @NotBlank
    @Size(max = 100)
    private String email;

    @Schema(description = "Helmholtz AAI generated unique user identifier",
            example = "110248495921238986420", required = true)
    @NotBlank
    private String sub;

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = Boolean.TRUE.equals(CerebrumEntityUuidGenerator.isValid(uuid)) ?
                uuid : generate("usr");
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }
}
