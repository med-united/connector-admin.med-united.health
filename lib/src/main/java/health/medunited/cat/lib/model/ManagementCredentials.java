package health.medunited.cat.lib.model;

public class ManagementCredentials {

  public String username;
  public String password;

  public ManagementCredentials() {

  }

  public ManagementCredentials(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

}
