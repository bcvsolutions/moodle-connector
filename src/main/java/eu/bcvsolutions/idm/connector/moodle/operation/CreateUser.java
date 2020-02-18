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
import eu.bcvsolutions.idm.connector.moodle.enmus.AttrNameEnum;
import eu.bcvsolutions.idm.connector.moodle.model.ResponseUser;
import eu.bcvsolutions.idm.connector.moodle.util.MoodleUtils;

/**
 * @author Roman Kucera
 */
public class CreateUser {
	private final String getUserFunction = "core_user_create_users";

	private static final Log LOG = Log.getLog(CreateUser.class);

	private MoodleConfiguration configuration;
	private MoodleUtils moodleUtils;
	private Connection connection;

	public CreateUser(MoodleConfiguration configuration) {
		this.configuration = configuration;
		moodleUtils = new MoodleUtils();
		connection = new Connection();
	}

	public ResponseUser createUser(Set<Attribute> createAttributes) throws URISyntaxException {
		URIBuilder uriBuilder = moodleUtils.buildBaseUrl(configuration);
		uriBuilder.addParameter("wsfunction", getUserFunction);
		uriBuilder.addParameter("moodlewsrestformat", "json");

		Map<String, Object> parameters = new HashMap<>();
		createAttributes.forEach(attribute -> {
			StringBuilder key = new StringBuilder();
			if (attribute.getName().equals("__PASSWORD__")) {
				key.append("users[0][").append(AttrNameEnum.password.toString()).append("]");
				parameters.put(key.toString(), moodleUtils.getPassword((GuardedString) attribute.getValue().get(0)));
			} else if (!attribute.getName().equals("__NAME__")){
				key.append("users[0][").append(attribute.getName()).append("]");
				List<Object> values = attribute.getValue();
				if (values.size() == 1) {
					parameters.put(key.toString(), values.get(0));
				}
			}
		});

		HttpResponse<String> response = connection.post(uriBuilder.build().toString(), parameters);

		if (response.getStatus() == HttpStatus.SC_OK) {
			ObjectMapper mapper = new ObjectMapper();
			List<ResponseUser> responseUsers = null;
			try {
				responseUsers = mapper.readValue(response.getBody(), new TypeReference<List<ResponseUser>>(){});
			} catch (JsonProcessingException e) {
				LOG.error("Error during parsing user response, we will try to parse error now", e);
				throw connection.handleError(response, "create user");
			}

			if (responseUsers.size() == 1) {
				return responseUsers.get(0);
			} else {
				LOG.error("Found no or more then 1 result");
			}
		} else {
			throw connection.handleError(response, "create user");
		}
		return null;
	}
}
