package de.helmholtz.marketplace.cerebrum.annotation;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * https://stackoverflow.com/questions/60727265/use-authenticationprincipal-with-jwtauthenticationtoken-to-use-own-user-class
 */
@Target({ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal(expression = "@marketuserRepository.findBySub(#this)")
public @interface CurrentUser {}