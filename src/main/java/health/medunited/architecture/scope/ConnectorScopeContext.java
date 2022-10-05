package health.medunited.architecture.scope;

import health.medunited.architecture.event.KillEvent;
import jakarta.enterprise.context.spi.Context;
import health.medunited.architecture.scope.ConnectorScopeContextHolder.ConnectorScopeInstance;

import jakarta.enterprise.context.spi.Contextual;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.Bean;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.logging.Logger;

public class ConnectorScopeContext implements Context, Serializable {

    private Logger log = Logger.getLogger(getClass().getSimpleName());

    private ConnectorScopeContextHolder customScopeContextHolder;

    public ConnectorScopeContext() {
        log.info("Init");
        this.customScopeContextHolder = ConnectorScopeContextHolder.getInstance();
    }

    @Override
    public <T> T get(final Contextual<T> contextual) {
        Bean bean = (Bean) contextual;
        if (customScopeContextHolder.getBeans().containsKey(bean.getBeanClass())) {
            return (T) customScopeContextHolder.getBean(bean.getBeanClass()).instance;
        } else {
            return null;
        }
    }

    @Override
    public <T> T get(final Contextual<T> contextual, final CreationalContext<T> creationalContext) {
        Bean bean = (Bean) contextual;
        if (customScopeContextHolder.getBeans().containsKey(bean.getBeanClass())) {
            return (T) customScopeContextHolder.getBean(bean.getBeanClass()).instance;
        } else {
            T t = (T) bean.create(creationalContext);
           ConnectorScopeInstance customInstance = new ConnectorScopeInstance();
            customInstance.bean = bean;
            customInstance.ctx = creationalContext;
            customInstance.instance = t;
            customScopeContextHolder.putBean(customInstance);
            return t;
        }
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return ConnectorScoped.class;
    }

    public boolean isActive() {
        return true;
    }

    public void passivate(@Observes KillEvent killEvent) {
        if (customScopeContextHolder.getBeans().containsKey(killEvent.getBeanType())) {
            customScopeContextHolder.destroyBean(customScopeContextHolder.getBean(killEvent.getBeanType()));
        }
    }

}
