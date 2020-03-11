package eu.bcvsolutions.idm.connector.moodle.operation;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.identityconnectors.common.logging.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;

import eu.bcvsolutions.idm.connector.moodle.MoodleConfiguration;
import eu.bcvsolutions.idm.connector.moodle.communication.Connection;
import eu.bcvsolutions.idm.connector.moodle.model.ResponseAllGroups;
import eu.bcvsolutions.idm.connector.moodle.model.ResponseGroup;
import eu.bcvsolutions.idm.connector.moodle.util.MoodleUtils;

/**
 * @author Roman Kucera
 */
public class GetGroup {
	private final String getAllGroupsFunction = "core_cohort_search_cohorts";
	private final String getOneGroupFunction = "core_cohort_get_cohorts";

	private static final Log LOG = Log.getLog(GetGroup.class);

	private MoodleConfiguration configuration;
	private MoodleUtils moodleUtils;
	private Connection connection;

	public GetGroup(MoodleConfiguration configuration) {
		this.configuration = configuration;
		moodleUtils = new MoodleUtils();
		connection = new Connection();
	}

	public ResponseGroup getGroup(String id) throws URISyntaxException {
		URIBuilder uriBuilder = moodleUtils.buildBaseUrl(configuration);
		uriBuilder.addParameter("wsfunction", getOneGroupFunction);
		uriBuilder.addParameter("moodlewsrestformat", "json");

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("cohortids[0]", Integer.valueOf(id));

		HttpResponse<String> response = connection.post(uriBuilder.build().toString(), parameters);

		if (response.getStatus() == HttpStatus.SC_OK) {
			ObjectMapper mapper = new ObjectMapper();
			List<ResponseGroup> responseGroup = null;
			try {
				responseGroup = mapper.readValue(response.getBody(), new TypeReference<List<ResponseGroup>>(){});
			} catch (JsonProcessingException e) {
				LOG.error("Error during parsing group response, we will try to parse error now", e);
				throw connection.handleError(response, "get group");
			}

			if (!responseGroup.isEmpty()) {
				return responseGroup.get(0);
			} else {
				return null;
			}
		} else {
			throw connection.handleError(response, "get group");
		}
	}

	public List<ResponseGroup> getGroups() throws URISyntaxException {
		URIBuilder uriBuilder = moodleUtils.buildBaseUrl(configuration);
		uriBuilder.addParameter("wsfunction", getAllGroupsFunction);
		uriBuilder.addParameter("moodlewsrestformat", "json");

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("query", null);
		parameters.put("context[contextid]", 0);
		parameters.put("context[contextlevel]", "system");
		parameters.put("context[instanceid]", 0);

		HttpResponse<String> response = connection.post(uriBuilder.build().toString(), parameters);

		if (response.getStatus() == HttpStatus.SC_OK) {
			ObjectMapper mapper = new ObjectMapper();
			ResponseAllGroups responseAllGroups = null;
			try {
				responseAllGroups = mapper.readValue(response.getBody(), ResponseAllGroups.class);
			} catch (JsonProcessingException e) {
				LOG.error("Error during parsing group response, we will try to parse error now", e);
				throw connection.handleError(response, "get groups");
			}

			return responseAllGroups.getCohorts();
		} else {
			throw connection.handleError(response, "get groups");
		}
	}
}
