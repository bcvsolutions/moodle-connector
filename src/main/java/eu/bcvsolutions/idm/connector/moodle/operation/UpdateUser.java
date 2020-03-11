package eu.bcvsolutions.idm.connector.moodle.operation;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.objects.Attribute;

import com.mashape.unirest.http.HttpResponse;

import eu.bcvsolutions.idm.connector.moodle.MoodleConfiguration;
import eu.bcvsolutions.idm.connector.moodle.communication.Connection;
import eu.bcvsolutions.idm.connector.moodle.enmus.GroupAttrNameEnum;
import eu.bcvsolutions.idm.connector.moodle.enmus.UserAttrNameEnum;
import eu.bcvsolutions.idm.connector.moodle.model.ResponseUser;
import eu.bcvsolutions.idm.connector.moodle.util.MoodleUtils;

/**
 * @author Roman Kucera
 */
public class UpdateUser {
	private final String updateUserFunction = "core_user_update_users";
	private final String addUserToGroupFunction = "core_cohort_add_cohort_members";
	private final String deleteUserToGroupFunction = "core_cohort_delete_cohort_members";

	private static final Log LOG = Log.getLog(UpdateUser.class);

	private MoodleConfiguration configuration;
	private MoodleUtils moodleUtils;
	private Connection connection;

	public UpdateUser(MoodleConfiguration configuration) {
		this.configuration = configuration;
		moodleUtils = new MoodleUtils();
		connection = new Connection();
	}

	public void updateUser(String id, Set<Attribute> updateAttributes) throws URISyntaxException {
		URIBuilder uriBuilder = moodleUtils.buildBaseUrl(configuration);
		uriBuilder.addParameter("wsfunction", updateUserFunction);
		uriBuilder.addParameter("moodlewsrestformat", "json");

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("users[0][id]", Integer.valueOf(id));
		updateAttributes.forEach(attribute -> {
			if (attribute.getName().equals(UserAttrNameEnum.roles.toString())) {
				return;
			}
			StringBuilder key = new StringBuilder();
			if (attribute.getName().equals("__PASSWORD__")) {
				key.append("users[0][").append(UserAttrNameEnum.password.toString()).append("]");
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

		if (response.getStatus() != HttpStatus.SC_OK || !response.getBody().equals("null")) {
			throw connection.handleError(response, "delete");
		}
	}

	public void updateGroups(String id, List<Integer> roles) throws URISyntaxException {
		// Get all users roles which he has on end system
		GetUser getUser = new GetUser(configuration);
		ResponseUser user = getUser.getUserByField(id, UserAttrNameEnum.id.toString());
		List<Integer> rolesOnSystem = user.getRoles();

		List<Integer> toAdd = new ArrayList<>(roles);
		List<Integer> toRemove = new ArrayList<>(rolesOnSystem);

		toAdd.removeAll(rolesOnSystem);
		toRemove.removeAll(roles);

		updateRoles(id, toAdd, addUserToGroupFunction);
		updateRoles(id, toRemove, deleteUserToGroupFunction);
	}

	private void updateRoles(String id, List<Integer> roles, String function) {
		roles.forEach(role -> {
			URIBuilder uriBuilder;
			try {
				uriBuilder = moodleUtils.buildBaseUrl(configuration);
				uriBuilder.addParameter("wsfunction", function);
				uriBuilder.addParameter("moodlewsrestformat", "json");

				Map<String, Object> parameters = new HashMap<>();
				parameters.put("members[0][cohorttype][type]", GroupAttrNameEnum.id.toString());
				parameters.put("members[0][cohorttype][value]", role);
				parameters.put("members[0][usertype][type]", UserAttrNameEnum.id.toString());
				parameters.put("members[0][usertype][value]", Integer.valueOf(id));

				HttpResponse<String> response = connection.post(uriBuilder.build().toString(), parameters);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		});
	}
}
