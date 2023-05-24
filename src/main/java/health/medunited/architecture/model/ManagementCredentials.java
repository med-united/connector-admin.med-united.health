package health.medunited.architecture.model;

public class ManagementCredentials {

    public String username;

    public String password;
    public String productCode;
    public String productVendorID;
    public String HWVersion;
    public String FWVersion;

    public ManagementCredentials() {

    }

    public ManagementCredentials(String productCode, String productVendorID,  String HWVersion, String FWVersion, String username, String password){
        this.productCode = productCode;
        this.productVendorID = productVendorID;
        this.HWVersion = HWVersion;
        this.FWVersion = FWVersion;
        this.username = username;
        this.password = password;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getProductVendorID() {
        return productVendorID;
    }


    public String getHWVersion() {
        return HWVersion;
    }

    public String getFWVersion() {
        return FWVersion;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

