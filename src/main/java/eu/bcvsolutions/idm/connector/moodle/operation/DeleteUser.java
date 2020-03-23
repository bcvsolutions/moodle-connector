package eu.bcvsolutions.idm.connector.moodle.operation;

import java.net.URISyntaxException;

import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;

import com.mashape.unirest.http.HttpResponse;

import eu.bcvsolutions.idm.connector.moodle.MoodleConfiguration;
import eu.bcvsolutions.idm.connector.moodle.communication.Connection;
import eu.bcvsolutions.idm.connector.moodle.util.MoodleUtils;

/**
 * @author Roman Kucera
 * Class which delete user
 */
public class DeleteUser {
	private final String deleteUserFunction = "core_user_delete_users";

	private MoodleConfiguration configuration;

	public DeleteUser(MoodleConfiguration configuration) {
		this.configuration = configuration;
	}

	public void deleteUser(String id) throws URISyntaxException {
		URIBuilder uriBuilder = MoodleUtils.buildBaseUrl(configuration);
		uriBuilder.addParameter("wsfunction", deleteUserFunction);
		uriBuilder.addParameter("userids[0]", id);
		uriBuilder.addParameter("moodlewsrestformat", "json");

		HttpResponse<String> response = Connection.get(uriBuilder.build().toString());

		if (response.getStatus() != HttpStatus.SC_OK || !response.getBody().equals("null")) {
			throw Connection.handleError(response, "delete user");
		}
	}
}
