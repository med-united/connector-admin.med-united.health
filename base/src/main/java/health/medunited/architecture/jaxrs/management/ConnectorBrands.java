package health.medunited.architecture.jaxrs.management;

public enum ConnectorBrands {
  SECUNET("secunet"),
  KOCOBOX("kocobox"),
  RISE("rise");

  private final String value;

  ConnectorBrands(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
