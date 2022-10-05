package health.medunited.architecture.scope;

import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ConnectorScopeContextHolder implements Serializable {

    private static ConnectorScopeContextHolder INSTANCE;
    private Map<Class, ConnectorScopeInstance> beans;//we will have only one instance of a type so the key is a class

    private ConnectorScopeContextHolder() {
        beans = Collections.synchronizedMap(new HashMap<Class, ConnectorScopeInstance>());
    }

    public synchronized static ConnectorScopeContextHolder getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConnectorScopeContextHolder();
        }
        return INSTANCE;
    }

    public Map<Class, ConnectorScopeInstance> getBeans() {
        return beans;
    }

    public ConnectorScopeInstance getBean(Class type) {
        return getBeans().get(type);
    }

    public void putBean(ConnectorScopeInstance customInstance) {
        getBeans().put(customInstance.bean.getBeanClass(), customInstance);
    }

    void destroyBean(ConnectorScopeInstance customScopeInstance) {
        getBeans().remove(customScopeInstance.bean.getBeanClass());
        customScopeInstance.bean.destroy(customScopeInstance.instance, customScopeInstance.ctx);
    }

    public static class ConnectorScopeInstance<T> {

        Bean<T> bean;
        CreationalContext<T> ctx;
        T instance;
    }
}
