package health.medunited.architecture.z;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.resource.spi.ConfigProperty;
import java.util.Objects;
import java.util.logging.Logger;

@ApplicationScoped
public class UserConfig {

    private final static Logger log = Logger.getLogger(UserConfig.class.getName());

    @Inject
    UserConfigurationService configurationManagementService;

    @ConfigProperty()
    String defaultConnectorBaseURI;

    @ConfigProperty()
    String defaultMandantId;

    @ConfigProperty()
    String defaultWorkplaceId;

    @ConfigProperty()
    String defaultClientSystemId;

    @ConfigProperty()
    String defaultUserId;

    @ConfigProperty()
    String defaultTvMode;

    @ConfigProperty()
    String defaultConnectorVersion;

    @ConfigProperty()
    String defaultPruefnummer;

    String defaultMuster16TemplateProfile = "DENS";

    private UserConfigurations configurations;

    @PostConstruct
    void init() {
        updateProperties();
    }

    public UserConfig() {
    }

    public UserConfigurations getConfigurations() {
        return configurations == null ? new UserConfigurations() : configurations;
    }

    public String getErixaHotfolder() {
        return getConfigurations().getErixaHotfolder();
    }

    public String getErixaReceiverEmail() {
        return getConfigurations().getErixaDrugstoreEmail();
    }

    public String getErixaUserEmail() {
        return getConfigurations().getErixaUserEmail();
    }

    public String getErixaUserPassword() {
        return getConfigurations().getErixaUserPassword();
    }

    public String getConnectorBaseURL() {
        return getConfigOrDefault(getConfigurations().getConnectorBaseURL(), defaultConnectorBaseURI);
    }

    public String getMandantId() {
        return getConfigOrDefault(getConfigurations().getMandantId(), defaultMandantId);
    }

    public String getWorkplaceId() {
        return getConfigOrDefault(getConfigurations().getWorkplaceId(), defaultWorkplaceId);
    }

    public String getClientSystemId() {
        return getConfigOrDefault(getConfigurations().getClientSystemId(), defaultClientSystemId);
    }

    public String getUserId() {
        return getConfigOrDefault(getConfigurations().getUserId(), defaultUserId);
    }

    public String getTvMode() {
        return getConfigOrDefault(getConfigurations().getTvMode(), defaultTvMode);
    }

    public String getConnectorVersion() {
        return getConfigOrDefault(getConfigurations().getVersion(), defaultConnectorVersion);
    }

    public String getPruefnummer() {
        return getConfigOrDefault(getConfigurations().getPruefnummer(), defaultPruefnummer);
    }

    public String getErixaApiKey() {
        return getConfigurations().getErixaApiKey();
    }

    public String getMuster16TemplateConfiguration() {
        return getConfigOrDefault(getConfigurations().getMuster16TemplateProfile(), defaultMuster16TemplateProfile);
    }

    public void updateProperties(UserConfigurations configurations) {
        this.configurations = configurations;
    }

    private void updateProperties() {
        updateProperties(configurationManagementService.getConfig());
    }

    private String getConfigOrDefault(String value, String defaultValue) {
        if (value != null)
            return value;
        else if (defaultValue != null)
            return defaultValue;
        else {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof UserConfig)) {
            return false;
        }
        UserConfig userConfig = (UserConfig) o;
        return Objects.equals(defaultConnectorBaseURI, userConfig.defaultConnectorBaseURI) && Objects.equals(defaultMandantId, userConfig.defaultMandantId) && Objects.equals(defaultWorkplaceId, userConfig.defaultWorkplaceId) && Objects.equals(defaultClientSystemId, userConfig.defaultClientSystemId) && Objects.equals(defaultUserId, userConfig.defaultUserId) && Objects.equals(defaultTvMode, userConfig.defaultTvMode) && Objects.equals(defaultConnectorVersion, userConfig.defaultConnectorVersion) && Objects.equals(defaultPruefnummer, userConfig.defaultPruefnummer) && Objects.equals(defaultMuster16TemplateProfile, userConfig.defaultMuster16TemplateProfile) && Objects.equals(configurations, userConfig.configurations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(defaultConnectorBaseURI, defaultMandantId, defaultWorkplaceId, defaultClientSystemId, defaultUserId, defaultTvMode, defaultConnectorVersion, defaultPruefnummer, defaultMuster16TemplateProfile, configurations);
    }
}
