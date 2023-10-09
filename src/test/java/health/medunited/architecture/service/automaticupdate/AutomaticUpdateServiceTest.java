package health.medunited.architecture.service.automaticupdate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;

import health.medunited.architecture.entities.KonnektorUpdate;
import health.medunited.architecture.entities.RuntimeConfig;

public class AutomaticUpdateServiceTest {
    @Test
    public void testCheckNeedsUpdate() {
        AutomaticUpdateService automaticUpdateService = new AutomaticUpdateService();
        automaticUpdateService.entityManager = mock(EntityManager.class);
        RuntimeConfig runtimeConfig = new RuntimeConfig();
        automaticUpdateService.checkNeedsUpdate(runtimeConfig);
        verify(automaticUpdateService.entityManager).persist(any(KonnektorUpdate.class));
    }
}
