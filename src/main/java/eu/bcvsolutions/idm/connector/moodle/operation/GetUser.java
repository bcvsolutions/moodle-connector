package eu.bcvsolutions.idm.connector.moodle.operation;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;

import eu.bcvsolutions.idm.connector.moodle.MoodleConfiguration;
import eu.bcvsolutions.idm.connector.moodle.communication.Connection;
import eu.bcvsolutions.idm.connector.moodle.enmus.AttrNameEnum;
import eu.bcvsolutions.idm.connector.moodle.model.ResponseAllUsers;
import eu.bcvsolutions.idm.connector.moodle.model.ResponseUser;
import eu.bcvsolutions.idm.connector.moodle.util.MoodleUtils;

/**
 * @author Roman Kucera
 */
public class GetUser {
	private final String getUserFunction = "core_user_get_users_by_field";
	private final String getAllUserFunction = "tool_lp_search_users";

	private static final Log LOG = Log.getLog(GetUser.class);

	private MoodleConfiguration configuration;
	private MoodleUtils moodleUtils;
	private Connection connection;

	public GetUser(MoodleConfiguration configuration) {
		this.configuration = configuration;
		moodleUtils = new MoodleUtils();
		connection = new Connection();
	}

	public ResponseUser getUserByField(String value, String field) throws URISyntaxException {
		URIBuilder uriBuilder = moodleUtils.buildBaseUrl(configuration);
		uriBuilder.addParameter("wsfunction", getUserFunction);
		uriBuilder.addParameter("field", field);
		uriBuilder.addParameter("values[0]", value);
		uriBuilder.addParameter("moodlewsrestformat", "json");

		HttpResponse<String> response = connection.get(uriBuilder.build().toString());

		if (response.getStatus() == HttpStatus.SC_OK) {
			ObjectMapper mapper = new ObjectMapper();
			List<ResponseUser> responseUsers = null;
			try {
				responseUsers = mapper.readValue(response.getBody(), new TypeReference<List<ResponseUser>>(){});
			} catch (JsonProcessingException e) {
				LOG.error("Error during parsing user response, we will try to parse error now", e);
				throw connection.handleError(response, "get user");
			}
			if (!responseUsers.isEmpty()) {
				return responseUsers.get(0);
			} else {
				return null;
			}
		} else {
			throw connection.handleError(response, "get user");
		}
	}

	public List<ResponseUser> getUsers() throws URISyntaxException {
		URIBuilder uriBuilder = moodleUtils.buildBaseUrl(configuration);
		uriBuilder.addParameter("wsfunction", getAllUserFunction);
		uriBuilder.addParameter("query", null);
		uriBuilder.addParameter("capability", null);
		uriBuilder.addParameter("moodlewsrestformat", "json");

		HttpResponse<String> response = connection.get(uriBuilder.build().toString());

		if (response.getStatus() == HttpStatus.SC_OK) {
			ObjectMapper mapper = new ObjectMapper();
			ResponseAllUsers responseAllUsers;
			try {
				responseAllUsers = mapper.readValue(response.getBody(), ResponseAllUsers.class);
			} catch (JsonProcessingException e) {
				LOG.error("Error during parsing user response, we will try to parse error now", e);
				throw connection.handleError(response, "get user");
			}

			List<ResponseUser> detailedUsers = new ArrayList<>();

			responseAllUsers.getUsers().forEach(responseUser -> {
				try {
					ResponseUser userDetail = getUserByField(String.valueOf(responseUser.getId()), AttrNameEnum.id.toString());
					detailedUsers.add(userDetail);
				} catch (URISyntaxException e) {
					throw new ConnectorException("Error during preparing request URL:", e);
				}
			});

			return detailedUsers;
		} else {
			throw connection.handleError(response, "get users");
		}
	}
}
