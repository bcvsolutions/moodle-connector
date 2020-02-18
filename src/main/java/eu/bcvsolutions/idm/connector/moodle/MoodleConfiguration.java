package eu.bcvsolutions.idm.connector.moodle;

import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.spi.AbstractConfiguration;
import org.identityconnectors.framework.spi.ConfigurationProperty;

public class MoodleConfiguration extends AbstractConfiguration {

    private String endpoint;
    private String user;
    private GuardedString token;

    @ConfigurationProperty(displayMessageKey = "moodle.connector.endpoint.display",
            helpMessageKey = "moodle.connector.endpoint.help", required = true, order = 1)
    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    @ConfigurationProperty(displayMessageKey = "moodle.connector.user.display",
            helpMessageKey = "moodle.connector.user.help", required = true, order = 2)
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @ConfigurationProperty(displayMessageKey = "moodle.connector.password.display",
            helpMessageKey = "moodle.connector.password.help", confidential = true, required = true, order = 3)
    public GuardedString getToken() {
        return token;
    }

    public void setToken(GuardedString token) {
        this.token = token;
    }

    @Override
    public void validate() {
        if (StringUtil.isBlank(endpoint)) {
            throw new ConfigurationException("Endpoint must not be blank!");
        }
        if (StringUtil.isBlank(user)) {
            throw new ConfigurationException("User must not be blank!");
        }
        if (getToken() == null) {
            throw new ConfigurationException("Password must not be blank!");
        }
    }

}
