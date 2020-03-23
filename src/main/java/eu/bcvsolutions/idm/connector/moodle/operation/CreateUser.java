package eu.bcvsolutions.idm.connector.moodle.operation;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.objects.Attribute;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;

import eu.bcvsolutions.idm.connector.moodle.MoodleConfiguration;
import eu.bcvsolutions.idm.connector.moodle.communication.Connection;
import eu.bcvsolutions.idm.connector.moodle.enmus.UserAttrNameEnum;
import eu.bcvsolutions.idm.connector.moodle.model.ResponseUser;
import eu.bcvsolutions.idm.connector.moodle.util.MoodleUtils;

/**
 * @author Roman Kucera
 * Class which perform creating of user
 */
public class CreateUser {
	private final String createUserFunction = "core_user_create_users";

	private static final Log LOG = Log.getLog(CreateUser.class);

	private MoodleConfiguration configuration;

	public CreateUser(MoodleConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Create user with specific attributes which are send as parameter
	 *
	 * @param createAttributes
	 * @return
	 * @throws URISyntaxException
	 */
	public ResponseUser createUser(Set<Attribute> createAttributes) throws URISyntaxException {
		URIBuilder uriBuilder = MoodleUtils.buildBaseUrl(configuration);
		uriBuilder.addParameter("wsfunction", createUserFunction);
		uriBuilder.addParameter("moodlewsrestformat", "json");

		// Transform parameters into Map which we can use in request later
		Map<String, Object> parameters = new HashMap<>();
		createAttributes.forEach(attribute -> {
			// Ignored attribute with role, because roles are handled in update
			if (attribute.getName().equals(UserAttrNameEnum.roles.toString())) {
				return;
			}
			StringBuilder key = new StringBuilder();
			if (attribute.getName().equals("__PASSWORD__")) {
				key.append("users[0][").append(UserAttrNameEnum.password.toString()).append("]");
				parameters.put(key.toString(), MoodleUtils.getPassword((GuardedString) attribute.getValue().get(0)));
			} else if (!attribute.getName().equals("__NAME__")) {
				key.append("users[0][").append(attribute.getName()).append("]");
				List<Object> values = attribute.getValue();
				if (values != null && values.size() == 1) {
					parameters.put(key.toString(), values.get(0));
				} else {
					parameters.put(key.toString(), "");
				}
			}
		});

		HttpResponse<String> response = Connection.post(uriBuilder.build().toString(), parameters);

		if (response.getStatus() == HttpStatus.SC_OK) {
			ObjectMapper mapper = new ObjectMapper();
			List<ResponseUser> responseUsers = null;
			try {
				responseUsers = mapper.readValue(response.getBody(), new TypeReference<List<ResponseUser>>() {
				});
			} catch (JsonProcessingException e) {
				LOG.error("Error during parsing user response, we will try to parse error now", e);
				throw Connection.handleError(response, "create user");
			}

			if (responseUsers.size() == 1) {
				return responseUsers.get(0);
			} else {
				LOG.error("Found no or more then 1 result");
			}
		} else {
			throw Connection.handleError(response, "create user");
		}
		return null;
	}
}
