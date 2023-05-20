package health.medunited.architecture.model;

public class RestartRequestBodyRISE {
    private String user;
    private String password;

    public RestartRequestBodyRISE() {
        // Required empty constructor for deserialization
    }

    public RestartRequestBodyRISE(String user, String password) {
        this.user = user;
        this.password = password;
    }

    // Getters and setters for the properties
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
