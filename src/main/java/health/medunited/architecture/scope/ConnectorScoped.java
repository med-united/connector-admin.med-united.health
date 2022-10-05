package health.medunited.architecture.scope;

import jakarta.inject.Scope;

import java.lang.annotation.*;

@Scope
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface ConnectorScoped {

}

