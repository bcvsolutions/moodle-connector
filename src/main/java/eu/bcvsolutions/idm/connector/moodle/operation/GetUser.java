package eu.bcvsolutions.idm.connector.moodle.operation;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import eu.bcvsolutions.idm.connector.moodle.enmus.UserAttrNameEnum;
import eu.bcvsolutions.idm.connector.moodle.model.ResponseAllUsers;
import eu.bcvsolutions.idm.connector.moodle.model.ResponseGroup;
import eu.bcvsolutions.idm.connector.moodle.model.ResponseGroupUsers;
import eu.bcvsolutions.idm.connector.moodle.model.ResponseUser;
import eu.bcvsolutions.idm.connector.moodle.util.MoodleUtils;

/**
 * @author Roman Kucera
 */
public class GetUser {
	private final String getUserFunction = "core_user_get_users_by_field";
	private final String getAllUserFunction = "tool_lp_search_users";
	private final String getUserInGroup = "core_cohort_get_cohort_members";

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
				responseUsers = mapper.readValue(response.getBody(), new TypeReference<List<ResponseUser>>() {
				});
			} catch (JsonProcessingException e) {
				LOG.error("Error during parsing user response, we will try to parse error now", e);
				throw connection.handleError(response, "get user");
			}
			if (!responseUsers.isEmpty()) {
				ResponseUser responseUser = responseUsers.get(0);
				List<Integer> roles = new ArrayList<>();

				getUserRoles(value, roles);

				responseUser.setRoles(roles);
				return responseUser;
			} else {
				return null;
			}
		} else {
			throw connection.handleError(response, "get user");
		}
	}

	private void getUserRoles(String value, List<Integer> roles) throws URISyntaxException {
		URIBuilder uriBuilderGroup = moodleUtils.buildBaseUrl(configuration);
		uriBuilderGroup.addParameter("wsfunction", getUserInGroup);
		uriBuilderGroup.addParameter("moodlewsrestformat", "json");

		GetGroup getGroup = new GetGroup(configuration);
		List<ResponseGroup> groups = getGroup.getGroups();
		groups.forEach(responseGroup -> {
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("cohortids[0]", responseGroup.getId());

			try {
				HttpResponse<String> groupResponse = connection.post(uriBuilderGroup.build().toString(), parameters);
				if (groupResponse.getStatus() == HttpStatus.SC_OK) {
					getUserGroupsFromResponse(value, roles, groupResponse);
				} else {
					throw connection.handleError(groupResponse, "get group users");
				}
			} catch (URISyntaxException e) {
				LOG.error("Error during building uri for getting groups users", e);
			}
		});
	}

	private void getUserGroupsFromResponse(String value, List<Integer> roles, HttpResponse<String> groupResponse) {
		ObjectMapper mapperGroup = new ObjectMapper();
		ResponseGroupUsers group;
		try {
			group = mapperGroup.readValue(groupResponse.getBody(), ResponseGroupUsers.class);
			if (group.getUserids() != null && !group.getUserids().isEmpty() && group.getUserids().contains(Integer.valueOf(value))) {
				roles.add(group.getCohortid());
			}
		} catch (JsonProcessingException e) {
			LOG.error("Error during parsing group users response, we will try to parse error now", e);
			throw connection.handleError(groupResponse, "get group users");
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
					ResponseUser userDetail = getUserByField(String.valueOf(responseUser.getId()), UserAttrNameEnum.id.toString());
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
