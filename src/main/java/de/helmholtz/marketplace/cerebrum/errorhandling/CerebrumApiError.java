package de.helmholtz.marketplace.cerebrum.errorhandling;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

@Schema(name="CerebrumApiError", description="a simple custom error message POJO")
public class CerebrumApiError
{
    @Schema(description = "HTTP status code")
    private HttpStatus status;

    @Schema(description = "error message associated with exception")
    private String message;

    @Schema(description = "list of constructed error messages")
    private List<String> errors;

    public CerebrumApiError()
    {
        super();
    }

    public CerebrumApiError(HttpStatus status, String message, List<String> errors)
    {
        super();
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public CerebrumApiError(HttpStatus status, String message, String error)
    {
        super();
        this.status = status;
        this.message = message;
        this.errors = Collections.singletonList(error);
    }

    public HttpStatus getStatus()
    {
        return status;
    }

    public void setStatus(HttpStatus status)
    {
        this.status = status;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public List<String> getErrors()
    {
        return errors;
    }

    public void setErrors(List<String> errors)
    {
        this.errors = errors;
    }
}
