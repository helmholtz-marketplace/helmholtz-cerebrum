package de.helmholtz.marketplace.cerebrum.errorhandling.exception;

public class CerebrumEntityNotFoundException extends RuntimeException
{
    public CerebrumEntityNotFoundException(String entityName, String id)
    {
        super("Could not find " +  entityName + " " +  id);
    }
}
