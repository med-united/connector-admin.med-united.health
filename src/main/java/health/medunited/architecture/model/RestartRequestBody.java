package health.medunited.architecture.model;

public class RestartRequestBody {
    private String username;
    private String password;

    public RestartRequestBody() {
        // Required empty constructor for deserialization
    }

    public RestartRequestBody(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and setters for the properties
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
