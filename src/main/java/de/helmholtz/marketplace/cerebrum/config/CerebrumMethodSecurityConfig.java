package de.helmholtz.marketplace.cerebrum.config;

import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CerebrumMethodSecurityConfig extends GlobalMethodSecurityConfiguration {}
