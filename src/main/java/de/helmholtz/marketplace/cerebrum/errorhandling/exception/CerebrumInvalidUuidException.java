package de.helmholtz.marketplace.cerebrum.errorhandling.exception;

public class CerebrumInvalidUuidException extends RuntimeException
{
    public CerebrumInvalidUuidException(String uuid)
    {
        super(uuid + " is an invalid uuid");
    }
}
