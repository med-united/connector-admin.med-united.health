package health.medunited.architecture.model;

public class RestartRequestBody {
    private String username;

    public String user;
    private String password;

    public RestartRequestBody() {
        // Required empty constructor for deserialization
    }

    public RestartRequestBody(String username, String password) {
        this.username = username;
        //for the RISE connector
        this.user = username;
        this.password = password;
    }

    // Getters and setters for the properties
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        this.user = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
