package health.medunited.architecture.entities;

import java.time.LocalDateTime;

public class UpdateStatus {

    public enum Status {
        PENDING, IN_PROGRESS, COMPLETED, FAILED
    }

    private LocalDateTime timestamp = LocalDateTime.now();
    private String errorMessage;
}
