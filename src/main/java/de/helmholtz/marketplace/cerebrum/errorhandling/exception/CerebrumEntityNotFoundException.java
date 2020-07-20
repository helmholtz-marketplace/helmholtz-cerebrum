package de.helmholtz.marketplace.cerebrum.errorhandling.exception;

public class CerebrumEntityNotFoundException extends RuntimeException
{
    public CerebrumEntityNotFoundException(String entityName, Long id)
    {
        super("Could not find " +  entityName + " " +  id);
    }
}
